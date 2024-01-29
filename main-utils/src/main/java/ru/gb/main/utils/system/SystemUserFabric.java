package ru.gb.main.utils.system;

import ru.gb.main.utils.entities.User;

public class SystemUserFabric {
    public static User systemUser() {
        User user = new User( "System", null);
        user.setSystem(true);
        user.setId(1);
        return user;
    }
}
