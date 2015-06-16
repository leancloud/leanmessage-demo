//
//  ChatViewController.m
//  LeanMessageDemo
//
//  Created by lzw on 15/5/13.
//  Copyright (c) 2015年 leancloud. All rights reserved.
//

#import "ChatViewController.h"
#import "AppDelegate.h"
#define RGB(R, G, B) [UIColor colorWithRed : (R) / 255.0f green : (G) / 255.0f blue : (B) / 255.0f alpha : 1.0f]
#define COMMON_BLUE RGB(102, 187, 255)

static NSInteger kPageSize = 15;

@interface ChatViewController () <UITableViewDataSource, UITableViewDelegate, AVIMClientDelegate>

@property (weak, nonatomic) IBOutlet UITableView *messageTableView;
@property (weak, nonatomic) IBOutlet UITextField *inputTextField;
@property (nonatomic, strong) NSMutableArray *messages;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *keyboardHeight;
@property (nonatomic, strong) UIRefreshControl *refreshControl;

@property (nonatomic, strong) AVIMClient *imClient;

@end

@implementation ChatViewController

#pragma mark - life cycle

- (void)viewDidLoad {
    [super viewDidLoad];
    _messages = [NSMutableArray array];
    
    self.title = @"Chat";
    
    [AVIMClient defaultClient].delegate = self;
    
    [self initTableView];
    
    [self loadMessagesWhenInit];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillChangeFrameNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)dealloc {
}

#pragma mark - tableView init

- (void)initTableView {
    [self.messageTableView setBackgroundColor:COMMON_BLUE];
    [self.messageTableView addSubview:self.refreshControl];
    self.messageTableView.dataSource = self;
    self.messageTableView.delegate = self;
}

- (UIRefreshControl *)refreshControl {
    if (_refreshControl == nil) {
        _refreshControl = [[UIRefreshControl alloc] init];
        [_refreshControl addTarget:self action:@selector(loadOldMessages:) forControlEvents:UIControlEventValueChanged];
        [_refreshControl setTintColor:[UIColor whiteColor]];
    }
    return _refreshControl;
}

#pragma mark - tableView Delegate

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.messages.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString *identifier = @"cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
        cell.backgroundColor = [UIColor clearColor];
    }
    AVIMTypedMessage *message = self.messages[indexPath.row];
    NSString *text;
    UIColor *fontColor;
    if (message.mediaType == kAVIMMessageMediaTypeText) {
        AVIMTextMessage *textMessage = (AVIMTextMessage *)message;
        text = textMessage.text;
    } else {
        text = @"其它格式的消息";
    }
    if ([message.clientId isEqualToString:self.imClient.clientId]) {
        fontColor = [UIColor whiteColor];
    } else {
        fontColor = [UIColor yellowColor];
    }
    cell.textLabel.textColor = fontColor;
    cell.textLabel.text = [NSString stringWithFormat:@"%@ : %@", message.clientId, text];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

#pragma mark - action

- (IBAction)onSendButtonClicked:(id)sender {
    NSString *text = self.inputTextField.text;
    if (text.length > 0) {
        [self sendText:text];
    }
}

#pragma mark - keyboard

- (void)keyboardWillShow:(NSNotification *)notification {
    NSDictionary *info = notification.userInfo;
    NSValue *kbFrame = [info objectForKey:UIKeyboardFrameEndUserInfoKey];
    NSTimeInterval animationDuration = [[info objectForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue];
    CGRect keyboardFrame = [kbFrame CGRectValue];
    CGFloat height = keyboardFrame.size.height;
    self.keyboardHeight.constant = height;
    [UIView animateWithDuration:animationDuration animations: ^{
        [self.view layoutIfNeeded];
    }];
}

- (void)keyboardWillHide:(NSNotification *)notification {
    NSTimeInterval animationDuration = [[notification.userInfo objectForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue];
    self.keyboardHeight.constant = 0;
    [UIView animateWithDuration:animationDuration animations: ^{
        [self.view layoutIfNeeded];
    }];
}

#pragma mark - scroll

- (void)scrollToLast {
    if (self.messages.count > 0) {
        [self.messageTableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:self.messages.count - 1 inSection:0] atScrollPosition:UITableViewScrollPositionBottom animated:NO];
    }
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView {
    if ([self.inputTextField isFirstResponder]) {
        [self.inputTextField resignFirstResponder];
    }
}

#pragma mark - message
// 过滤消息，避免非法的消息导致 Crash
- (NSMutableArray *)filterMessages:(NSArray *)messages {
    NSMutableArray *typedMessages = [NSMutableArray array];
    for (AVIMTypedMessage *message in messages) {
        if ([message isKindOfClass:[AVIMTypedMessage class]]) {
            [typedMessages addObject:message];
        }
    }
    return typedMessages;
}

- (void)loadMessagesWhenInit {
    WEAKSELF
    [self.conversation queryMessagesWithLimit:kPageSize callback:^(NSArray *objects, NSError *error) {
        if ([weakSelf filterError:error]) {
            weakSelf.messages = [weakSelf filterMessages:objects];
            [weakSelf.messageTableView reloadData];
            [weakSelf scrollToLast];
        }
    }];
}

- (void)loadOldMessages:(UIRefreshControl *)refreshControl {
    if (self.messages.count == 0) {
        [refreshControl endRefreshing];
        return;
    } else {
        AVIMTypedMessage *firstMessage = self.messages[0];
        WEAKSELF
        [self.conversation queryMessagesBeforeId:nil timestamp:firstMessage.sendTimestamp limit:kPageSize callback: ^(NSArray *objects, NSError *error) {
            [refreshControl endRefreshing];
            if ([weakSelf filterError:error]) {
                NSMutableArray *typedMessages = [weakSelf filterMessages:objects];
                NSMutableArray *messages = [NSMutableArray arrayWithArray:typedMessages];
                [messages addObjectsFromArray:weakSelf.messages];
                weakSelf.messages = messages;
                NSInteger count = typedMessages.count;
                if (count > 0) {
                    [weakSelf.messageTableView reloadData];
                    if (weakSelf.messages.count > count) {
                        NSIndexPath *indexPath = [NSIndexPath indexPathForRow:count inSection:0];
                        [weakSelf.messageTableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionTop animated:NO];
                    }
                }
            }
        }];
    }
}

- (void)sendText:(NSString *)text {
    AVIMTextMessage *textMessage = [AVIMTextMessage messageWithText:text attributes:nil];
    WEAKSELF
    [self.conversation sendMessage:textMessage callback: ^(BOOL succeeded, NSError *error) {
        if ([weakSelf filterError:error]) {
            [weakSelf addMessage:textMessage];
            weakSelf.inputTextField.text = nil;
        }
    }];
}

- (void)addMessage:(AVIMTypedMessage *)message {
    [self.messages addObject:message];
    [self.messageTableView reloadData];
    [self scrollToLast];
}

#pragma mark - AVIMClientDelegate

- (void)conversation:(AVIMConversation *)conversation didReceiveTypedMessage:(AVIMTypedMessage *)message {
    if ([conversation.conversationId isEqualToString:self.conversation.conversationId]) {
        [self addMessage:message];
    }
}

@end
