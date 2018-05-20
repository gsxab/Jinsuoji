package org.jinsuoji.jinsuoji;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import org.jinsuoji.jinsuoji.model.ContextStringConvertible;

public class ItemTouchListener<T extends ContextStringConvertible> implements RecyclerView.OnItemTouchListener {
    interface RecyclerViewOperator<T extends ContextStringConvertible> {
        Context getContext();
        boolean isTouchable(T data);
        void performEdit(View view, int pos, T data);
        void performRemove(View view, int pos, T data);
    }

    private GestureDetector mGestureDetector;
    private RecyclerViewOperator<T> mFragment;
    private RecyclerView mRecyclerView;

    ItemTouchListener(final RecyclerViewOperator<T> fragment, RecyclerView recyclerView) {
        mFragment = fragment;
        mRecyclerView = recyclerView;
        mGestureDetector = new GestureDetector(fragment.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            @SuppressWarnings("unchecked")
            public void onLongPress(MotionEvent e) {
                final View childView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null) {
                    final T data = (T) childView.getTag();
                    if (!fragment.isTouchable(data)) {
                        return;
                    }
                    final AlertDialog dialog = new AlertDialog.Builder(mFragment.getContext())
                            .setTitle(R.string.delete_warning)
                            .setMessage(childView.getResources()
                                    .getString(R.string.delete_warning_message,
                                            data.toContextString(mFragment.getContext())))
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mFragment.performRemove(childView,
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
                && mFragment.isTouchable((T) childView.getTag())) {
            mFragment.performEdit(childView, view.getChildAdapterPosition(childView),
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
