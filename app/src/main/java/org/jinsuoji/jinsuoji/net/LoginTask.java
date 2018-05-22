package org.jinsuoji.jinsuoji.net;

import org.jinsuoji.jinsuoji.account.AccountManager;

/**
 * 访问login，完成登录的认证，然后获取令牌.
 */
public class LoginTask extends RestfulAsyncTask<TokenBean> {
    /**
     * 构造并访问执行一个对login的请求，其中包含了salt请求.
     * @param manager 账户信息，用于获取username和掺沙子的密码
     * @param onSuccess 回调
     */
    public LoginTask(final AccountManager manager, final SuccessOperation<TokenBean> onSuccess,
                     final MessageOperation onMessage) {
        super(ReqAttr.RESTFUL_LOGIN, "/login", onSuccess, onMessage);
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
                bean.setReq(true);
                LoginTask.this.execute(bean, TokenBean.class);
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
