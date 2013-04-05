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
package com.stormpath.sdk.impl.tenant;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.EmailVerificationToken;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.directory.DirectoryList;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.tenant.Tenant;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 0.1
 */
public class DefaultTenant extends AbstractInstanceResource implements Tenant {

    private static final String NAME = "name";
    private static final String KEY = "key";
    private static final String APPLICATIONS = "applications";
    private static final String DIRECTORIES = "directories";

    public DefaultTenant(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultTenant(InternalDataStore dataStore, Map<String,Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public String getName() {
        return getStringProperty(NAME);
    }

    @Override
    public String getKey() {
        return getStringProperty(KEY);
    }

    @Override
    public void createApplication(Application application) {
        //ApplicationList list = getApplications();
        //String href = list.getHref();
        String href = "/applications"; //TODO enable auto discovery
        getDataStore().create(href, application);
    }

    @Override
    public ApplicationList getApplications() {
        return getResourceProperty(APPLICATIONS, ApplicationList.class);
    }

    @Override
    public DirectoryList getDirectories() {
        return getResourceProperty(DIRECTORIES, DirectoryList.class);
    }

    @Override
    public Account verifyAccountEmail(String token) {

        //TODO enable auto discovery via Tenant resource (should be just /emailVerificationTokens
        String href = "/accounts/emailVerificationTokens/" + token;

        Map<String,Object> props = new LinkedHashMap<String, Object>(1);
        props.put(HREF_PROP_NAME, href);

        EmailVerificationToken evToken = getDataStore().instantiate(EmailVerificationToken.class, props);

        //execute a POST (should clean this up / make it more obvious)
        return getDataStore().save(evToken, Account.class);
    }

    /**
     * @since 0.8
     */
    @Override
    public void createDirectory(Directory directory) {
        String href = "/" + DIRECTORIES; //TODO enable auto discovery
        getDataStore().create(href, directory);
    }
}
