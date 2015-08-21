//
//  BaseTextMessageTableViewCell.m
//  LeanMessageDemo
//
//  Created by WuJun on 8/21/15.
//  Copyright (c) 2015 LeanCloud. All rights reserved.
//

#import "BaseTextMessageTableViewCell.h"

@implementation BaseTextMessageTableViewCell
-(void)setTextMessage:(AVIMTextMessage *)textMessage{
        _textMessage = textMessage;
}
- (void)awakeFromNib {
    // Initialization code
}

/**
 *  获取点击的 ClientID ，进入与该 ClientID 的单聊
 */
-(void)clientIdLabelTapped:(UIGestureRecognizer *)sender
{
    // 获取被点击的 ClientId
    UILabel* tappedClientLabel=(UILabel*)sender.view;
    UIAlertView *view = [[UIAlertView alloc] initWithTitle:tappedClientLabel.text message:@"" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    
    // 获取 AVIMConversationQuery 实例
    AVIMConversationQuery *query = [[AVIMClient defaultClient] conversationQuery];
    
    /* 构建查询条件
     * 注意：比较建议开发者仔细阅读以下三行代码，这三个条件同时进行查询在数据量日益增加的时候，也能保持查询的性能不受太大影响。
     */
    [query whereKey:@"m" containedIn:@[[AVIMClient defaultClient].clientId, tappedClientLabel.text]];
    [query whereKey:@"m" sizeEqualTo:2];
    [query whereKey:AVIMAttr(@"customConversationType")  equalTo:@(1)];
    
    // 执行查询
    [query findConversationsWithCallback:^(NSArray *objects, NSError *error) {
        if(objects.count == 0){
            NSDictionary *attributes = @{ @"customConversationType": @(1) };
            [[AVIMClient defaultClient] createConversationWithName:@"" clientIds:@[@"Jerry"] attributes:attributes options:AVIMConversationOptionNone callback:^(AVIMConversation *conversation, NSError *error) {
                
            }];
        }
    }];
    [view show];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
