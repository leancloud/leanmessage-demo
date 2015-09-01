//
//  TextMessageTableViewCell.m
//  LeanMessageDemo
//
//  Created by WuJun on 8/24/15.
//  Copyright (c) 2015 LeanCloud. All rights reserved.
//

#import "LeftTextMessageTableViewCell.h"

@implementation LeftTextMessageTableViewCell

-(void)setTextMessage:(AVIMTextMessage *)textMessage{
    
    [super setTextMessage:textMessage];
    // 显示该条消息的发送者的 ClientId
    self.messageSenderClientId.text=textMessage.clientId;
    
    // 显示文本消息的内容setAttributedText
    [self.textMessageContentTextView setAttributedText:[[NSMutableAttributedString alloc] initWithString:textMessage.text]];
    
    [self layoutIfNeeded];
}

- (void)awakeFromNib {
    // Initialization code
    [super awakeFromNib];
    
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
