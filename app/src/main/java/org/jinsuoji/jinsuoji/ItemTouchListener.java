package org.jinsuoji.jinsuoji;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import org.jinsuoji.jinsuoji.model.ContextualStringConvertible;

/**
 * 监听一个RecyclerView的OnItemTouch事件.
 * @param <T> 条目的类型，由于需要显示条目信息，需要实现{@link ContextualStringConvertible}
 */
public class ItemTouchListener<T extends ContextualStringConvertible> implements RecyclerView.OnItemTouchListener {
    /**
     * 对RecyclerView操作需要的4个方法.
     * @param <T> 外部{@link ItemTouchListener}的T
     */
    interface RecyclerViewOperator<T extends ContextualStringConvertible> {
        /**
         * 从外部获取上下文对象从而获取资源等.
         * @return 上下文对象
         */
        Context getContext();

        /**
         * 检测一个条目是否直接响应OnItemTouch事件
         * @param data 条目数据
         * @return 是否响应
         */
        boolean isTouchable(T data);

        /**
         * 编辑一个条目的操作.
         * @param view 条目的视图
         * @param pos 条目的位置
         * @param data 条目的数据
         */
        void performEdit(View view, int pos, T data);

        /**
         * 删除一个条目的操作.
         * @param view 条目的视图
         * @param pos 条目的位置
         * @param data 条目的数据
         */
        void performRemove(View view, int pos, T data);
    }

    private GestureDetector mGestureDetector;
    private RecyclerViewOperator<T> mOperator;
    private RecyclerView mRecyclerView;

    ItemTouchListener(final RecyclerViewOperator<T> operator, RecyclerView recyclerView, final boolean right) {
        mOperator = operator;
        mRecyclerView = recyclerView;
        mGestureDetector = new GestureDetector(operator.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (right && e.getX() > 1150) return false; // FIXME 不知道可不可以，把多选框空出去
                Log.d("ITL", "onSingleTapUp: " + e.getX());
                return true;
            }

            @Override
            @SuppressWarnings("unchecked")
            public void onLongPress(MotionEvent e) {
                final View childView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null) {
                    final T data = (T) childView.getTag();
                    if (!operator.isTouchable(data)) {
                        return;
                    }
                    final AlertDialog dialog = new AlertDialog.Builder(mOperator.getContext())
                            .setTitle(R.string.delete_warning)
                            .setMessage(childView.getResources()
                                    .getString(R.string.delete_warning_message,
                                            data.toContextualString(mOperator.getContext())))
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mOperator.performRemove(childView,
                                            mRecyclerView.getChildAdapterPosition(childView), data);
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mGestureDetector.onTouchEvent(e)
                && mOperator.isTouchable((T) childView.getTag())) {
            mOperator.performEdit(childView, view.getChildAdapterPosition(childView),
                    (T) childView.getTag());
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}
