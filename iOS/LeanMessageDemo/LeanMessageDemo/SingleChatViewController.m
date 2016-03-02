//
//  SingleChatViewController.m
//  LeanMessageDemo
//
//  Created by WuJun on 8/24/15.
//  Copyright (c) 2015 LeanCloud. All rights reserved.
//

#import "SingleChatViewController.h"

@interface SingleChatViewController ()

@end

@implementation SingleChatViewController
-(void)setTargetClientId:(NSString *)targetClientId{
    _targetClientId = targetClientId;
}
- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title=[NSString stringWithFormat:@"与 %@ 的对话", self.targetClientId];
   
    [self initMessageToolBar];
    // 查询最近的 5 条聊天记录
    [self.currentConversation queryMessagesWithLimit:kPageSize callback:^(NSArray *objects, NSError *error) {
        // 刷新 Tabel 控件，为其添加数据源
        [self.messages addObjectsFromArray:objects];
        [self.messageTableView reloadData];
    }];
    self.imClient.delegate = self;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    UITableViewCell *cell=  [super tableView:tableView cellForRowAtIndexPath:indexPath];
    return cell;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
