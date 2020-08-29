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
package com.chemistry.enotebook.report.print;

import com.chemistry.enotebook.analyticalservice.delegate.AnalyticalServiceDelegate;
import com.chemistry.enotebook.analyticalservice.exceptions.AnalyticalServiceAccessException;
import com.chemistry.enotebook.domain.AnalysisModel;
import com.chemistry.enotebook.domain.AttachmentModel;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.report.datamanager.ExperimentLoader;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/*
 * Created on 25-May-2005
 */

/**
 * 
 *         <p/>
 *         This appends the Attachments and AnalyticalService files to the PDF file of the experiment sections.
 */
public class AttachmentsManager {
    public static final Log log = LogFactory.getLog(AttachmentsManager.class);
    private PrintOptions.reportType reportType = null;
    private NotebookPageModel experiment = null;
    private boolean includeIpRelatedAttachments = false;
    private boolean includeAnalyticalAttachments = false;
    private boolean includeAllInstruments = false;
    private List includedInstruments = new ArrayList();

    public AttachmentsManager(NotebookPageModel experiment,
                              PrintOptions.reportType type,
                              boolean includeAttachments,
                              boolean includeAnalytical,
                              boolean includeAllInstruments,
                              List includedInstruments) {
        this.experiment = experiment;
        this.includeIpRelatedAttachments = includeAttachments;
        this.includeAnalyticalAttachments = includeAnalytical;
        this.includeAllInstruments = includeAllInstruments;
        this.includedInstruments = includedInstruments;
        this.reportType = type;
    }

    /* (non-Javadoc)
      * @see java.lang.Runnable#run()
      */

    public ByteArrayOutputStream addAttachments(ByteArrayOutputStream ostream) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            if (reportType.equals(PrintOptions.reportType.PARALLEL) || reportType.equals(PrintOptions.reportType.SINGLETON)) {
                if (this.includeAnalyticalAttachments) {
                    AnalyticalServiceDelegate analyticalServiceDelegate = null;
                    try {
                        analyticalServiceDelegate = new AnalyticalServiceDelegate(); //NotebookUser.getInstance().getSessionIdentifier());
                    } catch (AnalyticalServiceAccessException e) {
                        log.error(e);
                        return baos;
                    }

                    //need to retreive AnalyticalService's instrument PDF's
                    List analList = experiment.getAnalysisCache().getAnalyticalList();
                    //Collections.sort(analList);
                    Iterator it = analList.iterator();
                    if (this.includeAllInstruments) {
                        while (it.hasNext()) {
                            AnalysisModel a = (AnalysisModel) it.next();
                            if (a.getFileType() != null && a.getFileType().equals("PDF")) {
                                System.out.println("Getting AnalyticalService file id " + a.getCyberLabFileId());
                                try {
                                    byte[] fileContents = analyticalServiceDelegate.retrieveFileContents(a.getCyberLabDomainId(), a.getCyberLabFileId(), a.getSite());
                                    ByteArrayOutputStream newbaos = this.concat(ostream, fileContents);
                                    if (newbaos != null)
                                        ostream = newbaos;
                                } catch (Throwable e) {
                                    log.error(e);
                                }
                            }
                        }
                    } else {
                        while (it.hasNext()) {
                            AnalysisModel a = (AnalysisModel) it.next();
                            if (a.getFileType() != null && a.getFileType().equals("PDF") && a.getInstrumentType() != null &&
                                    this.includedInstruments.contains(a.getInstrumentType())) {
                                System.out.println("Getting AnalyticalService file id " + a.getCyberLabFileId());
                                try {
                                    byte[] fileContents = analyticalServiceDelegate.retrieveFileContents(a.getCyberLabDomainId(), a.getCyberLabFileId(), a.getSite());
                                    ByteArrayOutputStream newbaos = this.concat(ostream, fileContents);
                                    if (newbaos != null)
                                        ostream = newbaos;
                                } catch (Throwable e) {
                                    log.error(e);
                                }
                            }
                        }
                    }
                }
            }

            // All reports can include ip related attachments
            if (includeIpRelatedAttachments) {
                try {
                    StorageDelegate storageDelegate = ExperimentLoader.getStorageDelegate();
                	//TODO why we need to get NotebookPageModel again? 
                	//NotebookPageModel pageModel = storageDelegate.getNotebookPageExperimentInfo(experiment.getNbRef(), experiment.getSiteCode());
                	NotebookPageModel pageModel = experiment;

                	//need to spool out all ip attachments for this page that are IPP relaed
                    for (AttachmentModel tmpAttach : pageModel.getAttachmentCache().getAttachmentList()){
	                    if (tmpAttach.isIpRelated()) {
	                        //need to get contents if null
	                        if (tmpAttach.getContents() == null) {
	                                AttachmentModel attach = storageDelegate.getNotebookPageExperimentAttachment(tmpAttach.getKey());
	                                tmpAttach.setContents(attach.getContents());
	                        }

	                        byte[] bytes = tmpAttach.getContents();
	                        if (bytes != null) {
	                            ostream = this.concat(ostream, bytes);
	                        }
	                    }
	                }
                } catch (Exception e) {
                    log.error("Error loading attachment.", e);
                }
            }            
        } catch (RuntimeException e) {
            log.error(e);
        }
        baos = ostream;
        return baos;
    }


//	private synchronized boolean concat_PDF(String[] files, String outFile)
//	{
//		boolean result = false;
//
//		try {
//			int pageOffset = 0;
//			ArrayList master = new ArrayList();
//			Document document = null;
//			PdfCopy  writer = null;
//
//			for (int f=0; f < files.length; f++) {
//				// we create a reader for a certain document
//				PdfReader reader = new PdfReader(files[f]);
//				reader.consolidateNamedDestinations();
//
//				// we retrieve the total number of pages
//				int n = reader.getNumberOfPages();
//				List bookmarks = SimpleBookmark.getBookmark(reader);
//				if (bookmarks != null) {
//					if (pageOffset != 0)
//						SimpleBookmark.shiftPageNumbers(bookmarks, pageOffset, null);
//					master.addAll(bookmarks);
//				}
//				pageOffset += n;
//				System.out.println("There are " + n + " page(s) in " + files[f]);
//
//				if (f == 0) {
//					// step 1: creation of a document-object
//					document = new Document(reader.getPageSizeWithRotation(1));
//
//					// step 2: we create a writer that listens to the document
//					writer = new PdfCopy(document, new FileOutputStream(outFile));
//
//					// step 3: we open the document
//					document.open();
//				}
//
//				// step 4: we add content
//				PdfImportedPage page;
//				for (int i=1; i <= n; i++) {
//					page = writer.getImportedPage(reader, i);
//					writer.addPage(page);
//					System.out.println("  Processed page " + i);
//				}
//
//				PRAcroForm form = reader.getAcroForm();
//				if (form != null)
//					writer.copyAcroForm(reader);
//			}
//			if (master.size() > 0)
//				writer.setOutlines(master);
//
//			// step 5: we close the document
//			document.close();
//			result = true;
//		} catch(Exception e) {
//			//CeNErrorHandler.getInstance().logExceptionMsg(null, e);
//		}
//
//		return result;
//	}	

    public ByteArrayOutputStream concat(ByteArrayOutputStream baos, byte[] baToAdd) {
        try {
            PdfReader js1 = new PdfReader(baos.toByteArray());
            PdfReader rd1 = new PdfReader(baToAdd);
            ByteArrayOutputStream newbaos = new ByteArrayOutputStream();
            PdfCopyFields cpy = new PdfCopyFields(newbaos); // new FileOutputStream( "C:/temp/attachments.pdf"));
            cpy.addDocument(js1);
            cpy.addDocument(rd1);
            cpy.close();
            return newbaos;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return baos;
    }


}