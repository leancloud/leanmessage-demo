//
//  BaseChatViewController.h
//  LeanMessageDemo
//
//  Created by LeanCloud on 15/5/14.
//  Copyright (c) 2015年 leancloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AVOSCloudIM.h>
#import "MessageToolBarView.h"

static NSInteger kPageSize = 5;
/**
 * 创建 BaseChatViewController 的目的是为了实现聊天页面的组件的共用
 * 群聊和私聊在逻辑上都是一个对话
 * 在界面显示的时候，都用用来聊天记录的 Tabel View 以及输入框和发送按钮，因此将这些公用元素都放在父类里面。
 * 这仅仅是当前这个 Demo 的一个用法，并不代表所有开发者都需要遵循如此的继承关系， UI 展现是 LeanCloud 的用户更为专业的领域。
 */

@class BaseChatViewController;

@interface BaseChatViewController :UIViewController<UITableViewDataSource, UITableViewDelegate, AVIMClientDelegate>


@property (strong, nonatomic) IBOutlet UITableView *messageTableView;
@property (nonatomic, strong) NSMutableArray *messages;
@property (nonatomic, strong) UIRefreshControl *refreshControl;
@property (nonatomic, strong) AVIMClient *imClient;
@property (nonatomic,strong) AVIMConversation *currentConversation;
@property (nonatomic,strong) MessageToolBarView *messageToolBar;
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath;
-(void)initMessageToolBar;
@end
