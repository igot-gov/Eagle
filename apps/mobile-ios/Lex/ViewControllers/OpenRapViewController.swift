//  OpenRapViewController.swift
//  Lex
//  Created by Shubham Singh on 3/12/18.
//  Copyright © 2019 Infosys. All rights reserved.

import UIKit
import WebKit
import SwiftyJSON
import UserNotifications
import CoreData
import CryptoSwift
import SSZipArchive

class OpenRapViewController: UIViewController, WKNavigationDelegate, WKScriptMessageHandler {
    
    @IBOutlet weak var hotspotWebView: WKWebView!
    @IBOutlet weak var navigationBar: UINavigationBar!
    @IBOutlet weak var homeButton: UIButton!
    
    
    @IBAction func downloadSegueButton(_ sender: Any) {
        WiFiUtil.openRapSuccess() { isConnected in
            if isConnected == true {
                WiFiUtil.result = true
            }
        }
        navigationController?.popViewController(animated: true)
        dismiss(animated: true, completion: nil)
    }
    
    
    @IBAction func homeButtonPressed(_ sender: Any) {
        WiFiUtil.openRapSuccess() { isConnected in
            if isConnected == true {
                WiFiUtil.result = true
            }
        }
        navigationController?.popViewController(animated: true)
        dismiss(animated: true, completion: nil)
    }
    
    
    
    // Observer to read the notification
    @objc func receiveNotifi(obj: NSNotification) {
        let type:String = obj.userInfo!["type"] as! String
        
        if type.lowercased()=="resourcedownloaded" {
            showToast(message: AppConstants.contentDownloadedGotoDownloads, force: true)
        }
        
        if type.lowercased().hasPrefix("showtoast") {
            let typeToast = type.split(separator: "/")
            let message = String(describing: typeToast[1])
            
            var force = false
            if typeToast.count>2 {
                force = true
            }
            showToast(message: message, force: force)
        }
    }
    var path: String? = nil
    
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        do {
            if message.name == "appRef" {
                let messageData = String(describing: message.body).data(using: .utf8)
                let messageJSON =  try JSONSerialization.jsonObject(with: messageData!, options: [])
                let jsonData = JSON(messageJSON)
                var shouldDownload = true
                //getting lex id
                let lex_ID = jsonData.dictionary!["data"]?.stringValue
                
                
                //checking if the artifact already exists
                let results = CoreDataService.getCoreDataRow(identifier: lex_ID!, uuid: UserDetails.UID)
                let data = results as! [NSManagedObject]
                
                if data.count != 0 {
                    showToast(message: (data[0].value(forKey: "content_type") as? String)! + " already exists in your downloads", force: true)
                } else {
                    let rows = CoreDataService.getAllRows(entity : "DownloadPersistance") as! [NSManagedObject]
                    for row in rows{
                        print("download persistance :", (row.value(forKey: "taskId") as! Int32))
                        let downloadingContentId = row.value(forKey: "contentId") as! String
                        if(downloadingContentId == lex_ID){
                            showToast(message: AppConstants.alreadyDownloading, force: true)
                            shouldDownload = false
                        }
                    }
                    if shouldDownload {
                        WiFiUtil.openRapSuccess() { isConnected in
                            
                            if isConnected == true {
                                self.showToast(message: "Download Initiated", force: true)
                                Telemetry().AddDownloadTelemetry(rid: lex_ID!, mimeType: "", contentType: "", status: "initiated", mode: AppConstants.downloadType.OPEN_RAP.name())
                                DownloadService.downloadArtifact(withId: lex_ID!, downloadtype: AppConstants.downloadType.OPEN_RAP.name())
                            } else {
                                let message = "It seems that iGoT 2.0 Hotspot is not available. Would you like to go downloads instead?"
                                
                                let alertController = UIAlertController(title: "Network Disconnected", message: message, preferredStyle: .alert)
                                alertController.view.layoutIfNeeded() //avoid Snapshotting error
                                
                                let yesAction = UIAlertAction(title: "Yes", style: .default) { (action) -> Void in
                                    self.navigationController?.popViewController(animated: true)
                                    self.dismiss(animated: true, completion: nil)
                                }
                                alertController.addAction(yesAction)
                                alertController.preferredAction = yesAction
                                self.present(alertController, animated: true, completion: nil)
                            }
                        }
                    }
                }
            }
        } catch {
            print(error)
            print("Exception while getting the data from the openrap url")
        }
    }
    
    func webView(_ webView: WKWebView, decidePolicyFor navigationResponse: WKNavigationResponse, decisionHandler: @escaping (WKNavigationResponsePolicy) -> Void) {
        if(navigationResponse.response .isKind(of: HTTPURLResponse.self)) {
            let response = navigationResponse.response as! HTTPURLResponse
            
            let errorCodeString = String(describing: response.statusCode)
            if(errorCodeString.starts(with: "4") || errorCodeString.starts(with: "5")) {
                showErrorAlert()
            }
        }
        decisionHandler(WKNavigationResponsePolicy.allow)
    }
    
    //error alert
    
    func showErrorAlert() {
        let alertController = UIAlertController(title: "Error", message: AppConstants.openRapLoadingErrorMessage, preferredStyle: .alert)
        
        // Initialize Actions
        let goBackAction = UIAlertAction(title: "Back", style: .default) { (action) -> Void in
            self.navigationController?.popViewController(animated: true)
        }
        
        
        // Add Actions
        alertController.addAction(goBackAction)
        alertController.preferredAction = goBackAction
        
        self.present(alertController, animated: true, completion: nil)
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        hotspotWebView.frame = CGRect(x: 0,y:self.navigationBar.frame.origin.y + self.navigationBar.frame.height, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height - (self.navigationBar.frame.origin.y + self.navigationBar.frame.height))
        // Loading the openrap URL
        if(!AppConstants.openRapLaunchPageUrl.isEmpty){
            let gotoURL = AppConstants.openRapLaunchPageUrl
            print(gotoURL)
            let myURL = URL(string: gotoURL)
            let myRequest = URLRequest(url: myURL!)
            
            
            if !Thread.isMainThread {
                DispatchQueue.main.sync {
                    hotspotWebView.navigationDelegate = self
                    hotspotWebView.configuration.preferences.javaScriptEnabled = true
                    hotspotWebView.configuration.preferences.javaScriptCanOpenWindowsAutomatically = true
                    hotspotWebView.configuration.preferences.setValue(true, forKey: "allowFileAccessFromFileURLs")
                    hotspotWebView.configuration.userContentController.add(self, name: "appRef")
                    hotspotWebView.load(myRequest)
                    print("My request: ", myRequest)
                    hotspotWebView.scrollView.addSubview(self.refreshControl)
                    hotspotWebView.scrollView.minimumZoomScale = 1.0
                    hotspotWebView.scrollView.maximumZoomScale = 5.0
                }
            } else {
                hotspotWebView.navigationDelegate = self
                hotspotWebView.configuration.preferences.javaScriptEnabled = true
                hotspotWebView.configuration.preferences.javaScriptCanOpenWindowsAutomatically = true
                hotspotWebView.configuration.preferences.setValue(true, forKey: "allowFileAccessFromFileURLs")
                hotspotWebView.configuration.userContentController.add(self, name: "appRef")
                hotspotWebView.load(myRequest)
                hotspotWebView.scrollView.addSubview(self.refreshControl)
                hotspotWebView.scrollView.minimumZoomScale = 1.0
                hotspotWebView.scrollView.maximumZoomScale = 5.0
            }
            
            WebViewService.addWebViewToProcessPool(webView: hotspotWebView)
            Telemetry().AddImpressionTelemetry(envType: "Openrap", type: "Openrap", pageID: "lex.openrap", id: "", url: "")
        
        }
        
    }
    func reload() {
        if path != nil {
            var params = path!.components(separatedBy: "?")
            if params.count > 1 {
                let paramsItems = params[1].components(separatedBy: "&")
                
                var paramsObject: [String:String] = [String:String]()
                for paramsItem in paramsItems {
                    let items = paramsItem.components(separatedBy: "=")
                    paramsObject[items[0]]=items[1]
                }
                self.hotspotWebView.evaluateJavaScript("navigateTo('/\(params[0])',\(JSON(paramsObject)))")
            } else {
                self.hotspotWebView.evaluateJavaScript("navigateTo('/\(path!)')")
            }
        }
        return
    }
    
    lazy var refreshControl: UIRefreshControl = {
        let refreshControl = UIRefreshControl()
        //refreshControl.attributedTitle = NSAttributedString(string: "Pull to refresh")
        refreshControl.addTarget(self, action:
            #selector(self.handleRefresh(_:)),
                                 for: UIControl.Event.valueChanged)
        refreshControl.tintColor = UIColor.blue
        
        return refreshControl
    }()
    @objc func handleRefresh(_ refreshControl: UIRefreshControl) {
        path = hotspotWebView.url?.path
        
        if path != nil {
            let currentUrl = Singleton.appConfigConstants.appUrl + path!
            var paramPrefix = "?"
            if currentUrl.contains(paramPrefix) {
                paramPrefix = "&"
            }
            //POSSIBLE STATEMENT
            //print(AppDelegate.appDelegate.getWiFiSsid())
            guard let reloadUrl = URL(string: Singleton.appConfigConstants.appUrl + path! + "\(paramPrefix)__ios__ts=\(Date().timeIntervalSinceNow)") else {
                return
            }
            URLCache.shared.removeAllCachedResponses()
            hotspotWebView.evaluateJavaScript("window.location.reload(true)", completionHandler: nil)
            _ = URLRequest(url: reloadUrl)
            //            webView.load(reloadRequest)
        }
        refreshControl.endRefreshing()
        //        self.reload()
    }
    
}
