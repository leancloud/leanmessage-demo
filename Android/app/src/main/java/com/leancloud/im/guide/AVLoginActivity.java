package com.leancloud.im.guide;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;

/**
 * Created by wli on 15/8/13.
 */
public class AVLoginActivity extends AVBaseActivity {

  private TextView onlineNumView;
  private EditText userNameView;
  private Button loginButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    onlineNumView = (TextView) findViewById(R.id.activity_login_tv_online_num);
    userNameView = (EditText) findViewById(R.id.activity_login_et_username);
    loginButton = (Button) findViewById(R.id.activity_login_btn_login);

    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openClient(userNameView.getText().toString().trim());
      }
    });
  }

  private void openClient(String selfId) {
    if (TextUtils.isEmpty(selfId)) {
      showToast(R.string.login_null_name_tip);
      return;
    }

    AVImClientManager.getInstance().open(selfId, new AVIMClientCallback() {
      @Override
      public void done(AVIMClient avimClient, AVIMException e) {
        if (filterException(e)) {
          startActivity(AVSquareActivity.class);
          finish();
        }
      }
    });
  }
}
