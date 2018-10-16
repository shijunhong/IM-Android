package net.sjh.italker.push;


import android.os.Bundle;
import android.widget.TextView;

import net.sjh.italker.common.app.Activity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {


    @BindView(R.id.txt_test)
    TextView mTxtTest;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTxtTest.setText("hahahahah");
    }
    //    @Override
//    protected int getContentLayoutId() {
//        return R.layout.activity_main;
//    }
//
//    @Override
//    protected void initWidget() {
//        super.initWidget();
//    }

}
