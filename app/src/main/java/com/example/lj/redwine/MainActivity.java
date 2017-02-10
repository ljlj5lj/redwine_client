package com.example.lj.redwine;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lj.redwine.adapter.MainViewPagerAdapter;
import com.example.lj.redwine.fragment.NewProductFragment;
import com.example.lj.redwine.fragment.OrderFragment;
import com.example.lj.redwine.fragment.PersonFragment;
import com.example.lj.redwine.fragment.RedWineFragment;
import com.example.lj.redwine.util.ToastUtil;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements View.OnClickListener{
    ImageView[] ImageViewArray;//底部栏图片数组
    TextView[] TextViewArray;//底部栏字体数组
    int[] ImageView_selected;//选中状态
    int[] ImageView_unselected;//非选中状态
    private int currentPageIndex = 0;//初始化当前页码
    LinearLayout[] LinearLayoutArray;//底部布局数组
    ViewPager viewPager;//滑动控件
    RedWineFragment redWineFragment = new RedWineFragment();//红酒主页面
    OrderFragment orderFragment = new OrderFragment();//订单页面
    NewProductFragment newProductFragment = new NewProductFragment();//新品页面
    PersonFragment personFragment = new PersonFragment();//个人管理页面
    MainViewPagerAdapter mainViewPagerAdapter;//主页滑动控件适配器
    private ArrayList<Fragment> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) this.findViewById(R.id.viewPager);
        mainViewPagerAdapter = new MainViewPagerAdapter(this.getSupportFragmentManager(),null);
        viewPager.setAdapter(mainViewPagerAdapter);
        viewPager.setOffscreenPageLimit(4);
        InitView();//初始化控件
        InitListener();//初始化监听器
        SetBottomBarColor();//设置底部栏选中颜色
    }

    private void InitListener() {
        for (LinearLayout linearlayout : LinearLayoutArray) {
            linearlayout.setOnClickListener(this);
        }
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPageIndex = position;
                SetBottomBarColor();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void SetBottomBarColor() {
        for (int i = 0; i<LinearLayoutArray.length; i++){
            if (i == currentPageIndex) {
                ImageViewArray[i].setBackgroundResource(ImageView_selected[i]);
                TextViewArray[i].setTextColor(getResources().getColor(R.color.main_color));
            }else {
                ImageViewArray[i].setBackgroundResource(ImageView_unselected[i]);
                TextViewArray[i].setTextColor(getResources().getColor(R.color.grey));
            }
        }
    }

    private void InitView() {
        list = new ArrayList<Fragment>();
        list.add(redWineFragment);
        list.add(orderFragment);
        list.add(newProductFragment);
        list.add(personFragment);
        mainViewPagerAdapter.setList(list);
        mainViewPagerAdapter.notifyDataSetChanged();

        LinearLayoutArray = new LinearLayout[]{//初始化底部栏布局
                (LinearLayout) findViewById(R.id.red_wine_layout),
                (LinearLayout) findViewById(R.id.order_layout),
                (LinearLayout) findViewById(R.id.new_product_layout),
                (LinearLayout) findViewById(R.id.person_layout)
        };
        ImageViewArray = new ImageView[]{//初始化底部栏图片
                (ImageView) findViewById(R.id.red_wine),
                (ImageView) findViewById(R.id.order),
                (ImageView) findViewById(R.id.new_product),
                (ImageView) findViewById(R.id.person)
        };
        TextViewArray = new TextView[]{//初始化底部栏文字
                (TextView) findViewById(R.id.text_red_wine),
                (TextView) findViewById(R.id.text_order),
                (TextView) findViewById(R.id.text_new_product),
                (TextView) findViewById(R.id.text_person)
        };
        ImageView_selected = new int[]{//图片选中状态
                R.drawable.redwine_selected,
                R.drawable.order_selected,
                R.drawable.new_product_selected,
                R.drawable.person_selected
        };
        ImageView_unselected = new int[]{//图片不选中状态
                R.drawable.redwine_unselected,
                R.drawable.order_unselected,
                R.drawable.new_product_unselected,
                R.drawable.person_unselected
        };

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.red_wine_layout:
                currentPageIndex = 0;
                break;
            case R.id.order_layout:
                currentPageIndex = 1;
                break;
            case R.id.new_product_layout:
                currentPageIndex = 2;
                break;
            case R.id.person_layout:
                currentPageIndex = 3;
        }
        viewPager.setCurrentItem(currentPageIndex);
    }
}
