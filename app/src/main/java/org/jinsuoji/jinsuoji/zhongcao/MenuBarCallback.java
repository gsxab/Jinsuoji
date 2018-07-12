package org.jinsuoji.jinsuoji.zhongcao;

import android.support.annotation.IdRes;

import java.util.List;

/**
 * 菜单的回调.
 * 所有函数返回布尔值，true代表处理完毕请求刷新数据，false代表不处理.
 */
interface MenuBarCallback {
    boolean action(@IdRes int actionId, List<Integer> indices);
}
