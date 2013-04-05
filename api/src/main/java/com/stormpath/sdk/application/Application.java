/*
 * Copyright 2013 Stormpath, Inc. and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.application;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.resource.Saveable;
import com.stormpath.sdk.resource.Status;
import com.stormpath.sdk.tenant.Tenant;

/**
 * An {@code Application} instance represents a Stormpath
 * <a href="https://www.stormpath.com/docs/managing-applications">registered application</a>.
 *
 * @since 0.1
 */
public interface Application extends Resource, Saveable {

    /**
     * Returns the Application's name.  An application's name must be unique across all other applications in the
     * Stormpath Tenant.
     *
     * @return the Application's name
     */
    String getName();

    /**
     * Sets the application's name.  Application names must be unique per Tenant.
     *
     * @param name tenant-unique name of the application.
     */
    void setName(String name);

    /**
     * Returns the application description.
     *
     * @return the application description.
     */
    String getDescription();

    /**
     * Sets the application description.
     *
     * @param description the application description.
     */
    void setDescription(String description);

    /**
     * Returns the application's Status.  Application users may login to an enabled application.  They may not login
     * to a disabled application.
     *
     * @return the application's Status.
     */
    Status getStatus();

    /**
     * Sets the application's Status.  Application users may login to an enabled application.  They may not login
     * to a disabled application.
     *
     * @param status the application's Status.
     */
    void setStatus(Status status);

    /**
     * Returns the AccountList for all accounts that may login to the application.
     *
     * @return the AccountList for all accounts that may login to the application.
     */
    AccountList getAccounts();

    /**
     * Returns the application's parent (owning) Tenant.
     *
     * @return the application's parent (owning) Tenant.
     */
    Tenant getTenant();

    /**
     * Sends a password reset email for the specified account username or email address.  The email will contain
     * a password reset link that the user can click or copy into their browser address bar.
     * <p/>
     * This method merely sends the password reset email that contains the link and nothing else.  You will need to
     * handle the link requests and then reset the account's password as described in the
     * {@link #verifyPasswordResetToken(String)} JavaDoc.
     *
     * @param accountUsernameOrEmail a username or email address of an Account that may login to the application.
     * @return the account corresponding to the specified username or email address.
     * @see #verifyPasswordResetToken(String)
     */
    Account sendPasswordResetEmail(String accountUsernameOrEmail);

    /**
     * Verifies a password reset token in a user-clicked link within an email.
     * <p/>
     * <h2>Base Link Configuration</h2>
     * You need to define the <em>Base</em> link that will process HTTP requests when users click the link in the
     * email as part of your Application's Workflow Configuration within the Stormpath UI Console.  It must be a URL
     * served by your application's web servers.  For example:
     * <pre>
     * https://www.myApplication.com/passwordReset
     * </pre>
     * <h2>Runtime Link Processing</h2>
     * When an application user clicks on the link in the email at runtime, your web server needs to process the request
     * and look for an {@code spToken} request parameter.  You can then verify the {@code spToken}, and then finally
     * change the Account's password.
     * <p/>
     * Usage Example:
     * <p/>
     * Browser:
     * {@code GET https://www.myApplication/passwordReset?spToken=someTokenValueHere}
     * <p/>
     * Your code:
     * <pre>
     * String token = httpServletRequest.getParameter("spToken");
     *
     * Account account = application.verifyPasswordResetToken(token);
     *
     * //token has been verified - now set the new password with what the end-user submits:
     * account.setPassword(user_submitted_new_password);
     * account.save();
     * </pre>
     *
     * @param token the verification token, usually obtained as a request parameter by your application.
     * @return the Account matching the specified token.
     * @since 0.4
     */
    Account verifyPasswordResetToken(String token);

    /**
     * Authenticates an account's submitted principals and credentials (e.g. username and password).  The account must
     * be in one of the Application's
     * <a href="http://www.stormpath.com/docs/managing-applications-login-sources">assigned Login Sources</a>.  If not
     * in an assigned login source, the authentication attempt will fail.
     * <h2>Example</h2>
     * Consider the following username/password-based example:
     * <p/>
     * <pre>
     * AuthenticationRequest request = new UsernamePasswordRequest(email, submittedRawPlaintextPassword);
     * Account authenticated = appToTest.authenticateAccount(request).getAccount();
     * </pre>
     *
     * @param request the authentication request representing an account's principals and credentials (e.g.
     *                username/password) used to verify their identity.
     * @return the result of the authentication.  The authenticated account can be obtained from
     *         {@code result.}{@link com.stormpath.sdk.authc.AuthenticationResult#getAccount() getAccount()}.
     * @throws ResourceException if the authentication attempt fails.
     */
    AuthenticationResult authenticateAccount(AuthenticationRequest request) throws ResourceException;

    /**
     * @since 0.8
     */
    void delete();
}
