//
//  LoginViewController.m
//  LeanMessageDemo
//
//  Created by lzw on 15/5/13.
//  Copyright (c) 2015年 leancloud. All rights reserved.
//

#import "LoginViewController.h"
#import "AppDelegate.h"

@interface LoginViewController ()

@property (weak, nonatomic) IBOutlet UITextField *selfIdTextField;

@end

@implementation LoginViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"LeanMessageDemo";
    //	self.selfIdTextField.text = @"a";
    
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString *selfId = [userDefaults objectForKey:kLoginSelfIdKey];
    if (selfId) {
        [self openClientWithClientId:selfId completion:^(BOOL succeeded, NSError *error) {
            if (!error) {
                [self performSegueWithIdentifier:@"toMain" sender:self];
            }
        }];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)onLoginButtonClicked:(id)sender {
    NSString *selfId = self.selfIdTextField.text;
    if (selfId.length > 0) {
        WEAKSELF
        [self openClientWithClientId:selfId completion:^(BOOL succeeded, NSError *error) {
            if (!error) {
                [[NSUserDefaults standardUserDefaults] setObject:selfId forKey:kLoginSelfIdKey];
                [[NSUserDefaults standardUserDefaults] synchronize];
                [weakSelf performSegueWithIdentifier:@"toMain" sender:self];
            }
        }];
    }
}

- (void)openClientWithClientId:(NSString *)clientId completion:(AVBooleanResultBlock)completion {
    AVIMClient *imClient = [[AVIMClient alloc] init];
    ((AppDelegate *)[UIApplication sharedApplication].delegate).imClient = imClient;
    if (imClient.status == AVIMClientStatusNone) {
        [imClient openWithClientId:clientId callback:completion];
    }
    else {
        [imClient closeWithCallback: ^(BOOL succeeded, NSError *error) {
            [imClient openWithClientId:clientId callback:completion];
        }];
    }
}

@end
