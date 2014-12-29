package com.yang.toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.ShareActionProvider;
import android.widget.Toast;
import android.support.v7.graphics.Palette;
import android.view.Window;

import com.yang.toolbar.widget.PagerSlidingTabStrip;

public class MainActivity extends ActionBarActivity {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Vincent");
        mToolbar.setSubtitle("我的单词本");
        setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.primaryColor));

        //显示导航栏左侧的返回按键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //设置抽屉菜单
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,mToolbar, R.string.drawer_open, R.string.drawer_close);

        //Call syncState() from your Activity's onPostCreate to synchronize the indicator
        // with the state of the linked DrawerLayout after onRestoreInstanceState has occurred.
        //调用syncState()方法后，导航图标代替了原有的"<-"返回图标？？？
        mDrawerToggle.syncState();
        //mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        //设置tabs
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        //设置pager和适配器
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mPagerSlidingTabStrip.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
                //no action needs to be done here
            }

            @Override
            public void onPageSelected(int position) {
                //当页面被选定后，调用方法 colorChange() 来重置mToolbar的色调
                colorChange(position);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                //no action needs to be done here
            }
        });

        //mPagerSlidingTabStrip组件设置默认值
        initTabsValue();
    }

    /**
     *  mPagerSlidingTabStrip组件设置默认值
     */
    private void initTabsValue() {
        //底部游标颜色
        mPagerSlidingTabStrip.setIndicatorColor(Color.BLUE);
        //Tab的分隔线颜色
        mPagerSlidingTabStrip.setDividerColor(Color.TRANSPARENT);
        //Tab背景色
        mPagerSlidingTabStrip.setBackgroundColor(Color.parseColor("#4876FF"));
        //Tab底线高度
        mPagerSlidingTabStrip.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        //游标高度，即选中的Tab下面的下划线高度
        mPagerSlidingTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
        //选中的Tab文字颜色
        mPagerSlidingTabStrip.setSelectedTextColor(Color.WHITE);
        //未选中的Tab文字颜色
        mPagerSlidingTabStrip.setTextColor(Color.BLACK);
    }

    /**
     *  重置mToolbar的色调
     */
    @SuppressLint("New API")
    private void colorChange(int position) {
        //用来提取颜色的Bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                              SuperAwesomeCardFragment.getBackgroundBitmapPosition(position));
        //Palette的部分
        Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
            //提取完之后的回调方法
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrant = palette.getVibrantSwatch();

                //界面颜色UI统一性处理，看起来更Material一些
                mPagerSlidingTabStrip.setBackgroundColor(vibrant.getRgb());
                mPagerSlidingTabStrip.setTextColor(vibrant.getTitleTextColor());

                //同理，状态栏、游标、底部导航栏等的颜色如果需要加深的话，参照设置游标的方式
                mPagerSlidingTabStrip.setIndicatorColor(colorBurn(vibrant.getRgb()));

                mToolbar.setBackgroundColor(vibrant.getRgb());
                if (Build.VERSION.SDK_INT >= 21) {
                    Window window = getWindow();

                    //以下两个属性是新API才有的
                    window.setStatusBarColor(colorBurn(vibrant.getRgb()));
                    window.setNavigationBarColor(colorBurn(vibrant.getRgb()));
                }
            }
        });
    }

    /**
     * 颜色加深处理
     *
     * @param RGBValues
     *            RGB的值，由alpha（透明度）、red（红）、green（绿）、blue（蓝）构成，
     *            Android中我们一般使用它的16进制，
     *            例如："#FFAABBCC",最左边到最右每两个字母就是代表alpha（透明度）、
     *            red（红）、green（绿）、blue（蓝）。每种颜色值占一个字节(8位)，值域0~255
     *            所以下面使用移位的方法可以得到每种颜色的值，然后每种颜色值减小一下，在合成RGB颜色，颜色就会看起来深一些了
     * @return
     */
    private int colorBurn(int RGBValues){
        int alpha = RGBValues >> 24;
        int red = RGBValues >> 16 & 0xFF;
        int green = RGBValues >> 8 & 0xFF;
        int blue = RGBValues & 0xFF;
        red = (int) Math.floor(red * (1 - 0.1));
        green = (int) Math.floor(green * (1 - 0.1));
        blue = (int) Math.floor(blue * (1 - 0.1));
        return Color.argb(alpha, red, green, blue);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        // ShareActionProvider配置
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.action_share));
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/*");
        mShareActionProvider.setShareIntent(intent);
        return super.onCreateOptionsMenu(menu);

        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                ToastMsg("[action_settings] is selected");
                break;
            case R.id.action_share:
                ToastMsg("[action_share] is selected");
                break;
            case R.id.action_search:
                //此处无反应，应该是被其SearchView类截获处理了
                ToastMsg("[action_search] is selected");
                break;
        }
        //return true;

        return super.onOptionsItemSelected(item);
    }

    private void ToastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /* ** *** */
    /**
     *  mViewPager 的适配器
     */
    public class MyPagerAdapter extends FragmentPagerAdapter {
        private final String[] titles = {"分类", "主页", "热门推荐", "热门收藏", "本月热榜", "今日热榜", "专栏", "随机"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            return SuperAwesomeCardFragment.newInstance(position);
        }
    }
}
