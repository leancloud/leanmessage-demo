package com.leancloud.im.guide;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

/**
 * Created by lzw on 15/5/29.
 */
public class BaseActivity extends ActionBarActivity {

  protected boolean filterException(Exception e) {
    if (e != null) {
      e.printStackTrace();
      toast(e.getMessage());
      return false;
    } else {
      return true;
    }
  }

  protected void toast(String str) {
    Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
  }

}
