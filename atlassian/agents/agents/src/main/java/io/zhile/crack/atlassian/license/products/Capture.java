/*
 * Decompiled with CFR 0.152.
 */
package io.zhile.crack.atlassian.license.products;

import io.zhile.crack.atlassian.license.products.ThirdPlugin;

public class Capture
extends ThirdPlugin {
    public Capture(String contactName, String contactEMail, String serverID, String organisation, boolean dataCenter) {
        super(contactName, contactEMail, serverID, organisation, dataCenter);
        this.setProductName("bonfire");
    }
}

