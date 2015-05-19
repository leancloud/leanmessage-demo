package com.leancloud.im.guide;

/**
 * Created by lzw on 15/5/19.
 */

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;

import java.util.List;

public interface AVIMTypedMessageArrayCallback {
  void done(List<AVIMTypedMessage> typedMessages, AVException e);
}
