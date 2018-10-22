package net.sjh.italker.push;


import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.widget.FloatActionButton;
import net.sjh.italker.common.app.Activity;
import net.sjh.italker.push.frags.main.ActiveFragment;
import net.sjh.italker.push.frags.main.ContactFragment;
import net.sjh.italker.push.frags.main.GroupFragment;
import net.sjh.italker.push.helper.NavHelper;
import java.util.Objects;
import butterknife.BindView;
import butterknife.OnClick;
import widget.PortraitView;


public class MainActivity extends Activity
        implements BottomNavigationView.OnNavigationItemSelectedListener, NavHelper.OnTabChangedListener<Integer> {

    @BindView(R.id.appbar)
    View mLayAppbar;
    @BindView(R.id.im_portrait)
    PortraitView mImPortrait;
    @BindView(R.id.txt_title)
    TextView mTxtTitle;
    @BindView(R.id.lay_container)
    FrameLayout mLayContainer;
    @BindView(R.id.btn_action)
    FloatActionButton mBtnAction;
    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;
    private NavHelper<Integer> mNavHelper;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        //初始化底部辅助工具类
        mNavHelper = new NavHelper<Integer>(this, R.id.lay_container, getSupportFragmentManager(), this);
        mNavHelper.add(R.id.action_home, new NavHelper.Tab<Integer>(ActiveFragment.class, R.string.title_home))
                .add(R.id.action_group, new NavHelper.Tab<Integer>(GroupFragment.class, R.string.title_group))
                .add(R.id.action_contact, new NavHelper.Tab<Integer>(ContactFragment.class, R.string.title_contact));

        //添加对底部按钮的监听
        mNavigation.setOnNavigationItemSelectedListener(this);

        Glide.with(this)
                .load(R.drawable.bg_src_morning)
                .centerCrop()
                .into(new ViewTarget<View, GlideDrawable>(mLayAppbar) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setBackground(resource.getCurrent());
                    }
                });
    }

    @Override
    protected void initData() {
        super.initData();
        //从底部导航中接管menu
        Menu menu = mNavigation.getMenu();
        //手动触发第一次点击
        menu.performIdentifierAction(R.id.action_home, 0);
    }

    @OnClick(R.id.im_search)
    void onSearchMenuClick() {

    }


    /**
     * 当我们的底部导航被点击的时候出发
     *
     * @param menuItem
     * @return 当前点击选项卡显示的界面
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //转接事件流到工具类中
        return mNavHelper.performClickMenu(menuItem.getItemId());
    }

    /**
     * NavHelper 处理后回调的方法
     *
     * @param newTab 新tab
     * @param oldTab 旧Tab
     */
    @Override
    public void onTabCahnged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
        //从额外字段中去除Title的资源ID
        mTxtTitle.setText(newTab.extra);

        //对浮动按钮进行隐藏于显示的动画
        float transY = 0;
        float rotation = 0;
        if (Objects.equals(newTab.extra, R.string.title_home)) {
            transY = Ui.dipToPx(getResources(),80);
        }else{
            //transY 默认为0 则显示
            if (Objects.equals(newTab.extra, R.string.title_group)) {
                //群
                mBtnAction.setImageResource(R.drawable.ic_group_add);
                rotation = -360;
            }else{
                //联系人
                mBtnAction.setImageResource(R.drawable.ic_contact_add);
                rotation = 360;
            }

        }
        //开始动画
        //旋转，y轴位移，弹性差值器，时间
        mBtnAction.animate()
                .rotation(rotation)
                .translationY(transY)
                .setInterpolator(new AnticipateOvershootInterpolator(1))
                .setDuration(480)
                .start();

    }
}
