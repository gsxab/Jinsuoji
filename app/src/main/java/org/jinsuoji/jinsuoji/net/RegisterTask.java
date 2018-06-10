package org.jinsuoji.jinsuoji.net;

import org.jinsuoji.jinsuoji.account.Account;

/**
 * 访问login，完成登录的认证，只认证不获取令牌.
 */
public class RegisterTask extends RestfulAsyncTask<String> {
    private final Account account;

    /**
     * 构造并访问执行一个对login的请求，其中包含了salt请求.
     * @param account 账户，用于获取username和掺沙子的密码
     * @param onSuccess 回调
     */
    public RegisterTask(final Account account,
                        final SuccessOperation<String> onSuccess,
                        final FailureOperation onFailure,
                        final MessageOperation onMessage) {
        super(ReqAttr.RESTFUL_LOGIN, "/register", onSuccess, onFailure, onMessage);
        this.account = account;
    }

    @Override
    public void start() {
        execute(account, String.class);
    }

    @Override
    protected boolean isFinalTask() {
        return true;
    }
}
