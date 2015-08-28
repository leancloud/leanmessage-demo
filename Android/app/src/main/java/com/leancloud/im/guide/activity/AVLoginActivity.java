package com.leancloud.im.guide.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationMemberCountCallback;
import com.leancloud.im.guide.AVImClientManager;
import com.leancloud.im.guide.Constants;
import com.leancloud.im.guide.R;

/**
 * Created by wli on 15/8/13.
 */
public class AVLoginActivity extends AVBaseActivity {

  private EditText userNameView;
  private Button loginButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

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

    loginButton.setEnabled(false);
    userNameView.setEnabled(false);
    AVImClientManager.getInstance().open(selfId, new AVIMClientCallback() {
      @Override
      public void done(AVIMClient avimClient, AVIMException e) {
        loginButton.setEnabled(true);
        userNameView.setEnabled(true);
        if (filterException(e)) {
          Intent intent = new Intent(AVLoginActivity.this, AVSquareActivity.class);
          intent.putExtra(Constants.CONVERSATION_ID, Constants.SQUARE_CONVERSATION_ID);
          intent.putExtra(Constants.ACTIVITY_TITLE, getString(R.string.square_name));
          startActivity(intent);
          finish();
        }
      }
    });
  }
}
