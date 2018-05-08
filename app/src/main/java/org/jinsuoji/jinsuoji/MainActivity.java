package org.jinsuoji.jinsuoji;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.jinsuoji.jinsuoji.data_access.DBHelper;
import org.jinsuoji.jinsuoji.data_access.ExpenseDAO;
import org.jinsuoji.jinsuoji.model.Expense;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 主活动类，包括了：
 * + 抽屉 DrawerLayout
 *   - 左抽屉导航界面NavigationView
 *   - 主界面ConstraintsLayout
 *     - 顶部标题栏（用户按钮等）ActionBar(ToolBar)
 *     - 底部标签页BottomNavigationView
 *     - 中间用来切换显示的部分Fragment(id/contentContainer)(要切换)
 */
public class MainActivity extends AppCompatActivity implements
        TodoListFragment.OnFragmentInteractionListener,
        CalendarFragment.OnFragmentInteractionListener,
        ExpenditureFragment.OnFragmentInteractionListener,
        ExpenditureListFragment.OnFragmentInteractionListener {

    BottomNavigationView navigation;
    DrawerLayout drawer;
    NavigationView leftDrawer;
    ImageButton toolbarAdd;
    ViewPager pager;

    public void onFragmentInteraction() {
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
            case R.id.navigation_home:
                pager.setCurrentItem(0);
                return true;
            case R.id.navigation_todo:
                pager.setCurrentItem(1);
                return true;
            case R.id.navigation_expenditure:
                pager.setCurrentItem(2);
                return true;
            case R.id.navigation_zhongcao:
                pager.setCurrentItem(3);
                return true;
            }
            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        pager = findViewById(R.id.content_container);

        List<String> stringList = new ArrayList<>();
        stringList.add(getString(R.string.title_home));
        stringList.add(getString(R.string.title_todo));
        stringList.add(getString(R.string.title_expenditure));
        stringList.add(getString(R.string.title_zhongcao));
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(CalendarFragment.newInstance());
        fragments.add(TodoListFragment.newInstance());
        fragments.add(ExpenditureFragment.newInstance());
        fragments.add(CalendarFragment.newInstance());
        pager.setAdapter(new PagerAdapter(getSupportFragmentManager(), stringList, fragments));
        pager.setOffscreenPageLimit(2);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                navigation.setSelectedItemId(navigation.getMenu().getItem(position).getItemId());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        drawer = findViewById(R.id.drawer);
        leftDrawer = findViewById(R.id.left_drawer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            android.widget.Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawer.openDrawer(leftDrawer);
                }
            });
        } else {
            android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawer.openDrawer(leftDrawer);
                }
            });
        }
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.content_container);
        if (fragment == null) {
            manager.beginTransaction()
                    .add(R.id.content_container, new CalendarFragment())
                    .commit();
        }
        toolbarAdd = findViewById(R.id.toolbar_add);
        toolbarAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, R.string.placeholder, Toast.LENGTH_SHORT)
                //        .show();
                switch (navigation.getSelectedItemId()) {
                case R.id.navigation_home:{
                }   break;
                case R.id.navigation_todo:{

                }   break;
                case R.id.navigation_expenditure:{
                    Intent intent = new Intent(MainActivity.this, ExpenseEditActivity.class);
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    intent.putExtra("org.jinsuoji.jinsuoji.Time", c.getTime());
                    startActivityForResult(intent, 1);
                }   break;
                case R.id.navigation_zhongcao:{
                }   break;
                }
            }
        });
        leftDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.sync_settings:
                        Toast.makeText(MainActivity.this, new ExpenseDAO(MainActivity.this).getAllExpenseNames().toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.clear_all:
                        Toast.makeText(MainActivity.this, R.string.recreate_database, Toast.LENGTH_SHORT).show();
                        DBHelper dbHelper = new DBHelper(MainActivity.this);
                        dbHelper.recreateTables();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Expense expense = (Expense) data.getSerializableExtra(ExpenseEditActivity.LAST_EXPENSE);
            ExpenseDAO expenseDAO = new ExpenseDAO(this);
            expenseDAO.addExpense(expense);
            if (navigation.getSelectedItemId() == R.id.navigation_expenditure) {
                try {
                    ExpenditureFragment fragment = (ExpenditureFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.content_container);
                    fragment.refreshList();
                } catch (ClassCastException e) {
                    return;
                }
            }
        }
    }
}
