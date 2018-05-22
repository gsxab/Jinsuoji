package org.jinsuoji.jinsuoji.net;

public class AccountBean {
    public AccountBean() {
        req = false;
    }

    private String username;
    private String encrypted;
    private boolean req;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(String encrypted) {
        this.encrypted = encrypted;
    }

    public boolean isReq() {
        return req;
    }

    public void setReq(boolean req) {
        this.req = req;
    }
}
