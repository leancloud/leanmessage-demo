package com.leancloud.im.guide;

/**
 * Created by lzw on 15/5/14.
 */
public enum  ConversationType {
  OneToOne(0),Group(1);
  int value;
  public static final String KEY_ATTRIBUTE_TYPE = "type";

  ConversationType(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
