package org.jinsuoji.jinsuoji;

import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.jinsuoji.jinsuoji.data_access.DBWrapper;
import org.jinsuoji.jinsuoji.data_access.ExpenseDAO;
import org.jinsuoji.jinsuoji.data_access.Serializer;
import org.jinsuoji.jinsuoji.data_access.TodoDAO;
import org.jinsuoji.jinsuoji.model.Expense;
import org.jinsuoji.jinsuoji.model.Todo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 主活动类，包括了：
 * <ul>
 * <li>抽屉 DrawerLayout
 *   <li>左抽屉导航界面NavigationView</li>
 *   <li>主界面ConstraintsLayout
 *      <li>顶部标题栏（用户按钮等）<!--ActionBar-->ToolBar</li>
 *      <li>底部标签页BottomNavigationView</li>
 *      <li>中间用来切换显示的部分ViewPager<!--Fragment(id/contentContainer)(要切换)--></li>
 *   </li>
 * </li>
 * </ul>
 */
public class MainActivity extends AppCompatActivity implements
        TodoListFragment.OnFragmentInteractionListener,
        CalendarFragment.OnFragmentInteractionListener,
        ExpenditureFragment.OnFragmentInteractionListener,
        ExpenditureListFragment.OnFragmentInteractionListener {

    private static final int CREATE_EXPENSE = 1;
    private static final int CREATE_TODO = 2;
    BottomNavigationView navigation;
    DrawerLayout drawer;
    NavigationView leftDrawer;
    ImageButton toolbarAdd;
    ViewPager pager;


    List<String> stringList;
    List<Fragment> fragments;

    public void onFragmentInteraction() {
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
            case R.id.navigation_home:
                pager.setCurrentItem(0);
                ((CalendarFragment) fragments.get(0)).refreshList();
                return true;
            case R.id.navigation_todo:
                pager.setCurrentItem(1);
                ((TodoListFragment) fragments.get(1)).refreshList();
                return true;
            case R.id.navigation_expenditure:
                pager.setCurrentItem(2);
                ((ExpenditureFragment) fragments.get(2)).refreshList();
                return true;
            //case R.id.navigation_zhongcao:
            //    Toast.makeText(MainActivity.this, getString(R.string.placeholder),
            //            Toast.LENGTH_SHORT).show();
            //    //pager.setCurrentItem(3);
            //    return false;
            }
            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Preference.isGuided(this)) {
            Intent intent = new Intent();
            intent.setClass(this, GuideActivity.class);
            startActivity(intent);
            finish();
        }

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        pager = findViewById(R.id.content_container);

        stringList = new ArrayList<>();
        stringList.add(getString(R.string.title_home));
        stringList.add(getString(R.string.title_todo));
        stringList.add(getString(R.string.title_expenditure));
        // stringList.add(getString(R.string.title_zhongcao));
        fragments = new ArrayList<>();
        fragments.add(CalendarFragment.newInstance());
        fragments.add(TodoListFragment.newInstance());
        fragments.add(ExpenditureFragment.newInstance());
        // fragments.add(CalendarFragment.newInstance());
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
        final Fragment fragment = manager.findFragmentById(R.id.content_container);
        if (fragment == null) {
            manager.beginTransaction()
                    .add(R.id.content_container, new CalendarFragment())
                    .commit();
        }
        toolbarAdd = findViewById(R.id.toolbar_add);
        toolbarAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (navigation.getSelectedItemId()) {
                case R.id.navigation_home:{
                    new AlertDialog.Builder(MainActivity.this).setTitle(R.string.create)
                            .setItems(new String[]{getString(R.string.create_todo),
                                        getString(R.string.create_expenditure)},
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            dialog.dismiss();
                                            showCreateTodo(((CalendarFragment) fragments.get(0)).getCurrent());
                                        } else if (which == 1) {
                                            dialog.dismiss();
                                            showCreateExpense(((CalendarFragment) fragments.get(0)).getCurrent());
                                        } else {
                                            dialog.cancel();
                                        }
                                    }
                                })
                            .setCancelable(true)
                            .show();
                }   break;
                case R.id.navigation_todo:{
                    showCreateTodo(Calendar.getInstance());
                }   break;
                case R.id.navigation_expenditure:{
                    showCreateExpense(Calendar.getInstance());
                }   break;
                //case R.id.navigation_zhongcao:{
                //
                //}   break;
                }
            }
        });
        leftDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                case R.id.personal_info:
                case R.id.about:
                case R.id.feedback:
                    // TODO 这些个菜单项
                    Toast.makeText(MainActivity.this, R.string.placeholder, Toast.LENGTH_SHORT)
                            .show();
                    break;
                case R.id.sync_settings:
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.load_example:
                    Toast.makeText(MainActivity.this, R.string.load_example, Toast.LENGTH_SHORT).show();
                    new Serializer(MainActivity.this).loadExample();
                    return true;
                case R.id.clear_all:
                    Toast.makeText(MainActivity.this, R.string.recreate_database, Toast.LENGTH_SHORT).show();
                    new DBWrapper(MainActivity.this).recreateTables();
                    Preference.clear(MainActivity.this);
                    return true;
                }
                return false;
            }
        });
    }

    private void showCreateExpense(Calendar calendar) {
        Intent intent = new Intent(MainActivity.this, ExpenseEditActivity.class);
        intent.putExtra(ExpenseEditActivity.TIME, calendar.getTime());
        startActivityForResult(intent, CREATE_EXPENSE);
    }

    private void showCreateTodo(Calendar calendar) {
        Intent intent = new Intent(MainActivity.this, TodoEditActivity.class);
        intent.putExtra(TodoEditActivity.TIME, calendar.getTime());
        startActivityForResult(intent, CREATE_TODO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CREATE_EXPENSE: {
                    Expense expense = (Expense) data.getSerializableExtra(ExpenseEditActivity.LAST_EXPENSE);
                    ExpenseDAO expenseDAO = new ExpenseDAO(this);
                    expenseDAO.addExpense(expense);
                    if (navigation.getSelectedItemId() == R.id.navigation_expenditure) {
                        try {
                            ((ExpenditureFragment) fragments.get(pager.getCurrentItem())).refreshList();
                        } catch (ClassCastException ignored) {
                        }
                    }
                } break;
                case CREATE_TODO: {
                    Todo todo = (Todo) data.getSerializableExtra(TodoEditActivity.LAST_TODO);
                    TodoDAO todoDAO = new TodoDAO(this);
                    todoDAO.addTodo(todo);
                    if (navigation.getSelectedItemId() == R.id.navigation_todo) {
                        try {
                            ((TodoListFragment) fragments.get(pager.getCurrentItem())).refreshList();
                        } catch (ClassCastException ignored) {
                        }
                    }
                } break;
                default:
                    // 这是Fragment调用的Activity在返回，不作处理
            }
        }
    }
}
