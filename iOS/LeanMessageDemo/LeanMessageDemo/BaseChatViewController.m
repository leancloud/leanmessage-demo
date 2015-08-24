//
//  MainViewController.m
//  LeanMessageDemo
//
//  Created by LeanCloud on 15/5/14.
//  Copyright (c) 2015年 leancloud. All rights reserved.
//

#import "BaseChatViewController.h"
#import "LeftTextMessageTableViewCell.h"
#import "MessageToolBarView.h"

#define kConversationId @"55cd829e60b2b52cda834469"
static NSInteger kPageSize = 5;

// 自定义属性来区分单聊和群聊
typedef enum : NSUInteger {
    ConversationTypeOneToOne = 0,
    ConversatinoTypeGroup
}ConversationType;

@interface BaseChatViewController ()<UITableViewDataSource, UITableViewDelegate, AVIMClientDelegate>

@end

@implementation BaseChatViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationController.navigationBar.barTintColor = [UIColor colorWithRed:44.0/255.0 green:151.0/255.0 blue:235.0/255.0 alpha:1.0];
    //self.navigationController.navigationBar.translucent = NO;
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
     *  首次进入会话之后，获取最近的 10条 聊天记录
     */
    AVIMClient *imClient = [AVIMClient defaultClient];
    AVIMConversationQuery *query = [imClient conversationQuery];
    [query getConversationById:kConversationId callback:^(AVIMConversation *conversation, NSError *error) {
        self.currentConversation=conversation;
        [self initMessageToolBar];
        [conversation queryMessagesWithLimit:kPageSize callback:^(NSArray *objects, NSError *error) {
            [self.messages addObjectsFromArray:objects];
            [self.messageTableView reloadData];
        }];
    }];
    
    /**
     * 为消息列表添加下拉刷新控件，每一次下拉都会增加 10 条聊天记录
     */
    [self.messageTableView addSubview:self.refreshControl];
    
    //[self.messageTableView removeFromSuperview];
}
/*
 * 获取了对话实例之后，把消息输入框以及发送按钮的 UIView 从 Xib 文件里面读取出来，并且渲染在当前页面上
 */
-(void)initMessageToolBar{
    
    MessageToolBarView *messageToolBar = [[MessageToolBarView alloc] init];
    messageToolBar.currentConversation= self.currentConversation;
    /*
     * 添加相关的视图约束，将 messageToolBar 置于整个 View 的底部
     */
    [self.view addSubview:messageToolBar];
    messageToolBar.translatesAutoresizingMaskIntoConstraints = NO;
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:messageToolBar
                                                          attribute:NSLayoutAttributeBottom
                                                          relatedBy:NSLayoutRelationEqual
                                                             toItem:self.view
                                                          attribute:NSLayoutAttributeBottom
                                                         multiplier:1.0
                                                           constant:0.0]];
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:messageToolBar
                                                          attribute:NSLayoutAttributeLeading
                                                          relatedBy:NSLayoutRelationEqual
                                                             toItem:self.view
                                                          attribute:NSLayoutAttributeLeading
                                                         multiplier:1.0
                                                           constant:0.0]];
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:messageToolBar
                                                          attribute:NSLayoutAttributeTrailing
                                                          relatedBy:NSLayoutRelationEqual
                                                             toItem:self.view
                                                          attribute:NSLayoutAttributeTrailing
                                                         multiplier:1.0
                                                           constant:0.0]];
    [messageToolBar updateConstraintsIfNeeded];
    [messageToolBar layoutIfNeeded];
    messageToolBar.view.frame=messageToolBar.bounds;
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
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
    }
    /**
     * 绘画 Tabel View Cell 控件
     */
    AVIMMessage *message= self.messages[indexPath.row];
    if ([message isKindOfClass:[AVIMTypedMessage class]]) {
        AVIMTypedMessage *typedMessage=(AVIMTypedMessage*)message;
        switch (typedMessage.mediaType) {
            case  kAVIMMessageMediaTypeText: {
                AVIMTextMessage *textMessage=(AVIMTextMessage*)typedMessage;
//                NSString* cellNamePrefix = @"Left";
//                NSString* cellName=@"TextMessageTableViewCell";
//                if([textMessage.clientId isEqualToString:[AVIMClient defaultClient].clientId]){
//                    cellNamePrefix=@"Right";
//                }
//                NSString* wholeCellName= [cellNamePrefix stringByAppendingString:cellName];
//                
//                id textMessageCellId = [[NSClassFromString(wholeCellName) alloc] init];
               
                LeftTextMessageTableViewCell *leftTestMessageCell = [[LeftTextMessageTableViewCell alloc] init];
                leftTestMessageCell.textMessage = textMessage;
                return leftTestMessageCell;
                
                
//                NSArray *nibArray = [[NSBundle mainBundle] loadNibNamed:wholeCellName owner:nil options:nil];
//                
//                LeftTextMessageTableViewCell* demoTextMessagecell = (LeftTextMessageTableViewCell*)[nibArray objectAtIndex:0];
//                [demoTextMessagecell.messageContentTextView layoutIfNeeded];
//                demoTextMessagecell.textMessage = textMessage;
//                
//                return demoTextMessagecell;
            }
                break;
            default:
                break;
        }
    }
    
    if(message){
        cell.textLabel.text = [NSString stringWithFormat:@"%@ : %@", message.clientId, @"当前版本暂不支持显示此消息类型。"];
        return cell;
    }
    return  cell;
}

#pragma mark - actions
@end
