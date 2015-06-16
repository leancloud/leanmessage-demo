//
//  MainViewController.m
//  LeanMessageDemo
//
//  Created by lzw on 15/5/14.
//  Copyright (c) 2015年 leancloud. All rights reserved.
//

#import "MainViewController.h"
#import "ChatViewController.h"
#import "LoginViewController.h"

#define kConversationId @"551a2847e4b04d688d73dc54"

@interface MainViewController ()

@property (weak, nonatomic) IBOutlet UITextField *otherIdTextField;
@property (weak, nonatomic) IBOutlet UILabel *welcomeLabel;

@end

@implementation MainViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"选择操作";
    
    self.welcomeLabel.text = [NSString stringWithFormat:@"%@  %@", self.welcomeLabel.text, [AVIMClient defaultClient].clientId];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

#pragma mark - actions

- (IBAction)onChatButtonClicked:(id)sender {
    // 获取用户输入的被邀请加入对话的 client id
    NSString *otherId = self.otherIdTextField.text;
    
    // 判断用户是否输入的是一个有效的字符串，如果有效，则进入程序逻辑运行
    if (otherId.length > 0) {
        // 新建一个 AVIMConversationQuery 实例
        AVIMConversationQuery *query = [[AVIMClient defaultClient] conversationQuery];
        // 构建一个数组，数组包含了当前 client 的 id 以及被邀请加入对话的 client id
        NSMutableArray *queryClientIDs = [[NSMutableArray alloc] initWithArray:@[otherId, [AVIMClient defaultClient].clientId]];
        // 构建查询条件：AVIMConversation 中的成员数量为 2
        [query whereKey:kAVIMKeyMember sizeEqualTo:queryClientIDs.count];
        // 构建查询条件：AVIMConversation 中成员同时包含当前 clientId 以及被邀请加入对话的 clientId
        [query whereKey:kAVIMKeyMember containsAllObjectsInArray:queryClientIDs];
        [query findConversationsWithCallback: ^(NSArray *objects, NSError *error) {
            if (error) {
                // 出错了，请稍候重试
                NSLog(@"%@", error);
            }
            // 如果未查询到符合条件的对话，则说明这两个 Client 在之前并没有在任何一个 Conversation 里
            else if (!objects || [objects count] < 1) {
                // 新建一个对话
                // createConversationWithName 方法是实现创建对话的操作
                // 参数详解：
                // name-> 对话的名称，这个有开发者自己定义，是一个当前对话内全局有效的字符串
                // clientIds-> 对话参与的人员，默认包含了当前 Client Id
                // callback -> 创建结果的回调函数
                NSMutableArray *queryClientIDsTest = [[NSMutableArray alloc] initWithArray:@[otherId]];
                [[AVIMClient defaultClient] createConversationWithName:nil clientIds:queryClientIDsTest callback:^(AVIMConversation *conversation, NSError *error) {
                    if ([self filterError:error]) {
                        // 创建一个新的对话成功之后，跳转到 ChatView 页面进行聊天
                        [self performSegueWithIdentifier:@"toChat" sender:conversation];
                    }
                }];
            } else {
                // 已经有一个对话存在，获取这个对话
                AVIMConversation *conversation = [objects lastObject];
                // 跳转到 ChatView 页面进行聊天
                [self performSegueWithIdentifier:@"toChat" sender:conversation];
            }
        }];
    }
}

- (IBAction)onStartGroupConversationButtonClicked:(id)sender {
    AVIMConversationQuery *query = [[AVIMClient defaultClient] conversationQuery];
    [query whereKey:@"objectId" equalTo:kConversationId];
    [query findConversationsWithCallback: ^(NSArray *conversations, NSError *error) {
        if ([self filterError:error]) {
            if (conversations.count == 0) {
                NSLog(@"聊天室不存在");
            } else {
                AVIMConversation *conversation = conversations[0];
                if ([conversation.members containsObject:[AVIMClient defaultClient].clientId]) {
                    //已经在对话里了，直接开始聊天
                    [self performSegueWithIdentifier:@"toChat" sender:conversation];
                } else {
                    // 加入对话
                    [conversation joinWithCallback:^(BOOL succeeded, NSError *error) {
                        if ([self filterError:error]) {
                            [self performSegueWithIdentifier:@"toChat" sender:conversation];
                        }
                    }];
                }
            }
        }
    }];
}

- (IBAction)onLogoutButtonClicked:(id)sender {
    [[AVIMClient defaultClient] closeWithCallback: ^(BOOL succeeded, NSError *error) {
        [[NSUserDefaults standardUserDefaults] setObject:nil forKey:kLoginSelfIdKey];
        [[NSUserDefaults standardUserDefaults] synchronize];
        [self.navigationController popViewControllerAnimated:YES];
    }];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    ChatViewController *chatViewController = (ChatViewController *)segue.destinationViewController;
    chatViewController.conversation = sender;
}

@end
