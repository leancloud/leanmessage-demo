//
//  MessageToolBarView.m
//  LeanMessageDemo
//
//  Created by WuJun on 8/20/15.
//  Copyright (c) 2015 LeanCloud. All rights reserved.
//

#import "MessageToolBarView.h"

@implementation MessageToolBarView

/**
 *  点击「发送」按钮，将消息发送到当前的聊天室
 */
- (IBAction)sendMessageClicked:(id)sender {
    AVIMTextMessage *textMessage = [AVIMTextMessage messageWithText:self.messageInputTextField.text attributes:nil];
    [self.currentConversation sendMessage:textMessage callback:^(BOOL succeeded, NSError *error) {
        if (error) {
            // 出错了，可能是网络问题无法连接 LeanCloud 云端，请检查网络之后重试。
            // 此时聊天服务不可用。
            UIAlertView *view = [[UIAlertView alloc] initWithTitle:@"聊天不可用！" message:[error description] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [view show];
        }
        else{
            //[self.messages addObject:textMessage];
            //[self.messageTableView reloadData];
            UIAlertView *view = [[UIAlertView alloc] initWithTitle:@"发送成功！" message:[error description] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            self.messageInputTextField.text = @"";
            [view show];
        }
    }];
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/


@end
