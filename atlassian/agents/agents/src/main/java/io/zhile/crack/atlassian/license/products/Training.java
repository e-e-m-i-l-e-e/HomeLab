/*
 * Decompiled with CFR 0.152.
 */
package io.zhile.crack.atlassian.license.products;

import io.zhile.crack.atlassian.license.products.Plugin;

public class Training
extends Plugin {
    public Training(String contactName, String contactEMail, String serverID, String organisation, boolean dataCenter) {
        super(contactName, contactEMail, serverID, organisation, dataCenter);
    }

    @Override
    public void init() {
        super.init();
        this.setSubscription(true);
    }

    @Override
    public String getProductName() {
        return "atlassian-jira-training";
    }

    public void setSubscription(boolean subscription) {
        this.data.put("Subscription", String.valueOf(subscription));
    }
}

