package net.sjh.italker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.yalantis.ucrop.UCrop;

import net.sjh.italker.common.app.Activity;
import net.sjh.italker.common.app.Fragment;
import net.sjh.italker.push.R;
import net.sjh.italker.push.frags.account.UpdateInfoFragment;

public class AccountActivity extends Activity {
    private Fragment mFragment;

    /**
     * 账户activity显示入口
     *
     * @param context 上下文
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, AccountActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_account;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mFragment = new UpdateInfoFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container, mFragment)
                .commit();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFragment.onActivityResult(requestCode, resultCode, data);
    }
}
