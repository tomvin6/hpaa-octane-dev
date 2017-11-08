/*
 * Copyright (c) 2012 Hewlett-Packard Development Company, L.P.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.hpe.application.automation.tools.sse.sdk.authenticator;

import com.hpe.application.automation.tools.common.SSEException;
import com.hpe.application.automation.tools.sse.sdk.Client;
import com.hpe.application.automation.tools.sse.sdk.Logger;
import com.hpe.application.automation.tools.sse.sdk.ResourceAccessLevel;
import com.hpe.application.automation.tools.sse.sdk.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Unify the rest authentication process here from separated part, ALMRestTool and RunManager.
 * Any authentication change will only need to change here.
 * Created by llu2 on 4/5/2017.
 */
public final class AuthenticationTool {

    private static List<Authenticator> authenticators;

    private AuthenticationTool() {
        // Add the private constructor to hide the implicit public one.
    }

    static {
        authenticators = new ArrayList<>();
        authenticators.add(new RestAuthenticator());
        /**
         * Mute RestAuthenticatorSaas for it's redundant after the improvement of RestAuthenticator.
         * authenticators.add(new RestAuthenticatorSaas());
         */
    }

    /**
     * Try authenticate use a list of authenticators and then create session.
     */
    public static boolean authenticate(Client client, String username, String password, String url, Logger logger) {
        if (login(client, username, password, url, logger)) {
            appendQCSessionCookies(client, logger);
            return true;
        }
        return false;
    }

    private static boolean login(Client client, String username, String password, String url, Logger logger) {
        boolean result = false;
        for(Authenticator authenticator : authenticators) {
            try {
                result = authenticator.login(client, username, password, logger);
                if (result) {
                    break;
                }
            } catch (Exception e) {
                logger.log(String.format(
                        "Failed login to ALM Server URL: %s. Exception: %s",
                        url.endsWith("/") ? url : String.format("%s/", url),
                        e.getMessage()));
            }
        }
        return result;
    }

    private static void appendQCSessionCookies(Client client, Logger logger) {
        logger.log("Creating session...");
        // issue a post request so that cookies relevant to the QC Session will be added to the RestClient
        Response response =
                client.httpPost(
                        client.build("rest/site-session"),
                        new byte[1],    // For some server would require post request has a Content-Length.
                        null,
                        ResourceAccessLevel.PUBLIC);
        if (!response.isOk()) {
            throw new SSEException("Cannot append QCSession cookies", response.getFailure());
        }
    }
}
