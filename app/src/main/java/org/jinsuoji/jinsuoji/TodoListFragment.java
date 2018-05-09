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

/**
 * 任务页的{@link Fragment}.
 */
public class TodoListFragment extends Fragment {
    private OnFragmentInteractionListener listener = null;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.finished_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new TodoListAdaptor(getContext(), 0, 0, 0, true));
        recyclerView.addItemDecoration(new SpaceItemDecoration(16));

        recyclerView = view.findViewById(R.id.unfinished_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new TodoListAdaptor(getContext(), 0, 0, 0, false));
        recyclerView.addItemDecoration(new SpaceItemDecoration(16));
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
