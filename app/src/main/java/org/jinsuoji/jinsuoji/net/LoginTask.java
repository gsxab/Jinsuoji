package org.jinsuoji.jinsuoji.net;

import org.jinsuoji.jinsuoji.account.AccountManager;

/**
 * 访问login，完成登录的认证，然后获取令牌.
 */
class LoginTask extends RestfulAsyncTask<TokenBean> {
    private final AccountBean bean;
    private final SuccessOperation<SaltTask.SaltBean> onSuccess;
    private final FailureOperation onFailure;
    private final MessageOperation onMessage;

    /**
     * 构造并访问执行一个对login的请求，其中包含了salt请求.
     * @param manager 账户信息，用于获取username和掺沙子的密码
     * @param onSuccess 回调
     */
    LoginTask(final AccountManager manager, final SuccessOperation<TokenBean> onSuccess,
              final FailureOperation onFailure, final MessageOperation onMessage) {
        super(ReqAttr.RESTFUL_LOGIN, "/login", onSuccess, onFailure, onMessage);
        if (manager.checkNoLoginInfo()) throw new AssertionError();
        bean = new AccountBean();
        bean.setUsername(manager.getUsername());
        this.onSuccess = new SuccessOperation<SaltTask.SaltBean>() {
            @Override
            public void onSuccess(SaltTask.SaltBean result) {
                bean.setSalt(result.getSalt());
                bean.setEncrypted(manager.addSalt(result.getSalt()));
                bean.setReq(true);
                LoginTask.this.execute(bean, TokenBean.class);
            }
        };
        this.onFailure = onFailure;
        this.onMessage = onMessage;
    }

    @Override
    public void start() {
        new SaltTask(bean, onSuccess, onFailure, onMessage).start();
    }

    @Override
    protected boolean isFinalTask() {
        return false;
    }
}
