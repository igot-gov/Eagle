//  ExternalContentViewController.swift
//  Lex
//  Created by Shubham Singh on 3/12/18.
//  Copyright © 2019 Infosys. All rights reserved.


import Foundation
import WebKit
import UIKit

class ExternalContentViewController: UIViewController, WKScriptMessageHandler, WKNavigationDelegate {
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        print(message)
    }
    let gracePeriodInSeconds = 5.0
    var contentLoadedDate: Double? = nil
    
    var originalUrl: String = ""
    var webUrl: String = ""
    var resourceId: String = ""
    
    var externalOpened: Bool = false
    var firstLoad: Bool = true
    var forceCancelled = false
    
    // We will kill the timer, when the user has exited the screen. Hence making a reference
    var telemetryTimer: Timer!
    
    // This will help to know if the url navigation is allowed or not. Else if only the navigation is checked, if the user is asked for login to some url which is not in our white list, then we will always block it. Hence once the url is loaded, then only we will check for the navigation check that if user is allowed to navigate to other page or not.
    
    var requestedResourceLoaded = false
    
    @IBOutlet weak var forwardButton: UIButton!
    @IBOutlet weak var navigationBar: UINavigationBar!
    @IBOutlet weak var homeIcon: UIButton!
    @IBOutlet var backButtonItem: [UIButton]!
    
    @IBOutlet weak var backButton: UIButton!
    @IBOutlet weak var webPageTitle: UILabel!
    @IBOutlet weak var webPageUrl: UILabel!
    
    
    @IBAction func homeIconTapped(_ sender: Any) {
        exitThisView()
        DispatchQueue.main.async {
            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "extPlayer"), object: self, userInfo: ["path": "/page/home"])
        }
        
    }
    
    //    func enableNavigationButtons() {
    //        if ExternalContentService.externalWebView.canGoForward {
    //            forwardButton.isHidden = false
    //        } else {
    //            forwardButton.isHidden = true
    //        }
    //
    //        if !ExternalContentService.externalWebView.canGoBack {
    //            backButton.isHidden = true
    //        } else {
    //            backButton.isHidden = false
    //        }
    //    }
    
    
    @IBAction func backButtonTapped(_ sender: Any) {
        
        
        if AppConstants.chatBotExternal == false{
            exitThisView()
        }
        //        print("back button")
        print(ExternalContentService.externalWebView.url?.absoluteString as Any )
        print(ExternalContentService.openedUrl)
        if (ExternalContentService.externalWebView.url?.absoluteString == ExternalContentService.openedUrl) {
            print("inside if")
            exitThisView()
        } else {
            ExternalContentService.externalWebView.goBack()
        }
        
    }
    
    @IBAction func forwardButtonTapped(_ sender: Any) {
        UIPasteboard.general.string = webPageUrl.text
        showToast(message: "Link Copied",force: true)
    }
    
    
    
    func exitThisView() {
        if ExternalContentService.externalWebView.isLoading {
            forceCancelled = true
            ExternalContentService.externalWebView.stopLoading()
        }
        if telemetryTimer != nil && telemetryTimer.isValid {
            telemetryTimer.invalidate()
            print("Timer removed for: ", webUrl)
            telemetryTimer = nil
        }
        AppConstants.isExternalview = false
        AppConstants.chatBotExternal = false
        //        ExternalContentService.externalWebView.
        _ = navigationController?.popViewController(animated: true)
    }
    
    override func viewDidLoad() {
        //        backButton.isHidden = true
        //        forwardButton.isHidden = trueUIColor.fromHex(rgbValue: 0xfdffbd, alpha: 0.68)
        webPageTitle.text = ExternalContentService.externalWebView.title
        webPageUrl.text = webUrl
        
        let navigationBarTintColor = AppConstants.primaryTheme.replacingOccurrences(of: "#", with: "")
        navigationBar.barTintColor = UIColor.fromHex(rgbValue: UInt32(String(navigationBarTintColor), radix: 16)!, alpha: 1.0)
        
        webPageTitle.textColor = UIColor.white
        if(UIScreen.main.bounds.width > 350) {
            homeIcon.frame = CGRect(x: 100+(50), y: 200 , width: 44, height: 44)
        }
        print("External webview URL value is:",webUrl)
        
        // Setting the frame for the web view -> Fix for iPhoneX
        DispatchQueue.main.async {
            ExternalContentService.externalWebView.frame = self.getWebViewFrame()
        }
        // Adding the web view to the process pool
        WebViewService.addWebViewToProcessPool(webView: ExternalContentService.externalWebView)
        
        self.view.addSubview(ExternalContentService.externalWebView)
        
        // Loading the web view here
        ExternalContentService.externalWebView.navigationDelegate = self
        print(webUrl)
        if (ExternalContentService.openedUrl != webUrl || AppConstants.chatBotExternal == true) {
            // Loading the view with the new URL
            print("this web url is ",webUrl)
            guard let url = URL(string: webUrl) else { return }
            let req = URLRequest(url: url)
            ExternalContentService.externalWebView.load(req)
            ExternalContentService.openedUrl = webUrl
            let title = ExternalContentService.externalWebView.title
            print(title as Any)
            webPageTitle.text = title
        }
        self.view.translatesAutoresizingMaskIntoConstraints = true
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.deviceRotated), name: UIDevice.orientationDidChangeNotification, object: nil)
        
        
    }
    
    func getWebViewFrame() -> CGRect {
        return CGRect(x: 0, y: self.navigationBar.frame.origin.y + self.navigationBar.frame.height, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height - (self.navigationBar.frame.origin.y + self.navigationBar.frame.height))
    }
    
    func showErrorAlert() {
        let alertController = UIAlertController(title: "Error", message: AppConstants.externalContentLoadError, preferredStyle: .alert)
        
        // Initialize Actions
        let goBackAction = UIAlertAction(title: "Back", style: .default) { (action) -> Void in
            self.exitThisView()
        }
        
        // Reporting an issue that the content did not load
        let reportAction = UIAlertAction(title: "Report", style: .default, handler: { (action) -> Void in
            self.exitThisView()
            DispatchQueue.main.async {
                NotificationCenter.default.post(name: NSNotification.Name(rawValue: "extPlayer"), object: self, userInfo: ["path": "contact-us"])
            }
        })
        
        // Add Actions
        alertController.addAction(reportAction)
        alertController.addAction(goBackAction)
        alertController.preferredAction = reportAction
        
        self.present(alertController, animated: true, completion: nil)
    }
    
    func addTelemetryForExternalViewer() {
        print("Fired the telemetry for external player...")
        Telemetry().AddPlayerTelemetry(json:[:], cid: "", rid: self.resourceId, mimeType: "resource")
    }
    
    // ======== Web view delegates starts ===============
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        showActivityIndicator()
        let title = ExternalContentService.externalWebView.title
        print(title as Any)
        webPageTitle.text = title
        
    }
    func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!) {
        print("Navigation commit success")
        
        if webView.url?.absoluteString == webUrl {
            requestedResourceLoaded = true
            // Saving the load time. Later will use it with grace periods to stop navigating out of this location
            contentLoadedDate = Date().timeIntervalSince1970
            print("Requested resource has successfully loaded...")
        }
    }
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        print("Navigation finished")
        hideActivityIndicator()
        
        if firstLoad == true {
            originalUrl = (ExternalContentService.externalWebView.url?.absoluteString)!
            firstLoad = false
        }
        
        //checking for the navigation of the link
        
        print(originalUrl)
        print(ExternalContentService.externalWebView.url?.absoluteString as Any)
        //        if (AppConstants.chatBotExternal && originalUrl != ExternalContentService.externalWebView.url?.absoluteString) {
        //            enableNavigationButtons()
        //        }
        //        else {
        //            backButton.isHidden = true
        //            if ExternalContentService.externalWebView.canGoForward {
        //                forwardButton.isHidden = false
        //            }
        //            else {
        //                forwardButton.isHidden = true
        //            }
        //        }
        
        // The if the url loaded is same as url requested. Start telemetry and say that the url is loaded. Navigation check is performed with respect to this
        if requestedResourceLoaded && telemetryTimer == nil && AppConstants.chatBotExternal == false {
            // Adding the telemetry events
            addTelemetryForExternalViewer()
            telemetryTimer = Timer.scheduledTimer(withTimeInterval: 30, repeats: true, block: { (Void)
                in
                self.addTelemetryForExternalViewer()
            })
        }
        
        
        let title = ExternalContentService.externalWebView.title
        //        print(title)
        webPageTitle.text = title
        webPageUrl.text = webUrl
        
    }
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        hideActivityIndicator()
        //        showErrorAlert()
        print(error)
        ExternalContentService.openedUrl = ""
    }
    func webView(_ webView: WKWebView,
                 didFailProvisionalNavigation navigation: WKNavigation!,
                 withError error: Error) {
        hideActivityIndicator()
        if !forceCancelled {
            showErrorAlert()
        }
        ExternalContentService.openedUrl = ""
    }
    
    // Checking if the user is going away from the resourse URL
    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        
        guard contentLoadedDate != nil else {
            // Allowing all content if the content is not completely loaded
            print("Check this out ->",navigationAction.request.url!.absoluteString)
            if ((navigationAction.request.url?.absoluteString.contains("https://app.pluralsight.com/player"))! && !externalOpened) {
                ExternalContentService.externalWebView.load(URLRequest(url: URL(string: (navigationAction.request.url?.absoluteString)!)!))
                ExternalContentService.openedUrl = webUrl
                externalOpened = true
                decisionHandler(.allow)
                return
                
            }else {
                decisionHandler(.allow)
                return
            }
        }
        
        let timeDifference = Date().timeIntervalSince1970 - contentLoadedDate!
        // Allow all urls when the time difference is less than the grace period. This will not check for any condition of loading different url in the same webview's initial source
        if timeDifference < self.gracePeriodInSeconds {
            decisionHandler(.allow)
            return
        }
        
        if requestedResourceLoaded && navigationAction.navigationType == .linkActivated {
            
            let possibleExternalNav = navigationAction.request.url!.absoluteString
            let links = possibleExternalNav.split(separator: "/")
            print(links)
            if possibleExternalNav == "about:blank" {
                decisionHandler(.allow)
            } else {
                // Right now checking if the link has .html tag in it.
                // If it has .html, allowing until the html element navigation
                // Else blocking the rest
                
                let htmlStringNow = possibleExternalNav.components(separatedBy: ".html")
                
                if htmlStringNow.count>1 {
                    // URL has .html in it
                    let htmlStringInUrl = webUrl.components(separatedBy: ".html")
                    
                    if htmlStringNow[0] == htmlStringInUrl[0] {
                        decisionHandler(.allow)
                    } else {
                        if AppConstants.chatBotExternal {
                            decisionHandler(.allow)
                            backButton.isHidden = false
                        } else {
                            decisionHandler(.allow)
//                            navNotAllowedError()
                        }
                    }
                } else {
                    // URL does not have any .html tags. Check for the query parameters and block the access
                    let queryParamsUrl = possibleExternalNav.components(separatedBy: "?")
                    
                    if queryParamsUrl.count>1 {
                        // URL has query params in it
                        let navInUrl = webUrl.components(separatedBy: "?")
                        
                        if queryParamsUrl[0] == navInUrl[0] {
                            decisionHandler(.allow)
                        } else {
                            if AppConstants.chatBotExternal {
                                decisionHandler(.allow)
                                backButton.isHidden = false
                            } else {
                                decisionHandler(.cancel)
                                navNotAllowedError()
                            }
                        }
                    }
                    else if(links.contains("www.coursera.org") && links.contains("learn")){
                        decisionHandler(.allow)
                        ExternalContentService.externalWebView.load(URLRequest(url: URL(string: possibleExternalNav)!))
                        ExternalContentService.openedUrl = webUrl
                    }
                    else {
                        // URL does not have query params as well. Block everything
                        if AppConstants.chatBotExternal {
                            decisionHandler(.allow)
                            backButton.isHidden = false
                        } else {
                            decisionHandler(.cancel)
                            navNotAllowedError()
                        }
                    }
                }
            }
        } else {
            print("Requested resource is not yet loaded. Hence ignoring the navigation")
            decisionHandler(.allow)
        }
    }
    // ======== Web view delegates ends ===============
    func navNotAllowedError() {
        let alertController = UIAlertController(title: "Restricted", message: AppConstants.externalNavigationRestriction, preferredStyle: .alert)
        
        // Reporting an issue that the content did not load
        let closeAction = UIAlertAction(title: "Close", style: .default, handler: { (action) -> Void in
            //            self.exitThisView()
        })
        
        // Add Actions
        alertController.addAction(closeAction)
        alertController.preferredAction = closeAction
        
        self.present(alertController, animated: true, completion: nil)
    }
    
    // Loader for Webview
    var activityIndicator: UIActivityIndicatorView = UIActivityIndicatorView()
    var container: UIView = UIView()
    var loadingView: UIView = UIView()
    
    func showActivityIndicator() {
        let containerX = 0
        let containerY = self.navigationBar.frame.origin.y + self.navigationBar.frame.height
        
        // This will start after the navigation bar
        container.frame = CGRect(x: CGFloat(containerX), y: containerY, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height-containerY)
        
        container.center.x = self.view.center.x
        container.backgroundColor = UIColor.lightGray.withAlphaComponent(0.5)
        
        loadingView.frame = CGRect(x:0, y: 0, width: 80, height: 80)
        loadingView.center = self.view.center
        loadingView.backgroundColor = UIColor(rgb: 0x444444).withAlphaComponent(0.3)
        loadingView.clipsToBounds = true
        loadingView.layer.cornerRadius = 10
        
        activityIndicator.frame = CGRect(x: 0.0, y: 0.0, width: 40.0, height: 40.0);
        activityIndicator.style = UIActivityIndicatorView.Style.whiteLarge
        activityIndicator.center = CGPoint(x: loadingView.frame.size.width / 2, y: loadingView.frame.size.height / 2);
        loadingView.addSubview(activityIndicator)
        container.addSubview(loadingView)
        
        self.view.addSubview(container)
        activityIndicator.startAnimating()
    }
    
    func hideActivityIndicator() {
        activityIndicator.stopAnimating()
        container.removeFromSuperview()
    }
    
    // Screen auto-rotate logic. Change this later. There must be a simpler way of doing this
    override func willTransition(to newCollection: UITraitCollection, with coordinator: UIViewControllerTransitionCoordinator) {
        DispatchQueue.main.async {
            ExternalContentService.externalWebView.frame = self.getWebViewFrame()
        }
    }
    
    @objc func deviceRotated() {
        
        print("Being called")
        var xPos = UIScreen.main.bounds.width
        var yPos = UIScreen.main.bounds.height
        
        switch UIDevice.current.userInterfaceIdiom {
        case .pad: xPos = UIScreen.main.bounds.height
        yPos = UIScreen.main.bounds.width
            break
            
        case .phone: break
            
        default : break
            
        }
        
        if UIDevice.current.orientation.isLandscape {
            let externalFrame =  CGRect(x: 0, y: self.navigationBar.frame.origin.y + self.navigationBar.frame.height, width: xPos, height: yPos - (self.navigationBar.frame.origin.y + self.navigationBar.frame.height))
            ExternalContentService.externalWebView.frame = externalFrame
        }
            
        else if UIDevice.current.orientation.isPortrait {
            let externalFrame =  CGRect(x: 0, y: self.navigationBar.frame.origin.y + self.navigationBar.frame.height, width: xPos, height: yPos - (self.navigationBar.frame.origin.y + self.navigationBar.frame.height))
            ExternalContentService.externalWebView.frame = externalFrame
        }
    }
    
}
