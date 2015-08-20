package com.leancloud.im.guide;

import android.os.Bundle;
import android.os.Handler;

/**
 * Created by wli on 15/8/20.
 */
public class AVLaunchActivity extends AVBaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_launch);

    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        startActivity(AVLoginActivity.class);
        finish();
      }
    }, 1500);
  }
}
