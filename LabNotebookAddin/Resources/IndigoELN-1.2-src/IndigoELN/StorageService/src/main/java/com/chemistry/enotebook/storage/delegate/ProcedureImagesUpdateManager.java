/****************************************************************************
 * Copyright (C) 2009-2015 EPAM Systems
 * 
 * This file is part of Indigo ELN.
 * 
 * This file may be distributed and/or modified under the terms of the
 * GNU General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.GPL included in the
 * packaging of this file.
 * 
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/
package com.chemistry.enotebook.storage.delegate;

import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProcedureImage;
import com.chemistry.enotebook.experiment.utils.GUIDUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This manager is responsible for updating text of experiment procedure. The main idea
 * is to store local images inserted to procedure area to database. So some operations
 * should be done before saving experiment (read new images data, encode text of procedure)
 * and after loading (creating local images and setting reference to them in procedure)
 * 
 * 
 */
public class ProcedureImagesUpdateManager {
	private static final Log log = LogFactory.getLog(ProcedureImagesUpdateManager.class);
	
	private static String procedureImagesDirectory = CeNConstants.getApplicationDirectory() + File.separator + "ProcedureImages";

	private static final String IMG_REGEX = "<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";

	////////////////////////////////////////////////////////////////////////
	//save

	// 1. Reading all image paths from procedure
	private static Set<String> getPathsOfAllImages(String procedure) {
		Set<String> imgPaths = new HashSet<String>();

		Pattern pattern = Pattern.compile(IMG_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(procedure);

		while (matcher.find()) {
			imgPaths.add(matcher.group(1));
		}

		return imgPaths;
	}

	private static void prepareNewImagesUploading(List<ProcedureImage> procedureImages, Set<String> localPaths) {
		if (procedureImages == null || localPaths == null) {
			return;
		}
		
		List<String> loadedImagesLocalPaths = new ArrayList<String>(procedureImages.size());
		
		for (ProcedureImage procedureImage : procedureImages) { 
			loadedImagesLocalPaths.add(procedureImage.getLocalPath());
		}

		// find all new images
		List<String> newImagesPaths = new ArrayList<String>(localPaths);  
		newImagesPaths.removeAll(loadedImagesLocalPaths);

		if (newImagesPaths.size() == 0) {
			// there is nothing to upload
			return;
		}

		// read image data
		for (String localPath : newImagesPaths) { 
			if (localPath == null) {
				continue;
			}
			
			try {
				// Generate a key for new images
				String imageKey = GUIDUtil.generateGUID(localPath);
				String imageType = getImageType(localPath);
				ProcedureImage newImage = new ProcedureImage(imageKey, localPath, imageType, ProcedureImage.NEW);
				
				// read image data
				byte[] image = readFile(localPath);
				newImage.setImageData(image);
				
				// save image object
				procedureImages.add(newImage);
			} catch (Exception e) {
				log.error("Failed reading image with path '" + localPath + "'", e);
			}
		}
	}

    private static String getImageType(String path) throws IOException {
        URL url;

        try {
            url = new URL(path);
        } catch (Exception e) {
            url = new File(path).toURI().toURL();
        }

        InputStream is = url.openConnection().getInputStream();
        InputStream bis = new BufferedInputStream(is);
        ImageInputStream iis = ImageIO.createImageInputStream(bis);

        try {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            if (readers.hasNext()) {
                return StringUtils.lowerCase(readers.next().getFormatName());
            }

            return null;
        } finally {
            iis.close();
            bis.close();
            is.close();
        }
    }

	private static byte[] readFile(String path) throws IOException {
		URL url;
		
		try {
			url = new URL(path);			
		} catch (Exception e) {
			url = new File(path).toURI().toURL();
		}

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        InputStream is = url.openConnection().getInputStream();
        InputStream bis = new BufferedInputStream(is);

        try {
            int b;
            while ((b = bis.read()) != -1) {
                baos.write(b);
            }

            return baos.toByteArray();
        } finally {
            bis.close();
            is.close();
            baos.close();
        }
    }
	
	private static void prepareRemovedImagesDeleting(List<ProcedureImage> procedureImages, Set<String> localPaths) {
		if (procedureImages == null || localPaths == null) {
			return;
		}
		
		//  Delete removed images from the images directory and update localPathsToProcedureDBImageIDsMap
		for (ProcedureImage procedureImage : procedureImages) {
			String localPath = procedureImage.getLocalPath();
			
			if (localPaths.contains(localPath)) {
				continue;
			}
			// if the local path of an image starts with image directory path then delete a corresponding file
			if (localPath != null && localPath.startsWith(procedureImagesDirectory)) {
				File imageFile = new File(localPath);
				if (imageFile.exists()) {
					boolean success = imageFile.delete();
					if (success && log.isInfoEnabled())
						log.info("Deleting image file successfully: " + imageFile.getName());
				}
			}
			
			procedureImage.setStoreState(ProcedureImage.DELETED);
		}
	}

	public static void updateProcedureOnSave(NotebookPageModel pageModel) {
		if (pageModel == null) {
			return;
		}
		
		List<ProcedureImage> procedureImages = pageModel.getPageHeader().getProcedureImages();
		// in the case when the experiment has just been created
		if (procedureImages == null) {
			procedureImages = new ArrayList<ProcedureImage>();
			pageModel.getPageHeader().setProcedureImages(procedureImages);
		}
		
		// update procedure images states. NEW images have been already saved to and DELETED have been already removed from database
		List<ProcedureImage> toDelete = new ArrayList<ProcedureImage>();
		for (ProcedureImage procedureImage : procedureImages) { 
			String storeState = procedureImage.getStoreState();
			if (storeState != null && storeState.equals(ProcedureImage.NEW)) {
				procedureImage.setStoreState(ProcedureImage.STORED);
				continue;
			}
			if (storeState != null && storeState.equals(ProcedureImage.DELETED)) {
				toDelete.add(procedureImage);
			}
		}
		procedureImages.removeAll(toDelete);
		
		// set - for repetition elimination
		Set<String> pathsOfAllImages = getPathsOfAllImages(pageModel.getProcedure());
		
		// Updating the set of images in the DB (insert new, delete deleted images)
		prepareRemovedImagesDeleting(procedureImages, pathsOfAllImages);
		prepareNewImagesUploading(procedureImages, pathsOfAllImages);
		
		// Substitute keys of procedure images for their local paths
		encodeProcedure(pageModel);
	}


	///////////////////////////////////////////////////////////////////
	// load

	private static boolean validateDirectory(String path) {
		boolean result = false;
		if (path != null && path.length() > 0) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (dir.mkdirs())
					result = true;
			} else
				result = true;
		}
		return result;
	}

	public static void updateProcedureOnLoad(NotebookPageModel pageModel) {
		if (pageModel == null) {
			return;
		}
		
		// select
		List<ProcedureImage> allImages = pageModel.getPageHeader().getProcedureImages();
	
		if (allImages != null) {
			// Specify local paths for procedure images 
			for (ProcedureImage image : allImages) {
				String imagePath = procedureImagesDirectory + File.separator + pageModel.getNotebookRefAsString() + "_" + image.getKey() + "." + image.getImageType();
				image.setLocalPath(imagePath);
			}
		
			// Create local files of images
			validateDirectory(procedureImagesDirectory);
		
			for (ProcedureImage image : allImages) {
				try {
					OutputStream out = new BufferedOutputStream(new FileOutputStream(image.getLocalPath()));
					out.write(image.getImageData());
					out.close();
				} catch (FileNotFoundException e) {
					log.error("Failed creating local procedure images in " + procedureImagesDirectory, e);
				} catch (IOException e) {
					log.error("Failed creating local procedure images in " + procedureImagesDirectory, e);			
				}
			}
		
			// Substitute new local paths for image keys
			decodeProcedure(pageModel);		
		}
	}

	// encode image tags in procedure so it is possible to re-establish correspondence 
	// between stored images and references to them on another computer
	private static void encodeProcedure(NotebookPageModel pageModel) {
		if (pageModel == null) {
			return;
		}
		
		List<ProcedureImage> allImages = pageModel.getPageHeader().getProcedureImages();
		
		if (allImages != null) {
			String procedure = pageModel.getProcedure();
		
			for (ProcedureImage image : allImages) { 
				if (image.getLocalPath() == null) {
					continue;
				}
				String localPath = image.getLocalPath().replaceAll("\\\\", "\\\\\\\\");
				procedure = procedure.replaceAll(localPath, image.getKey());
			}
		
			pageModel.setProcedure(procedure);
		}
	}

	// decode image tags in procedure. Substitute new local paths for image keys
	private static void decodeProcedure(NotebookPageModel pageModel) {
		if (pageModel == null) {
			return;
		}
		
		List<ProcedureImage> allImages = pageModel.getPageHeader().getProcedureImages();
		
		if (allImages != null) {
			String procedure = pageModel.getProcedure();
			
			for (ProcedureImage image : allImages) {
				String localPath = image.getLocalPath().replaceAll("\\\\", "\\\\\\\\");
				procedure = procedure.replaceAll(image.getKey(), localPath);
			}

			pageModel.setProcedure(procedure);
		}
	}
}
