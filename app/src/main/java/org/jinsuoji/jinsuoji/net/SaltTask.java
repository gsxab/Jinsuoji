package org.jinsuoji.jinsuoji.net;

/**
 * 访问login_salt.完成验证中的第一步骤.
 */
class SaltTask extends RestfulAsyncTask<SaltTask.SaltBean> {
    static class SaltBean {
        public SaltBean() {
        }

        public SaltBean(String salt) {
            this.salt = salt;
        }

        private String salt;

        public String getSalt() {
            return salt;
        }

        public void setSalt(String salt) {
            this.salt = salt;
        }
    }

    private AccountBean bean;

    /**
     * 构造并访问执行一个对login_salt的请求.
     * @param bean 账户信息，仅username字段有效
     * @param onSuccess 回调
     * @param onFailure 失败时回调
     * @param onMessage 消息时回调
     */
    SaltTask(AccountBean bean, SuccessOperation<SaltBean> onSuccess,
             FailureOperation onFailure, MessageOperation onMessage) {
        super(ReqAttr.RESTFUL_LOGIN, "/login_salt", onSuccess, onFailure, onMessage);
        this.bean = bean;
    }

    @Override
    public void start() {
        super.execute(bean, SaltBean.class);
    }
}
