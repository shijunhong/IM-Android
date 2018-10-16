package net.sjh.italker.common.app;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import butterknife.ButterKnife;

/**
 * 封装activity
 */
public abstract class Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initWindows();
        if (initArgs(getIntent().getExtras())) {
            //得到界面ID并设置到Activity界面中
            int layId = getContentLayoutId();
            setContentView(layId);
            initWidget();
            initData();
        }else {
            finish();
        }
    }


    /**
     * 初始化窗口数据
     */
    protected void initWindows(){}

    /**
     * 初始化相关数据
     * @return 如果正确返回true
     * */
    protected boolean initArgs(Bundle bundle) {
        return true;
    }

    /**
     * 得到当前界面的资源文件ID
     * @return 资源文件id
     */
    protected abstract int getContentLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget() {
        ButterKnife.bind(this);
    }

    /**
     * 初始化数据
     */
    protected void initData() {
    }

    @Override
    public boolean onSupportNavigateUp() {
        //当点击界面导航返回时，finish当前界面
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        //判断是否为空
        if (fragments!=null&&fragments.size()>0){
            for (Fragment fragment:fragments){
                //判断是否是我们能够处理的fragment类型
                if (fragment instanceof net.sjh.italker.common.app.Fragment){
                    //判断是否拦截了返回按钮
                    if (((net.sjh.italker.common.app.Fragment) fragment).onBackPressed()){
                        return;
                    }
                }
            }
        }

        super.onBackPressed();
        finish();
    }
}
