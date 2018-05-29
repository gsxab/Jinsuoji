package org.jinsuoji.jinsuoji.net;

import org.jinsuoji.jinsuoji.account.Account;
import org.jinsuoji.jinsuoji.account.AccountManager;

/**
 * 访问login，完成登录的认证，只认证不获取令牌.
 */
public class RegisterTask extends RestfulAsyncTask<String> {
    /**
     * 构造并访问执行一个对login的请求，其中包含了salt请求.
     * @param manager 账户信息，用于获取username和掺沙子的密码
     * @param onSuccess 回调
     */
    public RegisterTask(final AccountManager manager,
                        final Account account,
                        final SuccessOperation<String> onSuccess,
                        final MessageOperation onMessage) {
        super(ReqAttr.RESTFUL_LOGIN, "/register", onSuccess, onMessage);
        execute(account, String.class);
    }
}
