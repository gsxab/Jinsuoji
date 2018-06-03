package org.jinsuoji.jinsuoji.net;

import org.jinsuoji.jinsuoji.account.AccountManager;

import java.net.HttpURLConnection;

/**
 * 访问login，完成登录的认证，只认证不获取令牌.
 */
public class AuthTask extends RestfulAsyncTask<String> {
    private final SaltTask saltTask;

    /**
     * 构造并访问执行一个对login的请求，其中包含了salt请求.
     * @param manager 账户信息，用于获取username和掺沙子的密码
     * @param onSuccess 回调
     */
    public AuthTask(final AccountManager manager, final SuccessOperation<String> onSuccess,
                    final FailureOperation onFailure, final MessageOperation onMessage) {
        super(new ReqAttr("POST", false, true, true,
                HttpURLConnection.HTTP_OK), "/login", onSuccess, onFailure, onMessage);
        if (manager.checkNoLoginInfo()) throw new AssertionError();
        final AccountBean bean = new AccountBean();
        bean.setUsername(manager.getUsername());
        saltTask = new SaltTask(bean, new SuccessOperation<SaltTask.SaltBean>() {
            @Override
            public void onSuccess(SaltTask.SaltBean result) {
                bean.setSalt(result.getSalt());
                bean.setEncrypted(manager.addSalt(result.getSalt()));
                bean.setReq(false);
                AuthTask.this.execute(bean, String.class);
            }
        }, onFailure, onMessage);
    }

    @Override
    public void start() {
        saltTask.start();
    }
}
