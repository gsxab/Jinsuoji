package org.jinsuoji.jinsuoji.account;

import java.io.Serializable;

/**
 * 同步操作通过这个Bean获取内容.
 */
public class Account implements Serializable {
    private String username;
    private String storedPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStoredPassword() {
        return storedPassword;
    }

    public void setStoredPassword(String storedPassword) {
        this.storedPassword = storedPassword;
    }

    public Account(String username, String storedPassword) {
        this.username = username;
        this.storedPassword = storedPassword;
    }
}
