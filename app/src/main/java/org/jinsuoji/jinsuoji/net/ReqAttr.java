package org.jinsuoji.jinsuoji.net;

import java.net.HttpURLConnection;

/**
 * 一个连接的特征属性.
 * 成员依次为：
 * <ul>
 *     <li>方法名（GET,POST,DELETE,PATCH,PUT,...）</li>
 *     <li>是否在成功时打开输入流（不返回内容时为false）</li>
 *     <li>是否打开输出流写请求数据（方法GET的请求体会被忽视，放在url里）</li>
 *     <li>是否在返回40x时打开错误流（不返回内容时为false）</li>
 *     <li>视为成功的状态码</li>
 * </ul>
 */
public class ReqAttr {
    /**
     * RESTFUL API的GET，获取资源.信息写在URL里.
     */
    public static final ReqAttr RESTFUL_GET = new ReqAttr("GET", true,
            false, true,HttpURLConnection.HTTP_OK);
    /**
     * RESTFUL API的POST，创建资源.
     */
    public static final ReqAttr RESTFUL_POST = new ReqAttr("POST", false,
            true, true,HttpURLConnection.HTTP_CREATED);
    /**
     * RESTFUL API的PUT，创建或覆盖资源.
     */
    public static final ReqAttr RESTFUL_PUT = new ReqAttr("PUT", false,
            true, true,HttpURLConnection.HTTP_CREATED);
    /**
     * RESTFUL API的PATCH，发送一个资源的更新部分.
     */
    public static final ReqAttr RESTFUL_PATCH = new ReqAttr("PATCH", false,
            true, true,HttpURLConnection.HTTP_CREATED);
    /**
     * RESTFUL API的DELETE，删除一个资源.
     */
    public static final ReqAttr RESTFUL_DELETE = new ReqAttr("DELETE", false,
            false, true,HttpURLConnection.HTTP_NO_CONTENT);
    /**
     * RESTFUL API登录模式.POST信息以换取TOKEN.
     */
    public static final ReqAttr RESTFUL_LOGIN = new ReqAttr("POST", true,
            true, true, HttpURLConnection.HTTP_OK);

    final String methodName;
    final boolean openInputStream;
    final boolean openOutputStream;
    final boolean openErrorStream;
    final int successCode;

    public ReqAttr(String methodName, boolean openInputStream, boolean openOutputStream,
                   boolean openErrorStream, int successCode) {
        this.methodName = methodName;
        this.openInputStream = openInputStream;
        this.openOutputStream = openOutputStream;
        this.openErrorStream = openErrorStream;
        this.successCode = successCode;
    }
}
