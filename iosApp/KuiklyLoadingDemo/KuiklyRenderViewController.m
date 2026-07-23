#import "KuiklyRenderViewController.h"
#import <OpenKuiklyIOSRender/KuiklyRenderContextProtocol.h>
#import <OpenKuiklyIOSRender/KuiklyRenderViewControllerBaseDelegator.h>

@interface KuiklyRenderViewController () <
    KuiklyRenderViewControllerBaseDelegatorDelegate
>

@property(nonatomic, strong) KuiklyRenderViewControllerBaseDelegator *delegator;

@end

@implementation KuiklyRenderViewController

- (instancetype)initWithPageName:(NSString *)pageName
                        pageData:(NSDictionary *)pageData {
    self = [super init];
    if (self) {
        _delegator = [[KuiklyRenderViewControllerBaseDelegator alloc]
            initWithPageName:pageName
                   pageData:(pageData ?: @{})];
        _delegator.delegate = self;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = UIColor.whiteColor;
    [self.delegator viewDidLoadWithView:self.view];
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    [self.delegator viewDidLayoutSubviews];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.delegator viewWillAppear];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    [self.delegator viewDidAppear];
}

- (void)viewWillDisappear:(BOOL)animated {
    [self.delegator viewWillDisappear];
    [super viewWillDisappear:animated];
}

- (void)viewDidDisappear:(BOOL)animated {
    [self.delegator viewDidDisappear];
    [super viewDidDisappear:animated];
}

- (UIView *)createLoadingView {
    UIView *view = [[UIView alloc] init];
    view.backgroundColor = UIColor.whiteColor;
    return view;
}

- (UIView *)createErrorView {
    UIView *view = [[UIView alloc] init];
    view.backgroundColor = UIColor.whiteColor;
    return view;
}

- (void)fetchContextCodeWithPageName:(NSString *)pageName
                      resultCallback:(KuiklyContextCodeCallback)callback {
    if (callback) {
        callback(@"KuiklyLoadingDemo", nil);
    }
}

@end
