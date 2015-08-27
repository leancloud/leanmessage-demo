//
//  RightTextMessageTableViewCell.m
//  LeanMessageDemo
//
//  Created by WuJun on 8/25/15.
//  Copyright (c) 2015 LeanCloud. All rights reserved.
//

#import "RightTextMessageTableViewCell.h"

@implementation RightTextMessageTableViewCell
-(void)setTextMessage:(AVIMTextMessage *)textMessage{
    
    [super setTextMessage:textMessage];
    // 显示该条消息的发送者的 ClientId
    self.messageSenderClientId.text = textMessage.clientId;
    
    // 显示文本消息的内容setAttributedText
    [self.textMessageContentTextView setAttributedText:[[NSMutableAttributedString alloc] initWithString:textMessage.text]];
}
- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
