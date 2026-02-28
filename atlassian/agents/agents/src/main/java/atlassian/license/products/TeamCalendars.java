/*
 * Decompiled with CFR 0.152.
 */
package atlassian.license.products;

import io.zhile.crack.atlassian.license.products.Plugin;

public class TeamCalendars
extends Plugin {
    public TeamCalendars(String contactName, String contactEMail, String serverID, String organisation, boolean dataCenter) {
        super(contactName, contactEMail, serverID, organisation, dataCenter);
    }

    @Override
    public String getProductName() {
        return "team_calendars";
    }
}

