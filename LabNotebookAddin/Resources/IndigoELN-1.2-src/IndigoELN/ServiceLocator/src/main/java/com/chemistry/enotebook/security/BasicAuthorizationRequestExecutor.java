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
package com.chemistry.enotebook.security;

import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.remoting.httpinvoker.CommonsHttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;

import java.io.IOException;

public class BasicAuthorizationRequestExecutor extends CommonsHttpInvokerRequestExecutor {

    private String authorization;

    @Required
    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    @Override
    protected PostMethod createPostMethod(HttpInvokerClientConfiguration config) throws IOException {
        PostMethod postMethod = super.createPostMethod(config);

        postMethod.setRequestHeader("Authorization", authorization);
        
        return postMethod;
    }
}
