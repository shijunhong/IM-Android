package net.sjh.italker.push;

import android.os.Bundle;

import net.sjh.italker.push.activities.MainActivity;
import net.sjh.italker.push.assist.PermissionsFragment;

public class LaunchActivity extends net.sjh.italker.common.app.Activity {


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionsFragment.haveAll(this,getSupportFragmentManager())){
            MainActivity.show(this);
            finish();
        }

    }
}
