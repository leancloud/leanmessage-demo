package com.leancloud.im.guide;

import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;

/**
 * Created by wli on 15/8/13.
 */
public class AVImClientManager {

  private static AVImClientManager imClientManager;

  private AVIMClient avimClient;
  private String clientId;

  public synchronized static AVImClientManager getInstance() {
    if (null == imClientManager) {
      imClientManager = new AVImClientManager();
    }
    return imClientManager;
  }

  private AVImClientManager() {
  }

  public void open(String clientId, AVIMClientCallback callback) {
    this.clientId = clientId;
    avimClient = AVIMClient.getInstance(clientId);
    avimClient.open(callback);
  }

  public AVIMClient getClient() {
    return avimClient;
  }

  public String getClientId() {
    if (TextUtils.isEmpty(clientId)) {
      throw new IllegalStateException("Please call AVImClientManager.open first");
    }
    return clientId;
  }
}
