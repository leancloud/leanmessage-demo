package com.leancloud.im.guide;

/**
 * Created by wli on 15/8/23.
 */
public class Constants {

  private static final String LEANMESSAGE_CONSTANTS_PREFIX = "com.leancloud.im.guide";

  public static final String MEMBER_ID = getPrefixConstant("member_id");

  public static final String SQUARE_CONVERSATION_ID = "551a2847e4b04d688d73dc54";

  private static String getPrefixConstant(String str) {
    return LEANMESSAGE_CONSTANTS_PREFIX + str;
  }
}
