/*
 * Copyright (c) 2016 Uber Technologies, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.uber.sdk.rides.samples.servlet;

import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.uber.sdk.rides.auth.OAuth2Credentials;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Demonstrates how to make oauth callbacks via a servlet.
 */
public class OAuth2CallbackServlet extends HttpServlet {

    private OAuth2Credentials oAuth2Credentials;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (oAuth2Credentials == null) {
            SessionConfiguration config = Server.createSessionConfiguration();
            oAuth2Credentials = Server.createOAuth2Credentials(config);
        }

        HttpSession httpSession = req.getSession(true);
        if (httpSession.getAttribute(Server.USER_SESSION_ID) == null) {
            httpSession.setAttribute(Server.USER_SESSION_ID, new Random().nextLong());
        }
        super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestUrl = req.getRequestURL().append('?').append(req.getQueryString()).toString();
        AuthorizationCodeResponseUrl authorizationCodeResponseUrl =
                new AuthorizationCodeResponseUrl(requestUrl);

        if (authorizationCodeResponseUrl.getError() != null) {
            throw new IOException("Received error: " + authorizationCodeResponseUrl.getError());
        } else {
            // Authenticate the user and store their credential with their user ID (derived from
            // the request).
            HttpSession httpSession = req.getSession(true);
            if (httpSession.getAttribute(Server.USER_SESSION_ID) == null) {
                httpSession.setAttribute(Server.USER_SESSION_ID, new Random().nextLong());
            }
            String authorizationCode = authorizationCodeResponseUrl.getCode();
            oAuth2Credentials.authenticate(authorizationCode, httpSession.getAttribute(Server.USER_SESSION_ID).toString());
        }
        resp.sendRedirect("/");
    }
}
