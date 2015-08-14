//
//  MainViewController.m
//  LeanMessageDemo
//
//  Created by LeanCloud on 15/5/14.
//  Copyright (c) 2015年 leancloud. All rights reserved.
//

#import "MainViewController.h"
#import "TextMessageTableViewCell.h"

#define kConversationId @"55cd829e60b2b52cda834469"
static NSInteger kPageSize = 10;

typedef enum : NSUInteger {
    ConversationTypeOneToOne = 0,
    ConversatinoTypeGroup
}ConversationType;

@interface MainViewController ()<UITableViewDataSource, UITableViewDelegate, AVIMClientDelegate>
@property (nonatomic, strong) NSMutableArray *messages;
@property (nonatomic, strong) UIRefreshControl *refreshControl;
@property (nonatomic,strong) AVIMConversation *defaultConversation;
@end

@implementation MainViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
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
        [conversation queryMessagesWithLimit:10 callback:^(NSArray *objects, NSError *error) {
            [self.messages addObjectsFromArray:objects];
        }];
    }];
    
    /**
     * 为消息列表添加下拉刷新控件，每一次下拉都会增加 10 条聊天记录
     */
    [self.messageTableView addSubview:self.refreshControl];
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
    if (self.messages.count == 0) {
        [refreshControl endRefreshing];
        return;
    } else {
        AVIMTypedMessage *firstMessage = self.messages[0];
        [self.defaultConversation queryMessagesBeforeId:nil timestamp:firstMessage.sendTimestamp limit:kPageSize callback: ^(NSArray *objects, NSError *error) {
            [refreshControl endRefreshing];
            if (error == nil) {
                NSInteger count = objects.count;
                if (count == 0) {
                    NSLog(@"no more old message");
                } else {
                    [self.messages addObjectsFromArray:objects];
                    [self.messageTableView reloadData];
                }
            }
        }];
    }
}
#pragma Load Message History
- (void)createChatRoom{
    NSMutableArray *convMembers=[NSMutableArray arrayWithObjects: @"hjiang", nil];
    AVIMClient *imClient = [AVIMClient defaultClient];
    [imClient createConversationWithName:@"广场" clientIds:convMembers attributes:nil options:AVIMConversationOptionTransient  callback:^(AVIMConversation *conversation, NSError *error) {
        UIAlertView *view = [[UIAlertView alloc] initWithTitle:@"聊天室创建成功。" message:[error description] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [view show];
    }];
}
#pragma Draw Table
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return 1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    /**
     * 绘画 Tabel View Cell 控件
     */
    AVIMMessage *message= self.messages[indexPath.row];
    if ([message isKindOfClass:[AVIMTypedMessage class]]) {
        AVIMTypedMessage *typedMessage=(AVIMTypedMessage*)message;
        switch (typedMessage.mediaType) {
            case  kAVIMMessageMediaTypeText: {
                AVIMTextMessage *textMessage=(AVIMTextMessage*)typedMessage;
                TextMessageTableViewCell  *textCellView = [[TextMessageTableViewCell alloc]init];
                textCellView.messageSenderClientIdLabel.text=textMessage.clientId;
                textCellView.textMessageContentTextView.text=textMessage.text;
                return textCellView;
            }
                break;
            default:
                break;
        }
    }
    
    NSString *identifier = @"cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
    }
    cell.textLabel.text = [NSString stringWithFormat:@"%@ : %@", message.clientId, @"当前版本暂不支持显示此消息类型。"];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}
#pragma mark - actions

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // ChatViewController *chatViewController = (ChatViewController *)segue.destinationViewController;
    // chatViewController.conversation = sender;
}

- (IBAction)sendMessage:(id)sender {
    /**
     *  点击「发送」按钮，将消息发送到当前的聊天室
     */
    AVIMTextMessage *textMessage = [AVIMTextMessage messageWithText:self.messageInputTextField.text attributes:nil];
    [self.defaultConversation sendMessage:textMessage callback:^(BOOL succeeded, NSError *error) {
        [self.messages addObject:textMessage];
        [self.messageTableView reloadData];
    }];
}
@end
