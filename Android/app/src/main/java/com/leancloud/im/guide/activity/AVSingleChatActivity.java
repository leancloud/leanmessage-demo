package com.leancloud.im.guide.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.leancloud.im.guide.AVImClientManager;
import com.leancloud.im.guide.Constants;
import com.leancloud.im.guide.R;
import com.leancloud.im.guide.fragment.ChatFragment;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;

/**
 * Created by wli on 15/8/14.
 * 一对一单聊的页面，需要传入 Constants.MEMBER_ID
 */
public class AVSingleChatActivity extends AVBaseActivity {

  @Bind(R.id.toolbar)
  protected Toolbar toolbar;

  protected ChatFragment chatFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_square);

    chatFragment = (ChatFragment)getFragmentManager().findFragmentById(R.id.fragment_chat);

    setSupportActionBar(toolbar);
    toolbar.setNavigationIcon(R.drawable.btn_navigation_back);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });

    String memberId = getIntent().getStringExtra(Constants.MEMBER_ID);
    setTitle(memberId);
    getConversation(memberId);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Bundle extras = intent.getExtras();
    if (null != extras && extras.containsKey(Constants.MEMBER_ID)) {
      String memberId = extras.getString(Constants.MEMBER_ID);
      setTitle(memberId);
      getConversation(memberId);
    }
  }

  /**
   * 获取 conversation，为了避免重复的创建，此处先 query 是否已经存在只包含该 member 的 conversation
   * 如果存在，则直接赋值给 ChatFragment，否者创建后再赋值
   */
  private void getConversation(final String memberId) {
    final AVIMClient client = AVImClientManager.getInstance().getClient();
    AVIMConversationQuery conversationQuery = client.getQuery();
    conversationQuery.withMembers(Arrays.asList(memberId), true);
    conversationQuery.findInBackground(new AVIMConversationQueryCallback() {
      @Override
      public void done(List<AVIMConversation> list, AVIMException e) {

        //注意：此处仍有漏洞，如果获取了多个 conversation，默认取第一个
        if (null != list && list.size() > 0) {
          chatFragment.setConversation(list.get(0));
        } else {
          client.createConversation(Arrays.asList(memberId), null, null, true, new AVIMConversationCreatedCallback() {
            @Override
            public void done(AVIMConversation avimConversation, AVIMException e) {
              chatFragment.setConversation(avimConversation);
            }
          });
        }
      }
    });
  }
}
