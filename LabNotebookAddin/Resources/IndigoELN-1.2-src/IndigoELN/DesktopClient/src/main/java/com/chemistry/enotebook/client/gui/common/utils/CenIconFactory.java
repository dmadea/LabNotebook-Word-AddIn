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
package com.chemistry.enotebook.client.gui.common.utils;

import javax.swing.*;
import java.awt.*;

public class CenIconFactory {
	public static class MenuBar {
		public final static String NEW_PAGE = "icons/file_obj.gif";
		public final static String NEW_MEDCHEM_PAGE = "icons/file_obj.gif";
		public final static String NEW_PARALLEL_PAGE = "icons/files_obj.gif";
		public final static String NEW_CONCEPT_PAGE = "icons/concept_obj.gif";
		public final static String OPEN_PAGE = "icons/open_page.gif";
		public final static String SAVE_PAGE = "icons/save_edit.gif";
		public final static String SAVE_PAGE_BACKGROUND = "icons/save_edit_bkgd.gif";
		public final static String SAVE_ALL = "icons/saveall_edit.gif";
		public final static String SAVE_AS = "icons/saveas_edit.gif";
		public final static String PRINT = "icons/print_edit.gif";
		public final static String PRINT_PREVIEW = "icons/print_preview.gif";
		public final static String EDIT_UNDO = "icons/undo_edit.gif";
		public final static String EDIT_CUT = "icons/cut_edit.gif";
		public final static String EDIT_COPY = "icons/copy_edit.gif";
		public final static String EDIT_PASTE = "icons/paste_edit.gif";
		public final static String REFRESH_PAGE = "icons/refresh_nav.gif";
		public final static String HELP = "icons/help_contents.gif";
		public final static String SEARCH = "icons/search.gif";
		public final static String OPTIONS = "icons/options.gif";
		public final static String CASCADE = "icons/cascade.gif";
		public final static String TILE = "icons/tile.gif";
		public final static String CLOSE_ALL = "icons/close_all.gif";

	}

	public static class General {
		public final static String HEALTH_GOOD = "images/health_good.gif";
		public final static String HEALTH_QUESTIONABLE = "images/health_maybe.gif";
		public final static String HEALTH_CRITICAL = "images/health_critical.gif";
		public final static String ABOUT = "images/about.gif";
		public final static String APPLICATION = "icons/application.png";
		public final static String APPLICATION_ICON = "icons/application_icon.png";
		public final static String MAXIMIZE = "icons/maximize.gif";
		public final static String RESTORE = "icons/restore.gif";
		public final static String DROP_DOWN = "icons/drop_down.gif";
		public final static String UNKNOWN_DOCUMENT = "icons/unknown_document.gif";
		public final static String CHECK_MARK = "icons/check.gif";
		public final static String UP_ARROW = "icons/prev_nav.gif";
		public final static String DOWN_ARROW = "icons/next_nav.gif";
		public final static String SELECTED_CHECKBOX = "icons/selectedCheckBox.gif";
		public final static String DESELECTED_CHECKBOX = "icons/deselectedCheckBox.gif";
	}

	public static class ContainerTree {
		public final static String MONO_PLATE = "icons/plate.gif";
		public final static String PROD_PLATE = "icons/plate.gif";
		public final static String OPEN_FOLDER = "icons/open_folder.gif";
		public final static String CLOSE_FOLDER = "icons/close_folder.gif";
		public final static String CONTAINER = "icons/container.gif";
	}

	public static class SpeedBar {
		public final static String SITE = "icons/site.gif";
		public final static String WORLD = "icons/world.gif";
		public final static String USER = "icons/user.gif";
		public final static String GROUP = "icons/group.gif";
		public final static String PAGES = "icons/pages.gif";
		public final static String NEW_CONCEPT = "icons/new_concept.gif";
		public final static String NEW_SINGLETON = "icons/new_singleton.gif";
		public final static String NEW_PARALLEL = "icons/new_parallel.gif";
		public final static String COPY = "icons/copy_page.gif";
		public final static String DELETE = "icons/remove_cor.gif";
		public final static String CONTENTS = "icons/contents.gif";
		public final static String BOOK_STATUS_OPEN = "icons/book_open.gif";
		public final static String BOOK_STATUS_CLOSED = "icons/book_closed.gif";
		
		public final static String PS_CONCEPT_OPEN = "icons/ps_concept_open.gif";
		public final static String PS_CONCEPT_COMPLETE = "icons/ps_concept_complete.gif";
		public final static String PS_CONCEPT_SIGNING = "icons/ps_concept_signing.gif";
		public final static String PS_CONCEPT_SIGN_ERROR = "icons/ps_concept_sign_error.gif";
		public final static String PS_CONCEPT_SIGNED = "icons/ps_concept_signed.gif";
		public final static String PS_CONCEPT_ARCHIVING = "icons/ps_concept_archiving.gif";
		public final static String PS_CONCEPT_ARCHIVED = "icons/ps_concept_archived.gif";
		public final static String PS_CONCEPT_ARCHIVE_ERROR = "icons/ps_concept_archive_error.gif";
		
		public final static String PS_SINGLETON_OPEN = "icons/ps_singleton_open.gif";
		public final static String PS_SINGLETON_COMPLETE = "icons/ps_singleton_complete.gif";
		public final static String PS_SINGLETON_SIGNING = "icons/ps_singleton_signing.gif";
		public final static String PS_SINGLETON_SIGN_ERROR = "icons/ps_singleton_sign_error.gif";
		public final static String PS_SINGLETON_SIGNED = "icons/ps_singleton_signed.gif";
		public final static String PS_SINGLETON_ARCHIVING = "icons/ps_singleton_archiving.gif";
		public final static String PS_SINGLETON_ARCHIVED = "icons/ps_singleton_archived.gif";
		public final static String PS_SINGLETON_ARCHIVE_ERROR = "icons/ps_singleton_archive_error.gif";
		
		public final static String PS_PARALLEL_OPEN = "icons/ps_parallel_open.gif";
		public final static String PS_PARALLEL_COMPLETE = "icons/ps_parallel_complete.gif";
		public final static String PS_PARALLEL_SIGNING = "icons/ps_parallel_signing.gif";
		public final static String PS_PARALLEL_SIGN_ERROR = "icons/ps_parallel_sign_error.gif";
		public final static String PS_PARALLEL_SIGNED = "icons/ps_parallel_signed.gif";
		public final static String PS_PARALLEL_ARCHIVING = "icons/ps_parallel_archiving.gif";
		public final static String PS_PARALLEL_ARCHIVED = "icons/ps_parallel_archived.gif";
		public final static String PS_PARALLEL_ARCHIVE_ERROR = "icons/ps_parallel_archive_error.gif";
		
		public final static String VIEW_ARCHIVE = "icons/view_archive.gif";
		
		public final static String SPID = "icons/spid.gif";
		public final static String SPID_LOCKED = "icons/spid_locked.gif";
	}

	public static ImageIcon getImageIcon(String name) {
		if (name != null) {
			java.net.URL imgURL = CenIconFactory.class.getClassLoader().getResource(name);
			if (imgURL != null) {
				return new ImageIcon(imgURL);
			} else {
				return new ImageIcon(name);
			}
		} else
			return null;
	}

	public static ImageIcon getComplianceImageIcon(String name, boolean inCompliance) 
	{
        if (name != null)
        {
        	java.net.URL imgURL = CenIconFactory.class.getClassLoader().getResource(name);
        	if (imgURL != null) {
        	    return new CeNComplianceIcon(imgURL, inCompliance);
        	} else {
        	    return new CeNComplianceIcon(name, inCompliance);
        	}
        }
        else
            return null;
    }

	public static Image getImage(String name) {
		if (name != null) {
			java.net.URL imgURL = CenIconFactory.class.getClassLoader().getResource(name);
			if (imgURL != null) {
				return Toolkit.getDefaultToolkit().createImage(imgURL);
			} else {
				return Toolkit.getDefaultToolkit().createImage(name);
			}
		} else
			return null;
	}
	
	private static Image companyLogo;
	
	public static synchronized Image getCompanyLogo() {
		if (companyLogo == null) {
			companyLogo = new ImageIcon(getImageIcon("images/companyLogo.png").getImage().getScaledInstance(60, 29, Image.SCALE_SMOOTH)).getImage();
		}
		return companyLogo;
	}
}
