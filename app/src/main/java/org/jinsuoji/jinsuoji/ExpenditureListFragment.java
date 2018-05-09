package org.jinsuoji.jinsuoji;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A {@link Fragment} representing a list of {@link org.jinsuoji.jinsuoji.model.EntryNode}.
 * <p/>
 * 只含有一个RecyclerView.使用的Adapter是ExpenseListAdapter.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ExpenditureListFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private ExpenseListAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ExpenditureListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expenditure_list, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }

    public static ExpenditureListFragment newInstance(Context context, int year, int month, int date, boolean byDate) {
        ExpenditureListFragment fragment = new ExpenditureListFragment();
        fragment.adapter = new ExpenseListAdapter(context, year, month, date, byDate);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.expenditure_list);
        if (adapter != null) {
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getView() != null) {
            recyclerView = getView().findViewById(R.id.expenditure_list);
        }
    }

    /**
     * 重新设置选择条件.仅有按月显示界面在调整月份时会调用这个函数.
     *
     * @param context 上下文
     * @param year 年
     * @param month 月[1-12]
     * @param byDate 是按日期还是按类别
     */
    public void setSelector(Context context, int year, int month, boolean byDate) {
        if (recyclerView == null && getView() != null) {
            recyclerView = getView().findViewById(R.id.expenditure_list);
        }
        if (recyclerView != null)
            ((ExpenseListAdapter) recyclerView.getAdapter())
                    .setNewDate(context, year, month, 0, byDate);
    }
}
