package org.jinsuoji.jinsuoji.net;

public class ErrorBean {
    public ErrorBean(String error, String data) {
        this.error = error;
        this.data = data;
    }

    private String error;
    private String data;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
