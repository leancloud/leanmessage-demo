//
//  MainViewController.m
//  LeanMessageDemo
//
//  Created by lzw on 15/5/14.
//  Copyright (c) 2015年 leancloud. All rights reserved.
//

#import "MainViewController.h"

#define kConversationId @"551a2847e4b04d688d73dc54"

typedef enum : NSUInteger {
    ConversationTypeOneToOne = 0,
    ConversatinoTypeGroup
}ConversationType;

@interface MainViewController ()<UITableViewDataSource, UITableViewDelegate, AVIMClientDelegate>

@end

@implementation MainViewController

- (void)viewDidLoad {
    [super viewDidLoad];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}
#pragma Draw Table
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return 1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}
#pragma mark - actions

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // ChatViewController *chatViewController = (ChatViewController *)segue.destinationViewController;
    // chatViewController.conversation = sender;
}

@end
