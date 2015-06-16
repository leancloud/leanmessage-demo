//
//  BaseViewController.m
//  LeanMessageDemo
//
//  Created by lzw on 15/6/16.
//  Copyright (c) 2015年 LeanCloud. All rights reserved.
//

#import "BaseViewController.h"

@interface BaseViewController ()

@end

@implementation BaseViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - util

- (BOOL)filterError:(NSError *)error {
    if (error) {
        UIAlertView *alertView = [[UIAlertView alloc]
                                  initWithTitle:nil message:error.description delegate:nil
                                  cancelButtonTitle   :@"确定" otherButtonTitles:nil];
        [alertView show];
        return NO;
    }
    return YES;
}


@end
