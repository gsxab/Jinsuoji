package org.jinsuoji.jinsuoji;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Tab+ViewPager+Fragment界面用到的Adapter.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    /**
     * Tab(如果有的话)中的字符串.
     */
    private List<String> tabNames;
    /**
     * ViewPager中的页面.
     */
    private List<Fragment> fragments;

    PagerAdapter(FragmentManager fm, List<String> tabNames, List<Fragment> fragments) {
        super(fm);
        this.tabNames = tabNames;
        this.fragments = fragments;
    }
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
    @Override
    public int getCount() {
        return fragments.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return tabNames.get(position);
    }
}
