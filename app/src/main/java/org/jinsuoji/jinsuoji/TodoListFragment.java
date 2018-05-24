package org.jinsuoji.jinsuoji;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

/**
 * 任务页的{@link Fragment}.
 */
public class TodoListFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    private OnFragmentInteractionListener listener = null;
    private RecyclerView finishedListView, unfinishedListView;
    private TextView finishedListTitle;
    private RecyclerView.ItemDecoration decoration = new SpaceItemDecoration(16);

    public TodoListFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // params
        }
    }

    @Override
    public @Nullable View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_todo_list, container, false);
    }

    interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = ((OnFragmentInteractionListener) context);
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Preference.setShowFinished(buttonView.getContext(), isChecked);
        if (isChecked) {
            onShowFinished();
        } else {
            onHideFinished();
        }
    }

    private void onShowFinished() {
        finishedListView.setVisibility(View.VISIBLE);
        finishedListTitle.setVisibility(View.VISIBLE);
        finishedListView.setLayoutManager(new LinearLayoutManager(getContext()));
        finishedListView.setAdapter(new TodoListAdaptor(getContext(), true));
        finishedListView.addItemDecoration(decoration);
    }

    private void onHideFinished() {
        finishedListView.removeItemDecoration(decoration);
        finishedListView.setVisibility(View.GONE);
        finishedListTitle.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(final @NonNull View view, @Nullable Bundle savedInstanceState) {
        unfinishedListView = view.findViewById(R.id.unfinished_list);
        unfinishedListView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        unfinishedListView.setAdapter(new TodoListAdaptor(getContext(), false));
        unfinishedListView.addItemDecoration(decoration);

        finishedListView = view.findViewById(R.id.finished_list);
        finishedListTitle = view.findViewById(R.id.finished_list_title);

        if (Preference.getShowFinished(view.getContext())) {
            onShowFinished();
        } else {
            onHideFinished();
        }

        Switch aSwitch = view.findViewById(R.id.show_finished_switch);
        aSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public static TodoListFragment newInstance() {
        return new TodoListFragment();
    }
}
