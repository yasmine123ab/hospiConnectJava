package org.hospiconnect.utils;

import org.hospiconnect.model.User;

public class SecurityUtils {
    private static User connectedUser;

    public static User getConnectedUser() {
        return connectedUser;
    }

    public static void setConnectedUser(User connectedUser) {
        SecurityUtils.connectedUser = connectedUser;
    }

}
