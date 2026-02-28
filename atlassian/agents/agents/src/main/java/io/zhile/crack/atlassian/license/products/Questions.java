/*
 * Decompiled with CFR 0.152.
 */
package io.zhile.crack.atlassian.license.products;

import io.zhile.crack.atlassian.license.products.Plugin;

public class Questions
extends Plugin {
    public Questions(String contactName, String contactEMail, String serverID, String organisation, boolean dataCenter) {
        super(contactName, contactEMail, serverID, organisation, dataCenter);
    }

    @Override
    public String getProductName() {
        return "com.atlassian.confluence.plugins.confluence-questions";
    }
}

