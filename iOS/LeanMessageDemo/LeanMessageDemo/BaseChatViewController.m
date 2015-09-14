//
//  MainViewController.m
//  LeanMessageDemo
//
//  Created by LeanCloud on 15/5/14.
//  Copyright (c) 2015年 leancloud. All rights reserved.
//

#import "BaseChatViewController.h"
#import "TextMessageTableViewCell.h"
#import "LeftTextMessageTableViewCell.h"
#import "RightTextMessageTableViewCell.h"
#import "TextMessageTableViewCell.h"
#import "MessageTableViewCell.h"


#define kConversationId @"55cd829e60b2b52cda834469"


// 自定义属性来区分单聊和群聊
typedef enum : NSUInteger {
    ConversationTypeOneToOne = 0,
    ConversatinoTypeGroup
}ConversationType;

@interface BaseChatViewController ()

@end

@implementation BaseChatViewController
// 设置当前对话
-(void)setCurrentConversation:(AVIMConversation *)currentConversation{
    _currentConversation = currentConversation;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationController.navigationBar.barTintColor = [UIColor colorWithRed:44.0/255.0 green:151.0/255.0 blue:235.0/255.0 alpha:1.0];
    self.navigationController.navigationBar.tintColor =[UIColor whiteColor];
    [self.navigationController.navigationBar setTitleTextAttributes:@{NSForegroundColorAttributeName : [UIColor whiteColor]}];

    /**
     * 初始化必要的数据
     */
    _messages = [NSMutableArray array];
    /**
     *  为显示聊天记录的 TableView 数据源
     */
    self.messageTableView.dataSource = self;
    self.messageTableView.delegate = self;
    
    /**
     *  初始化页面上的控件
     */
    
    self.imClient = [AVIMClient defaultClient];
    self.imClient.delegate = self;
    
    UITapGestureRecognizer *singleFingerTap =
    [[UITapGestureRecognizer alloc] initWithTarget:self
                                            action:@selector(handleSingleTap:)];
    [self.view addGestureRecognizer:singleFingerTap];
    /**
     * 为消息列表添加下拉刷新控件，每一次下拉都会增加 10 条聊天记录
     */
    [self.messageTableView addSubview:self.refreshControl];
    
    //[self.messageTableView removeFromSuperview];
}
- (void)handleSingleTap:(UITapGestureRecognizer *)recognizer {
    [self.messageToolBar.messageInputTextField resignFirstResponder];
    NSLog(@"sdd");
}
// 接收消息的回调函数
- (void)conversation:(AVIMConversation *)conversation didReceiveTypedMessage:(AVIMTypedMessage *)message {
    
    UIAlertView *view = [[UIAlertView alloc] initWithTitle:@"收到新的消息" message:@"" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [view show];
    
    if ([message.conversationId isEqualToString:self.currentConversation.conversationId]) {
        [self.messages addObject:message];
        [self.messageTableView reloadData];
    }
}
-(void)conversation:(AVIMConversation *)conversation didReceiveCommonMessage:(AVIMMessage *)message{
    UIAlertView *view = [[UIAlertView alloc] initWithTitle:@"收到新的消息" message:@"" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [view show];
}
/*
 * 获取了对话实例之后，把消息输入框以及发送按钮的 UIView 从 Xib 文件里面读取出来，并且渲染在当前页面上
 */
-(void)initMessageToolBar{
    
    self.messageToolBar = [[MessageToolBarView alloc] init];
    self.messageToolBar.currentConversation= self.currentConversation;
    
    __weak typeof(self) weakSelf = self;
    self.messageToolBar.messageSentBlock = ^(AVIMMessage *message){
        [weakSelf.messages addObject:message];
        [weakSelf.messageTableView reloadData];
    };
    /*
     * 添加相关的视图约束，将 messageToolBar 置于整个 View 的底部
     */
    [self.view addSubview:self.messageToolBar];
    self.messageToolBar.translatesAutoresizingMaskIntoConstraints = NO;
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:self.messageToolBar
                                                          attribute:NSLayoutAttributeBottom
                                                          relatedBy:NSLayoutRelationEqual
                                                             toItem:self.view
                                                          attribute:NSLayoutAttributeBottom
                                                         multiplier:1.0
                                                           constant:0.0]];
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:self.messageToolBar
                                                          attribute:NSLayoutAttributeLeading
                                                          relatedBy:NSLayoutRelationEqual
                                                             toItem:self.view
                                                          attribute:NSLayoutAttributeLeading
                                                         multiplier:1.0
                                                           constant:0.0]];
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:self.messageToolBar
                                                          attribute:NSLayoutAttributeTrailing
                                                          relatedBy:NSLayoutRelationEqual
                                                             toItem:self.view
                                                          attribute:NSLayoutAttributeTrailing
                                                         multiplier:1.0
                                                           constant:0.0]];
    [self.messageToolBar updateConstraintsIfNeeded];
    [self.messageToolBar layoutIfNeeded];
    self.messageToolBar.view.frame=self.messageToolBar.bounds;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}
#pragma set Refresh Control
- (UIRefreshControl *)refreshControl {
    if (_refreshControl == nil) {
        _refreshControl = [[UIRefreshControl alloc] init];
        [_refreshControl addTarget:self action:@selector(loadHistoryMessages:) forControlEvents:UIControlEventValueChanged];
        [_refreshControl setTintColor:[UIColor whiteColor]];
    }
    return _refreshControl;
}
#pragma Refresh Control getter
- (void)loadHistoryMessages:(UIRefreshControl *)refreshControl {
    
    /**
     * 下拉 Table View 的时候，从服务端获取更多的消息记录。
     */
    if (self.messages.count == 0) {
        [refreshControl endRefreshing];
        return;
    } else {
        AVIMTypedMessage *firstMessage = self.messages[0];
        [self.currentConversation queryMessagesBeforeId:nil timestamp:firstMessage.sendTimestamp limit:kPageSize callback: ^(NSArray *objects, NSError *error) {
            [refreshControl endRefreshing];
            if (error == nil) {
                NSInteger count = objects.count;
                if (count == 0) {
                    NSLog(@"no more old message");
                } else {
                    
                    // 将更早的消息记录插入到 Tabel View 的顶部
                    NSIndexSet *indexes = [NSIndexSet indexSetWithIndexesInRange:
                                           NSMakeRange(0,[objects count])];
                    [self.messages insertObjects:objects atIndexes:indexes];
                    [self.messageTableView reloadData];
                }
            }
        }];
    }
}
#pragma Draw Table
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.messages.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString *identifier = @"cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];

    /**
     * 绘画 Tabel View Cell 控件
     */
    AVIMMessage *message= self.messages[indexPath.row];
    // 判断是否为当前 ClientId 发送的消息，如果是，则该条消息会出现在列表的右侧
    BOOL isMe= [[AVIMClient defaultClient ].clientId isEqualToString:message.clientId];
    if ([message isKindOfClass:[AVIMTypedMessage class]]) {
        AVIMTypedMessage *typedMessage=(AVIMTypedMessage*)message;
        switch (typedMessage.mediaType) {
            case  kAVIMMessageMediaTypeText: {
                
                AVIMTextMessage *textMessage=(AVIMTextMessage*)typedMessage;
                TextMessageTableViewCell *textCell=[TextMessageTableViewCell cellWithTableView:tableView isMe:isMe];
                textCell.textMessage = textMessage;
                return textCell;
            }
                break;
            default:
                break;
        }
    }
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
    }
    if(message){
        cell.textLabel.text = [NSString stringWithFormat:@"%@ : %@", message.clientId, @"当前版本暂不支持显示此消息类型。"];
        return cell;
    }
    return  cell;
}
#pragma mark - actions

@end
