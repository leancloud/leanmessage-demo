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
#import "AppDelegate.h"

#define kConversationId @"551a2847e4b04d688d73dc54"

@interface MainViewController ()

@property (weak, nonatomic) IBOutlet UITextField *otherIdTextField;
@property (weak, nonatomic) IBOutlet UILabel *welcomeLabel;

@property (nonatomic, strong) AVIMClient *imClient;

@end

@implementation MainViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"选择操作";
    self.imClient = ((AppDelegate *)[UIApplication sharedApplication].delegate).imClient;;
    
    self.welcomeLabel.text = [NSString stringWithFormat:@"%@  %@", self.welcomeLabel.text, self.imClient.clientId];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

#pragma mark - actions

- (IBAction)onChatButtonClicked:(id)sender {
    // 获取用户输入的被邀请加入对话的 client id
    NSString *otherId = self.otherIdTextField.text;
    
    // 判断用户是否输入的是一个有效的字符串
    if (otherId.length > 0) {
        AVIMConversationResultBlock completion = ^(AVIMConversation *conversation, NSError *error) {
            if (error) {
                NSLog(@"%@", error);
            } else {
                [self performSegueWithIdentifier:@"toChat" sender:conversation];
            }
        };
        
        // 新建一个 AVIMConversationQuery 实例
        AVIMConversationQuery *query = [self.imClient conversationQuery];
        // 构建一个数组，数组包含了当前 client 的 id 以及被邀请加入对话的 client id
        NSMutableArray *queryClientIDs = [[NSMutableArray alloc] initWithArray:@[otherId,self.imClient.clientId]];
        // 构建查询条件：AVIMConversation 中的成员数量为 2
        [query whereKey:kAVIMKeyMember sizeEqualTo:queryClientIDs.count];
        // 构建查询条件：AVIMConversation 中成员同时包含当前 client 的 id 以及被邀请加入对话的 client id
        [query whereKey:kAVIMKeyMember containsAllObjectsInArray:queryClientIDs];
        [query findConversationsWithCallback: ^(NSArray *objects, NSError *error) {
            if (error) {
                // 出错了，请稍候重试
                completion(nil, error);
            }
            // 如果未查询到符合条件的对话
            else if (!objects || [objects count] < 1) {
                // 新建一个对话
                [self.imClient createConversationWithName:nil clientIds:queryClientIDs callback:completion];
            } else {
                // 已经有一个对话存在，继续在这一对话中聊天
                AVIMConversation *conversation = [objects lastObject];
                completion(conversation, nil);
            }
        }];
    }
}

- (IBAction)onStartGroupConversationButtonClicked:(id)sender {
    AVIMConversationQuery *query = [self.imClient conversationQuery];
    [query whereKey:@"objectId" equalTo:kConversationId];
    [query findConversationsWithCallback: ^(NSArray *conversations, NSError *error) {
        if (error) {
            NSLog(@"error = %@",error);
        } else {
            if (conversations.count == 0) {
                NSLog(@"聊天室不存在");
            } else {
                AVIMConversation *conversation = conversations[0];
                [conversation joinWithCallback:^(BOOL succeeded, NSError *error) {
                    if (error) {
                        NSLog(@"error : %@",error);
                    } else {
                        [self performSegueWithIdentifier:@"toChat" sender:conversation];
                    }
                }];
            }
        }
    }];
}

- (IBAction)onLogoutButtonClicked:(id)sender {
    [self.imClient closeWithCallback: ^(BOOL succeeded, NSError *error) {
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
