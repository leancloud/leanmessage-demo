//
//  LoginViewController.m
//  LeanMessageDemo
//
//  Created by lzw on 15/5/13.
//  Copyright (c) 2015年 leancloud. All rights reserved.
//

#import "LoginViewController.h"

@interface LoginViewController ()

@property (weak, nonatomic) IBOutlet UITextField *selfIdTextField;

@end

@implementation LoginViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"LeanMessageDemo";
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString *selfId = [userDefaults objectForKey:kLoginSelfIdKey];
    if (selfId) {
        [self openClientWithClientId:selfId completion: ^(BOOL succeeded, NSError *error) {
            if (!error) {
                [self performSegueWithIdentifier:@"toMain" sender:self];
            }
        }];
    }
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)onLoginButtonClicked:(id)sender {
    NSString *selfId = self.selfIdTextField.text;
    if (selfId.length > 0) {
        [self openClientWithClientId : selfId completion : ^(BOOL succeeded, NSError *error) {
            if (!error) {
                [[NSUserDefaults standardUserDefaults] setObject:selfId forKey:kLoginSelfIdKey];
                [[NSUserDefaults standardUserDefaults] synchronize];
                [self performSegueWithIdentifier:@"toMain" sender:self];
            }
        }];
    }
}

- (void)openClientWithClientId:(NSString *)clientId completion:(AVBooleanResultBlock)completion {
    AVIMClient *imClient = [AVIMClient defaultClient];
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
