//
//  ChatRoomViewController.m
//  LeanMessageDemo
//
//  Created by WuJun on 8/20/15.
//  Copyright (c) 2015 LeanCloud. All rights reserved.
//

#import "ChatRoomViewController.h"

#define kConversationId @"55cd829e60b2b52cda834469"

@interface ChatRoomViewController ()
@property (nonatomic,strong) NSString *selectedClientId;
@end

@implementation ChatRoomViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // 新建一个针对 AVIMConversation 的查询，根据 Id 查询出对应的 AVIMConversation 实例
    AVIMConversationQuery *query = [self.imClient conversationQuery];
    [query getConversationById:kConversationId callback:^(AVIMConversation *conversation, NSError *error) {
        // 将当前所在的对话实例设置为查询出来的 conversation
        self.currentConversation=conversation;
        // 注意：如果不主动调用 joinWithCallback 方法，则不会收到聊天室的消息通知
        [self.currentConversation joinWithCallback:^(BOOL succeeded, NSError *error) {
            NSLog(@"成功加入聊天室，开始接收消息。");
        }];
        // 初始化消息发送面板（消息输入框，发送按钮）
        [self initMessageToolBar];
        // 查询最近的 10 条聊天记录
        [conversation queryMessagesWithLimit:kPageSize callback:^(NSArray *objects, NSError *error) {
            // 刷新 Tabel 控件，为其添加数据源
            [self.messages addObjectsFromArray:objects];
            [self.messageTableView reloadData];
        }];
    }];

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    UITableViewCell *cell=  [super tableView:tableView cellForRowAtIndexPath:indexPath];
     if ([cell isKindOfClass:[LeftTextMessageTableViewCell class]]) {
         LeftTextMessageTableViewCell *leftTextMessageCell=(LeftTextMessageTableViewCell*)cell;
         leftTextMessageCell.messageSenderClientId.userInteractionEnabled = YES;
         UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(clientIdLabelTapped:)];
         [leftTextMessageCell.messageSenderClientId addGestureRecognizer:tapGesture];
         }
    return cell;
}
-(void)clientIdLabelTapped:(UIGestureRecognizer *)sender{
    // 获取被点击的 ClientId
    UILabel* tappedClientLabel=(UILabel*)sender.view;
    self.selectedClientId = tappedClientLabel.text;
    
    // 获取 AVIMConversationQuery 实例
    AVIMConversationQuery *query = [[AVIMClient defaultClient] conversationQuery];
    
    /* 构建查询条件
     * 注意：比较建议开发者仔细阅读以下三行代码，这三个条件同时进行查询在数据量日益增加的时候，也能保持查询的性能不受太大影响。
     */
    [query whereKey:@"m" containsAllObjectsInArray:@[[AVIMClient defaultClient].clientId, self.selectedClientId]];
    [query whereKey:@"m" sizeEqualTo:2];
    [query whereKey:AVIMAttr(@"customConversationType")  equalTo:@(1)];
    query.cachePolicy = kAVCachePolicyNetworkOnly;
    [AVOSCloud setAllLogsEnabled:YES];
    
    // 执行查询
    [query findConversationsWithCallback:^(NSArray *objects, NSError *error) {
        if(objects.count == 0){
            NSDictionary *attributes = @{ @"customConversationType": @(1) };
            [[AVIMClient defaultClient] createConversationWithName:@"" clientIds:@[self.selectedClientId] attributes:attributes options:AVIMConversationOptionNone callback:^(AVIMConversation *conversation, NSError *error) {
                 [self performSegueWithIdentifier:@"toSingleChat" sender:conversation];
            }];
        }
        else{
            [self performSegueWithIdentifier:@"toSingleChat" sender:[objects objectAtIndex:0]];
        }
    }];
}


#pragma mark - Navigation

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // 跳转到私聊的界面，设置必要的属性
    if ([[segue identifier] isEqualToString:@"toSingleChat"]) {
        AVIMConversation *targetConversation = (AVIMConversation*)sender;
        SingleChatViewController *singleVC = [segue destinationViewController];
        // 显示当前私聊对象的 ClientId
        [singleVC setTargetClientId:self.selectedClientId];
        // 设置私聊所在的具体对话
        [singleVC setCurrentConversation:targetConversation];
    }


}


@end
