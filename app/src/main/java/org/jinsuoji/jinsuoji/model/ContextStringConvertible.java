package org.jinsuoji.jinsuoji.model;

import android.content.Context;

/**
 * 通过上下文转成字符串.
 */
public interface ContextStringConvertible {
    /**
     * 转成字符串.
     * @param context 上下文对象，用于获取字符串资源
     * @return 字符串
     */
    String toContextString(Context context);
}
