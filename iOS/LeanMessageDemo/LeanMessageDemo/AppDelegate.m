//
//  AppDelegate.m
//  SimpleChat
//
//  Created by lzw on 15/5/13.
//  Copyright (c) 2015年 leancloud. All rights reserved.
//

#import "AppDelegate.h"
#import "LeanMessageManager.h"

@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
	[LeanMessageManager setupApplication];
#ifdef DEBUG
	[AVAnalytics setAnalyticsEnabled:NO];
	[AVOSCloud setVerbosePolicy:kAVVerboseShow];
	[AVLogger addLoggerDomain:AVLoggerDomainIM];
	[AVLogger addLoggerDomain:AVLoggerDomainCURL];
	[AVLogger setLoggerLevelMask:AVLoggerLevelAll];
#endif
	return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application {
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
}

- (void)applicationWillTerminate:(UIApplication *)application {
}

@end
