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
package com.ggasoftware.indigo.eln.services.crs;

import com.chemistry.enotebook.compoundregistration.exceptions.RegistrationRuntimeException;
import com.chemistry.enotebook.search.exceptions.SearchServiceException;
import com.epam.indigo.crs.client.CrsClient;
import com.epam.indigo.crs.services.registration.BingoRegistration;
import com.epam.indigo.crs.services.search.BingoSearch;

import java.io.InputStream;
import java.util.Properties;

public class CrsConnection {

    public static BingoRegistration getRegistrationService() throws RegistrationRuntimeException {
        try {
            return CrsClient.getRegistrationService(getURL());
        } catch (Exception e) {
            throw new RegistrationRuntimeException(e.getMessage(), e);
        }
    }

    public static BingoSearch getSearchService() throws SearchServiceException {
        try {
            return CrsClient.getSearchService(getURL());
        } catch (Exception e) {
            throw new SearchServiceException(e.getMessage(), e);
        }
    }

    private static String getURL() throws Exception {
        InputStream is = null;

        try {
            is = CrsConnection.class.getClassLoader().getResourceAsStream("registration.properties");

            Properties properties = new Properties();
            properties.load(is);

            return properties.getProperty("SERVICE_URL");
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
