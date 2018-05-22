package org.jinsuoji.jinsuoji.net;

import android.content.Context;

import org.jinsuoji.jinsuoji.account.AccountManager;
import org.jinsuoji.jinsuoji.data_access.Serializer;

import java.net.HttpURLConnection;

class DownloadTask extends RestfulAsyncTask<Serializer.DBMirror> {
    private String token;

    /**
     * 构造并访问执行一个对sync的GET请求，包括之前的login的和salt请求.
     * @param manager   账户信息，用于获取username和加密的密码
     * @param onSuccess 回调
     * @param onMessage 显示消息
     */
    public DownloadTask(AccountManager manager, final Context context,
                        final SuccessOperation<Serializer.DBMirror> onSuccess, MessageOperation onMessage) {
        super(ReqAttr.RESTFUL_GET, "/sync", new SuccessOperation<Serializer.DBMirror>() {
            @Override
            public void onSuccess(Serializer.DBMirror result) {
                Serializer serializer = new Serializer(context);
                serializer.replaceImport(result);
                onSuccess.onSuccess(result);
            }
        }, onMessage);
        new LoginTask(manager, new SuccessOperation<TokenBean>() {
            @Override
            public void onSuccess(TokenBean result) {
                token = result.getToken();
                execute();
                DownloadTask.this.execute(null, Serializer.DBMirror.class);
            }
        }, onMessage);
    }

    @Override
    protected void decorate(HttpURLConnection conn) {
        conn.setRequestProperty("Token", token);
    }
}
