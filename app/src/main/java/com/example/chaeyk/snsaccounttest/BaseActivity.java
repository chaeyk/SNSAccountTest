package com.example.chaeyk.snsaccounttest;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

//import com.kakao.sdk.sample.common.widget.WaitingDialog;

/**
 * @author leoshin, created at 15. 7. 20..
 */
public class BaseActivity extends FragmentActivity {
    protected static Activity self;

    @Override
    protected void onResume() {
        super.onResume();
        GlobalApplication.setCurrentActivity(this);
        self = BaseActivity.this;
    }

    @Override
    protected void onPause() {
        clearReferences();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences() {
        Activity currActivity = GlobalApplication.getCurrentActivity();
        if (currActivity != null && currActivity.equals(this)) {
            GlobalApplication.setCurrentActivity(null);
        }
    }

    /*
    protected static void showWaitingDialog() {
        WaitingDialog.showWaitingDialog(self);
    }

    protected static void cancelWaitingDialog() {
        WaitingDialog.cancelWaitingDialog();
    }
    */

    protected void redirectActivity(Class clazz) {
        final Intent intent = new Intent(this, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    protected void redirectMainActivity() {
        redirectActivity(MainActivity.class);
    }
}
