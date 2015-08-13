package com.leancloud.im.guide;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.widget.Toast;

/**
 * Created by wli on 15/8/13.
 */
public class AVBaseActivity extends AppCompatActivity {
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

  protected void showToast(String content) {
    Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
  }

  protected void showToast(int resId) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
  }


  protected void startActivity(Class<?> cls) {
    Intent intent = new Intent(this, cls);
    startActivity(intent);
  }
}
