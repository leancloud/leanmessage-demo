package com.leancloud.im.guide.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.leancloud.im.guide.AVImClientManager;
import com.leancloud.im.guide.LetterView;
import com.leancloud.im.guide.adapter.MembersAdapter;
import com.leancloud.im.guide.R;
import com.leancloud.im.guide.event.MemberLetterEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wli on 15/8/14.
 */
public class AVSquareMembersActivity extends AVEventBaseActivity {

  private Toolbar toolbar;
  private MembersAdapter itemAdapter;
  private SwipeRefreshLayout refreshLayout;
  private AVIMConversation conversation;
  private LetterView letterView;
  private SearchView searchView;
  LinearLayoutManager layoutManager;

  private List<String> memberList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_square_members);

    toolbar = (Toolbar) findViewById(R.id.toolbar);

    setSupportActionBar(toolbar);
    toolbar.setNavigationIcon(R.drawable.btn_navigation_back);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_square_members_rv_list);
    refreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_square_members_srl_list);
    letterView = (LetterView) findViewById(R.id.activity_square_members_letterview);

    setTitle("在线成员列表");

    layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);

    letterView.setLetters(getSortLetters());
    itemAdapter = new MembersAdapter(this);
    recyclerView.setAdapter(itemAdapter);

    refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        conversation.fetchInfoInBackground(new AVIMConversationCallback() {
          @Override
          public void done(AVIMException e) {
            getMembers();
            refreshLayout.setRefreshing(false);
          }
        });
      }
    });
    getMembers();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_member_menu, menu);

    searchView = (SearchView)menu.findItem(R.id.activity_member_menu_search).getActionView();
    searchView.setOnCloseListener(new SearchView.OnCloseListener() {
      @Override
      public boolean onClose() {
        return false;
      }
    });

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        itemAdapter.setMemberList(filterMembers(newText));
        itemAdapter.notifyDataSetChanged();
        return false;
      }
    });
    return true;
  }

  private List<String> filterMembers(String content) {
    List<String> members = new ArrayList<String>();
    for (String name : memberList) {
      if (name.contains(content)) {
        members.add(name);
      }
    }
    return members;
  }

  private void getMembers() {
    conversation = AVImClientManager.getInstance().getClient().getConversation("551a2847e4b04d688d73dc54");
    memberList = conversation.getMembers();
    if (null != memberList && memberList.size() > 0) {
      itemAdapter.setMemberList(memberList);
      itemAdapter.notifyDataSetChanged();
    } else {
      conversation.fetchInfoInBackground(new AVIMConversationCallback() {
        @Override
        public void done(AVIMException e) {
          memberList = conversation.getMembers();
          itemAdapter.setMemberList(memberList);
          itemAdapter.notifyDataSetChanged();
        }
      });
    }
  }

  public void onEvent(MemberLetterEvent event) {
    Character targetChar = Character.toLowerCase(event.letter);
    if (itemAdapter.getIndexMap().containsKey(targetChar)) {
      int index = itemAdapter.getIndexMap().get(targetChar);
      if (index > 0 && index < itemAdapter.getItemCount()) {
        layoutManager.scrollToPositionWithOffset(index, 0);
      }
    }
  }

  private List<Character> getSortLetters() {
    List<Character> letterList = new ArrayList<Character>();
    for (char c = 'A'; c <= 'Z'; c++) {
      letterList.add(c);
    }
    return letterList;
  }
}
