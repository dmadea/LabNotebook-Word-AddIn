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
package com.chemistry.enotebook.scheduler.externalcollaborator;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConversionUtil {

    private static String dateFormat = "dd/MMM/yyyy";
    private static long oneDayMilliSec = 86400000;

    public static String getDateInExternalCollaboratorFormat(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        String s = formatter.format(date);
        s = s.toUpperCase();
        return s;

    }

    public static String getNextDayInExternalCollaboratorFormat(Date date) {
        Date nextDate = new Date(date.getTime() + oneDayMilliSec);
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        String s = formatter.format(nextDate);
        s = s.toUpperCase();
        return s;

    }

    public static String getPreviousWeekDayInExternalCollaboratorFormat(Date date) {
        Date nextDate = new Date(date.getTime() - (7 * oneDayMilliSec));
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        String s = formatter.format(nextDate);
        s = s.toUpperCase();
        return s;

    }
}
