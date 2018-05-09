package org.jinsuoji.jinsuoji;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 是记账页总的{@link Fragment}.包括题头、日期选择、tab页和tab内容(列表ExpenditureListFragment或图表).
 * ExpenditureFragment
 * - 第一行
 *   - “记帐本”
 *   - “”
 *
 * Use the {@link ExpenditureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpenditureFragment extends Fragment {
    private static final String KEY_YEAR = "exp_year";
    private static final String KEY_MONTH = "exp_month";
    private static final String KEY_STAT = "exp_stat";

    private static final int MIN_YEAR = 1950;
    private static final int MAX_YEAR = 2040;

    private int year;
    private int month;
    private boolean showStat;
    private TabLayout tab;
    private ViewPager pager;

    private ImageButton prevYear, nextYear, prevMonth, nextMonth;
    private TextView yearNumber, monthNumber;
    private ExpenditureListFragment listByDateFragment;
    private ExpenditureListFragment listByCategoryFragment;
    // private ExpenditureChartsFragment chartsFragment;

    private OnFragmentInteractionListener mListener;

    public ExpenditureFragment() {
        // Required empty public constructor
    }

    public static ExpenditureFragment newInstance() {
        ExpenditureFragment fragment = new ExpenditureFragment();
        Bundle args = new Bundle();
        Calendar date = Calendar.getInstance();
        args.putInt(KEY_YEAR, date.get(Calendar.YEAR));
        args.putInt(KEY_MONTH, date.get(Calendar.MONTH) + 1);
        args.putBoolean(KEY_STAT, false);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            year = getArguments().getInt(KEY_YEAR);
            month = getArguments().getInt(KEY_MONTH);
            showStat = getArguments().getBoolean(KEY_STAT);
        } else if (savedInstanceState != null) {
            year = savedInstanceState.getInt(KEY_YEAR);
            month = savedInstanceState.getInt(KEY_MONTH);
            showStat = savedInstanceState.getBoolean(KEY_STAT);
        }
    }

    private void prevYear() {
        if (year > MIN_YEAR) {
            year -= 1;
            refreshList();
        }
    }
    private void nextYear() {
        if (year < MAX_YEAR) {
            year += 1;
            refreshList();
        }
    }
    private void prevMonth() {
        if (month != 1) {
            month -= 1;
            refreshList();
        } else if (year > MIN_YEAR) {
            year -= 1;
            month = 12;
            refreshList();
        }
    }
    private void nextMonth() {
        if (month != 12) {
            month += 1;
            refreshList();
        } else if (year < MAX_YEAR) {
            year += 1;
            month = 1;
            refreshList();
        }
    }

    public void refreshList() {
        if (yearNumber != null) {
            yearNumber.setText(String.valueOf(year));
        }
        if (monthNumber != null) {
            monthNumber.setText(String.valueOf(month));
        }
        if (listByDateFragment != null) {
            listByDateFragment.setSelector(getActivity(), year, month, true);
        }
        if (listByCategoryFragment != null) {
            listByCategoryFragment.setSelector(getActivity(), year, month, false);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expenditure, container, false);
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tab = view.findViewById(R.id.expenditure_tab);
        pager = view.findViewById(R.id.expenditure_viewpager);
        tab.setupWithViewPager(pager);
        List<String> tabNames = new ArrayList<>(2);
        tabNames.add(getString(R.string.details_by_date));
        tabNames.add(getString(R.string.statistic));
        List<Fragment> fragments = new ArrayList<>(2);
        listByDateFragment = ExpenditureListFragment.newInstance(view.getContext(),
                year, month, 0, true);
        fragments.add(listByDateFragment);
        listByCategoryFragment = /*ExpenditureChartsFragment.newInstance()*/ExpenditureListFragment
                .newInstance(view.getContext(), year, month, 0, false);
        fragments.add(listByCategoryFragment);
        PagerAdapter adapter = new PagerAdapter(getFragmentManager(),
                tabNames, fragments);
        pager.setAdapter(adapter);

        yearNumber = view.findViewById(R.id.year_number);
        monthNumber = view.findViewById(R.id.month_number);

        prevYear = view.findViewById(R.id.prev_year);
        prevYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevYear();
            }
        });
        nextYear = view.findViewById(R.id.next_year);
        nextYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextYear();
            }
        });

        prevMonth = view.findViewById(R.id.prev_month);
        prevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevMonth();
            }
        });
        nextMonth = view.findViewById(R.id.next_month);
        nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonth();
            }
        });
        refreshList();
    }
}
