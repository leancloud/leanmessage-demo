package com.leancloud.im.guide.activity;


import de.greenrobot.event.EventBus;

/**
 * Created by wli on 15/8/23.
 */
public class AVEventBaseActivity extends AVBaseActivity {

  @Override
  protected void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
  }
}
