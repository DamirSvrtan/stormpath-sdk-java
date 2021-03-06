/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.sdk.impl.group;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.directory.AccountStoreVisitor;
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupMembership;
import com.stormpath.sdk.group.GroupMembershipList;
import com.stormpath.sdk.group.GroupOptions;
import com.stormpath.sdk.group.GroupStatus;
import com.stormpath.sdk.impl.directory.AbstractDirectoryEntity;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.CollectionReference;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.StatusProperty;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * @since 0.2
 */
public class DefaultGroup extends AbstractDirectoryEntity implements Group {

    // SIMPLE PROPERTIES
    static final StringProperty NAME = new StringProperty("name");
    static final StringProperty DESCRIPTION = new StringProperty("description");
    static final StatusProperty<GroupStatus> STATUS = new StatusProperty<GroupStatus>(GroupStatus.class);

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Directory> DIRECTORY = new ResourceReference<Directory>("directory", Directory.class);
    static final ResourceReference<Tenant> TENANT = new ResourceReference<Tenant>("tenant", Tenant.class);

    // COLLECTION RESOURCE REFERENCES:
    static final CollectionReference<AccountList, Account> ACCOUNTS =
            new CollectionReference<AccountList, Account>("accounts", AccountList.class, Account.class);
    static final CollectionReference<GroupMembershipList, GroupMembership> ACCOUNT_MEMBERSHIPS =
            new CollectionReference<GroupMembershipList, GroupMembership>("accountMemberships", GroupMembershipList.class, GroupMembership.class);

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            NAME, DESCRIPTION, STATUS, CUSTOM_DATA, DIRECTORY, TENANT, ACCOUNTS, ACCOUNT_MEMBERSHIPS);

    public DefaultGroup(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultGroup(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getName() {
        return getString(NAME);
    }

    @Override
    public void setName(String name) {
        setProperty(NAME, name);
    }

    @Override
    public String getDescription() {
        return getString(DESCRIPTION);
    }

    @Override
    public void setDescription(String description) {
        setProperty(DESCRIPTION, description);
    }

    @Override
    public GroupStatus getStatus() {
        String value = getStringProperty(STATUS.getName());
        if (value == null) {
            return null;
        }
        return GroupStatus.valueOf(value.toUpperCase());
    }

    @Override
    public void setStatus(GroupStatus status) {
        setProperty(STATUS, status.name());
    }

    @Override
    public CustomData getCustomData() {
        return super.getCustomData();
    }

    @Override
    public Tenant getTenant() {
        return getResourceProperty(TENANT);
    }

    @Override
    public Directory getDirectory() {
        return getResourceProperty(DIRECTORY);
    }

    @Override
    public AccountList getAccounts() {
        return getResourceProperty(ACCOUNTS);
    }

    @Override
    public AccountList getAccounts(Map<String, Object> queryParams) {
        AccountList list = getAccounts(); //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(list.getHref(), AccountList.class, queryParams);
    }

    @Override
    public AccountList getAccounts(AccountCriteria criteria) {
        AccountList list = getAccounts(); //safe to get the href; does not execute a query until iteration occurs
        return getDataStore().getResource(list.getHref(), AccountList.class, criteria);
    }

    @Override
    public GroupMembershipList getAccountMemberships() {
        return getResourceProperty(ACCOUNT_MEMBERSHIPS);
    }

    @Override
    public GroupMembership addAccount(Account account) {
        return DefaultGroupMembership.create(account, this, getDataStore());
    }

    /**
     * @since 0.8
     */
    @Override
    public void delete() {
        getDataStore().delete(this);
    }

    /**
     * @since 0.8
     */
    @Override
    public void accept(AccountStoreVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void save() {
        applyCustomDataUpdatesIfNecessary();
        super.save();
    }

    @Override
    public void saveWithResponseOptions(GroupOptions groupOptions) {
        Assert.notNull(groupOptions, "accountOptions can't be null.");
        applyCustomDataUpdatesIfNecessary();
        getDataStore().save(this, groupOptions);
    }
}
