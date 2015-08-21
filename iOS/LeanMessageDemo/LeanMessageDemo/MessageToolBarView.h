//
//  MessageToolBarView.h
//  LeanMessageDemo
//
//  Created by WuJun on 8/20/15.
//  Copyright (c) 2015 LeanCloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AVOSCloudIM.h>

@interface MessageToolBarView : UIView
@property (nonatomic,strong) AVIMConversation *currentConversation;
- (IBAction)sendMessageClicked:(id)sender;
@property (strong, nonatomic) IBOutlet UITextField *messageInputTextField;
@property (strong, nonatomic) IBOutlet UIView *view;
@end
