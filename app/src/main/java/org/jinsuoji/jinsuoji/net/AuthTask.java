package org.jinsuoji.jinsuoji.net;

import org.jinsuoji.jinsuoji.account.AccountManager;

import java.net.HttpURLConnection;

/**
 * 访问login，完成登录的认证，只认证不获取令牌.
 */
public class AuthTask extends RestfulAsyncTask<String> {
    /**
     * 构造并访问执行一个对login的请求，其中包含了salt请求.
     * @param manager 账户信息，用于获取username和掺沙子的密码
     * @param onSuccess 回调
     */
    public AuthTask(final AccountManager manager, final SuccessOperation<String> onSuccess,
                    final MessageOperation onMessage) {
        super(new ReqAttr("POST", false, true, true,
                HttpURLConnection.HTTP_OK), "/login", onSuccess, onMessage);
        final AccountBean bean = new AccountBean();
        if (!manager.hasLoginInfo()) {
            onMessage.onFailure(new ErrorBean("NO_LOGIN_INFO", ""));
            return;
        }
        bean.setUsername(manager.getUsername());
        new SaltTask(bean, new SuccessOperation<SaltTask.SaltBean>() {
            @Override
            public void onSuccess(SaltTask.SaltBean result) {
                bean.setEncrypted(manager.addSalt(result.getSalt()));
                bean.setReq(false);
                AuthTask.this.execute(bean, null);
            }
        }, new MessageOperation() {
            @Override
            public void onFailure(ErrorBean errorBean) {
                onMessage.onFailure(errorBean);
            }

            @Override
            public void onProgressUpdate(int phase) {
                onMessage.onProgressUpdate(phase);
            }
        });
    }
}
