package org.jinsuoji.jinsuoji.net;

import android.content.Context;

import org.jinsuoji.jinsuoji.account.AccountManager;
import org.jinsuoji.jinsuoji.data_access.Serializer;

import java.net.HttpURLConnection;

class UploadTask extends RestfulAsyncTask<Void> {
    private String token;

    /**
     * 构造并访问执行一个对sync的POST请求，包括之前的login的和salt请求.
     * @param manager   账户信息，用于获取username和加密的密码
     * @param onSuccess 回调
     * @param onMessage 显示消息
     */
    public UploadTask(AccountManager manager, final Context context,
                      SuccessOperation<Void> onSuccess, MessageOperation onMessage) {
        super(ReqAttr.RESTFUL_POST, "/sync", onSuccess, onMessage);
        new LoginTask(manager, new SuccessOperation<TokenBean>() {
            @Override
            public void onSuccess(TokenBean result) {
                token = result.getToken();
                execute();
                Serializer.DBMirror mirror = new Serializer(context).export();
                UploadTask.this.execute(mirror);
            }
        }, onMessage);
    }

    @Override
    protected void decorate(HttpURLConnection conn) {
        conn.setRequestProperty("Token", token);
    }
}
