package com.leancloud.im.guide.activity;

import android.os.Bundle;
import android.os.Handler;

import com.leancloud.im.guide.R;

/**
 * Created by wli on 15/8/20.
 * Launch 页面
 */
public class AVLaunchActivity extends AVBaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_launch);

    /**
     * 默认等待 1.5 秒后跳转到登陆页面
     */
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        startActivity(AVLoginActivity.class);
        finish();
      }
    }, 1500);
  }
}
