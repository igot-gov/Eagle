//  HomeViewController.swift
//  Lex
//  Created by prakash.chakraborty/ Shubham Singh on 3/12/18.
//  Copyright © 2019 Infosys. All rights reserved.

import UIKit
import WebKit
import SwiftyJSON
import UserNotifications
import Reachability
import CoreData
import Alamofire

// base URL which will be used for home webView
let baseURL : String = Singleton.appConfigConstants.appUrl + "/"

// HomeViewController class
class HomeViewController: UIViewController, WKScriptMessageHandler, WKNavigationDelegate, UNUserNotificationCenterDelegate {
    
    // outlet for webView
    @IBOutlet weak var webView: WKWebView!
    @IBAction func unwindToHome(segue:UIStoryboardSegue) { Singleton.tempCounter = "" }
    @IBAction func unwindToHome2(segue:UIStoryboardSegue) { Singleton.tempCounter = "" }
    
    // variables
    static var firstDownload = true
    var path : String? = nil
    var container: UIView = UIView()
    var loadingView: UIView = UIView()
    var activityIndicator: UIActivityIndicatorView = UIActivityIndicatorView()
    var externalOpened = false
    var downloadAllowed: Bool = true
    
    // For last playing resource ID
    var lastPlayingResourceId = ""
    var didHomeLoad = false
    var minimumVersion : [Int:Int] = [5:1]
    
    // static label for environment other than PROD
    let appVersionLabel = UILabel()
    let appVersionLabelHeight = 30
    let appVersionLabelPadding = 10
    let documentsDirectoryURL =  FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
    let fileUrlForQuizResponse = Singleton.fileManager.urls(for: .documentDirectory, in: .userDomainMask).first?.appendingPathComponent("Data_QuizResponse.json")
    
    // For connection test of WiFi or Mobile Data
    let reachability = Reachability()!
    
    @objc func reachabilityChanged(note: NSNotification) {
        let reachability = note.object as! Reachability
        switch reachability.connection {
        case .wifi:
            NetworkUtil.setNetworkType(type: .Wifi)
            TestUtil.currentNetworkType = "Wifi"
            SnackBarUtil.removeSnackBarLabel()

            WiFiUtil.openRapSuccess() { isConnected in
                if isConnected == true {
                    self.goToDownloads()
                }
            }
            
        case .cellular:
            SnackBarUtil.removeSnackBarLabel()
            TestUtil.currentNetworkType = "Cellular"
            NetworkUtil.setNetworkType(type: .Cellular)
            
        case .none:
            SnackBarUtil.addSnackBarLabel(webview: webView,message: AppConstants.noConnection)
            Singleton.snackBarShown = true
            print("No network connection in Home View...")
            TestUtil.currentNetworkType = "None"
            NetworkUtil.setNetworkType(type: .None)
            Singleton.isOffline = false
            print(Singleton.isOffline)
            self.homeToDownload()
        }
    }
    
    // function for receiving notification
    @objc func receiveNotifi(obj: NSNotification) {
        guard let type:String = obj.userInfo!["type"] as? String else {
            return
        }
        
        if type.lowercased()=="resourcedownloaded" {
            HomeViewController.firstDownload = true
            showToast(message: AppConstants.contentDownloadedGotoDownloads, force: HomeViewController.firstDownload)
            HomeViewController.firstDownload = false
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
        
        if type.lowercased().hasPrefix("showloader") {
            self.showActivityIndicator()
        }
        if type.lowercased().hasPrefix("hideloader") {
            self.hideActivityIndicator()
        }
    }
    
    
    func homeToDownload(){
        print("function to download")
        let dateLastLoggedIn = UserServices.getLastLoggedIn()
        // Constants which decide what message to be shown
        var userHasLoggedInBefore = false
        var canDownloadsBeAccessible = false
        
        if dateLastLoggedIn != nil {
            userHasLoggedInBefore = true
            
            let daysDifference = DateUtil.getDateDifferenceInDays(date1: Date(), date2: dateLastLoggedIn!)
            if daysDifference<Double(AppConstants.maxOfflineUseInDays) {
                canDownloadsBeAccessible = true
            }
        }
        var message = "It seems that internet is not available."
        
        let alertController = UIAlertController(title: "Network Disconnected", message: message, preferredStyle: .alert)
        alertController.view.layoutIfNeeded() //avoid Snapshotting error
        
        // User has logged in and downloads are accessible
        if userHasLoggedInBefore && canDownloadsBeAccessible && !Singleton.isOffline {
            message = "\(message). Would you like to go to Downloads instead?"
            
            // Initialize Actions for alert
            let yesAction = UIAlertAction(title: "Yes", style: .default) { (action) -> Void in
                Singleton.isOffline = true
                self.performSegue(withIdentifier: "segueHomeToToC", sender: self)
            }
            
            alertController.addAction(yesAction)
            alertController.preferredAction = yesAction
            
        } else if userHasLoggedInBefore && !canDownloadsBeAccessible { // User has logged in, but before the downloads accessiblility conditional number of days
            message = "\(message). \(AppConstants.lexOfflineDownloadsAccessCondition)"
            
            let okayAction = UIAlertAction(title: "Exit", style: .destructive) { (action) -> Void in
                // What to be done after the action has been performed
                exit(0)
            }
            
            alertController.addAction(okayAction)
        } else if !userHasLoggedInBefore {
            message = "\(message). Connect to the internet and Login to access Lex."
            let okayAction = UIAlertAction(title: "Okay", style: .default) { (action) -> Void in
                // What to be done after the action has been perforemed
                // User has never logged into Lex. Does not make sense to keep the app in momory. Will exit now
                // Going back to the home screen
                exit(0)
            }
            TestUtil.userLoggedInBefore = userHasLoggedInBefore
            TestUtil.isDownloadsAccessible = canDownloadsBeAccessible
            alertController.addAction(okayAction)
        }
        alertController.message = message
        if(!Singleton.isOffline){
            self.present(alertController, animated: true, completion: nil)
        }
    }
    
    // this function starts the network notifier which adds observer for change in network type
    func startNetworkNotifier() {
        NotificationCenter.default.addObserver(self, selector: #selector(reachabilityChanged(note:)), name: .reachabilityChanged, object: reachability)
        do{
            try reachability.startNotifier()
        } catch let error as NSError{
            print("could not start reachability notifier due to -> ",error)
        }
        // Adding the downloads and internet connectivity check as well
        let notificationName = NSNotification.Name(rawValue: "LoggedInCheck")
        
        // Adding the notification to change the URL on the click of URL from  chatbot web view
        let chatBotNotificationName = NSNotification.Name(rawValue: "chatBot")
        
        // Adding the notification to change the URL after we come back from the external player
        let externalNotificationName = NSNotification.Name(rawValue: "extPlayer")
        let offlineNotificationName = NSNotification.Name(rawValue: "offlinePlayer")
        
        
        // Register to receive notification for recieveNetworkCheck
        NotificationCenter.default.addObserver(self, selector: #selector(receiveNetworkCheck(obj: )), name: notificationName, object: nil)
        // Register to receive notification for getChatbotData
        NotificationCenter.default.addObserver(self, selector: #selector(getChatbotData(notification: )), name: chatBotNotificationName, object: nil)
        // Register to receive notification for getExternalPlayerData
        NotificationCenter.default.addObserver(self, selector: #selector(getExternalPlayerData(notification: )), name: externalNotificationName, object: nil)
        
        NotificationCenter.default.addObserver(self, selector: #selector(getOfflinePlayerData(notification: )), name: offlineNotificationName, object: nil)
    }
    
    
    // methods for viewDidLoad and viewDidAppear
    override func viewDidLoad() {
        super.viewDidLoad()
        //creating a object for SdkResource Entity
        //        let sdkObject = SdkResource(key: "test", value: Data() , userEmail: "sade", dateInserted: UserServices.getLastLoggedIn()!, dateUpdated: UserServices.getLastLoggedIn()!, tenantName: "", mobileAppTenant: "",type : "Linked")
        //
        //        // saving the entry to the core data
        //        let checked = SdkDataService.saveSdkDataToCoreData(sdkResourceObject: sdkObject, type: sdkObject.type,deleteExisting: false)
        //
        //        print(checked)
        
        //fetching the saved core data entry
        
        //        addChatButton()
        
        //adding notification for device rotation
        NotificationCenter.default.addObserver(self, selector: #selector(self.deviceRotated), name: UIDevice.orientationDidChangeNotification, object: nil)
        
        //        let data = SdkDataService.getCoreDataRowforSdkResource(key: "test",entityName: AppConstants.SdkResourceWithTypeEntityName)
        //        print(data!)
        //
        //        //deleting the entry now
        //        let deleted = SdkDataService.deleteCoreDataWithKey(key: "test",entityName: AppConstants.SdkResourceWithTypeEntityName)
        //        print("Sdk Resource Entry deleted - ",deleted)
        //
        //        let results2 = CoreDataService.getAllRows(entity: AppConstants.lexResourceV2EntityName)
        //        print("LEX RESOURCES v2--",results2!)
        //
        //        let results = CoreDataService.getAllRows(entity: AppConstants.lexResourceEntityName)
        //        print("LEX RESOURCES --",results!)
        
        if WiFiUtil.result {
            let dateLateLoggedIn = UserServices.getLastLoggedIn()
            
            //Constants which decide what message is to be shown
            var userHasLoggedInBefore = false
            if dateLateLoggedIn != nil {
                userHasLoggedInBefore = true
                print(dateLateLoggedIn!)
            }
            
            let message = "LEX Hotspot needs you be logged in atleast once"
            let alertController = UIAlertController(title: "Network Disconnected", message: message, preferredStyle: .alert)
            alertController.view.layoutIfNeeded() //avoid Snapshotting error
            
            if userHasLoggedInBefore {
                goToDownloads()
            } else if !userHasLoggedInBefore {
                let okayAction = UIAlertAction(title: "Okay" , style: .default ) { (action) -> Void in
                    exit(0) }
                alertController.addAction(okayAction)
                alertController.message = message
                self.presentingViewController?.dismiss(animated: true, completion: nil)
                self.present(alertController, animated: true, completion: nil)
            }
        } else {
            SnackBarUtil.removeSnackBarLabel()
            
            // Loading the chatbot in the background
            ChatbotService.loadChatbotWebView(xPos: UIScreen.main.bounds.width, yPos: UIScreen.main.bounds.height)
            
            // Checking if the user has ever logged in, if he has, then asking for touch id
            if UserServices.getLastLoggedIn() != nil {
                AuthenticatorService.authWithBiometrics(finished: { (authResponse) in
                    print(authResponse)
                    
                    // Since this is not a mandatory check, we will launch the web view on all cases, change this feature in future to act accordingly on according situations
                    if authResponse == AuthenticatorService.AuthOptions.SUCCESS {
                        print("User touch/face id successfully authenticated");
                        self.launchHomeWebView()
                    }   else if authResponse == AuthenticatorService.AuthOptions.NOT_ENROLLED {
                        print("User has not enrolled for Any kind of authentication")
                        self.launchHomeWebView()
                    }   else{
                        exit(0)
                    }
                })
            } else {
                self.launchHomeWebView()
            }
        }
    }
    
    override func viewDidAppear(_ animated: Bool) {
        print("Added the notification for Reachibility... ")
        if(Singleton.snackBarShown){
            SnackBarUtil.removeSnackBarLabel()
        }
        startNetworkNotifier()
        _ = NotificationCenter.default.addObserver(self, selector: #selector(appMovedToForeground), name:UIApplication.willEnterForegroundNotification, object: nil)
        externalOpened = false
        if Singleton.sendUserForOpenRap {
            self.webView.reload()
            Singleton.sendUserForOpenRap = !Singleton.sendUserForOpenRap
        }
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        print("Removing the reachability observer from Home View Controller")
        NotificationCenter.default.removeObserver(self, name: .reachabilityChanged, object: reachability)
        print("Removing the foreground observer from Home View Controller")
        NotificationCenter.default.removeObserver(self, name: UIApplication.willEnterForegroundNotification, object: nil)
    }
    
    //function which gets data from chat bot
    @objc func getChatbotData(notification: NSNotification) {
        let typeOfNotif = String(describing: notification.userInfo!["key"]!)
        let value = String(describing: notification.userInfo!["value"]!)
        switch typeOfNotif {
            
        case "urlNav":
            if value.count>0 {
                path = value
                self.reload()
            }
            break
            
        case "goalsNav" :
            if value.count>0 {
                path = value
                self.reload()
            }
            break
            
        case "interestNav" :
            if value.count>0 {
                path = value
                self.reload()
            }
            break
            
        case "homeNav":
            if value.count>0 {
                path = value
                self.reload()
            }
            break
            
        case "searchNav":
            break
            
        case "externalNav":
            if (value.count > 0  && externalOpened == false){
                self.currentExternalURL = value
                goToExternalWebView()
                externalOpened = true
            }
            break
        default:
            break
        }
    }
    
    @objc func getExternalPlayerData(notification: NSNotification) {
        let path = String(describing: notification.userInfo!["path"]!)
        self.path = path
        self.reload()
    }
    
    @objc func getOfflinePlayerData(notification: NSNotification){
        let path = String(describing: notification.userInfo!["path"]!)
        self.path = path
        self.reload()
    }
    
    @objc func receiveNetworkCheck(obj : NSNotification){}
    
    //this method is called when the application is moved to Foreground
    @objc func appMovedToForeground() {
        Timer.scheduledTimer(withTimeInterval: 0.5, repeats: false) { (Timer) in
            print("Application moved to foreground",Singleton.universalLinkClicked)
            if Singleton.universalLinkClicked {
                print("Inside Here")
                self.path = Singleton.universalLink
                self.reload()
            } else {
                WiFiUtil.openRapSuccess() { isConnected in
                    if isConnected == true {
                        WiFiUtil.result = true
                        self.goToDownloads()
                    }
                }
                if NetworkUtil.getNetworkType() == NetworkUtil.NetworkType.None {
                    SnackBarUtil.addSnackBarLabel(webview: self.webView,message: AppConstants.noConnection)
                }
            }
        }
    }
    
    // Chat bot adding and removing methods
    func addChatButton() {
        // Adding the chatbot to the view. If it is not present in the view
        if self.webView.subviews.contains(ChatbotService.chatbotButton) {
            print("Already contains the chatbot button... Not adding it")
        } else {
            // Adding the actions
            ChatbotService.chatbotButton.addTarget(self, action: #selector(launchChatbot), for: .touchUpInside)
            self.webView.insertSubview(ChatbotService.chatbotButton, aboveSubview: self.webView)
        }
    }
    
    func removeChatButton() {
        // Removing the chatbot button, if it is in the view
        if self.webView.subviews.contains(ChatbotService.chatbotButton) {
            ChatbotService.chatbotButton.removeTarget(self, action: #selector(launchChatbot), for: .touchUpInside)
            ChatbotService.chatbotButton.removeFromSuperview()
        } else {
            print("Chtbot button is not present in the view...")
        }
    }
    
    @objc func launchChatbot() {
        let nextViewController = ChatbotViewController()
        nextViewController.modalPresentationStyle = .fullScreen
        self.present(nextViewController, animated:true, completion:nil)
    }
    
    //function for launching the homeWebView
    func launchHomeWebView() {
        didHomeLoad = true
        checkIfOffline()
        NotificationCenter.default.addObserver(self, selector: #selector(receiveNotifi(obj: )), name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil)
        var gotoURL = ""
        if path != nil {
            gotoURL = baseURL + path!
        }
        else{
            gotoURL = baseURL
        }
        if Singleton.universalLinkClicked {
            gotoURL = Singleton.universalLink
            Singleton.universalLinkClicked = false
        }
        TestUtil.gotoURL = gotoURL
        
        let myURL = URL(string: gotoURL)
        let myRequest = URLRequest(url: myURL!)
        
        if !Thread.isMainThread {
            DispatchQueue.main.sync {
                webView.navigationDelegate = self
                UNUserNotificationCenter.current().delegate = self
                webView.configuration.preferences.javaScriptEnabled = true
                webView.configuration.preferences.javaScriptCanOpenWindowsAutomatically = true
                webView.configuration.preferences.setValue(true, forKey: "allowFileAccessFromFileURLs")
                webView.configuration.userContentController.add(self, name: "appRef")
                webView.load(myRequest)
                webView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
                print("My request: ", myRequest)
                webView.scrollView.addSubview(self.refreshControl)
                webView.scrollView.minimumZoomScale = 1.0
                webView.scrollView.maximumZoomScale = 5.0
            }
        } else {
            webView.navigationDelegate = self
            UNUserNotificationCenter.current().delegate = self
            webView.configuration.preferences.javaScriptEnabled = true
            webView.configuration.preferences.javaScriptCanOpenWindowsAutomatically = true
            webView.configuration.preferences.setValue(true, forKey: "allowFileAccessFromFileURLs")
            webView.configuration.userContentController.add(self, name: "appRef")
            webView.load(myRequest)
            webView.scrollView.addSubview(self.refreshControl)
            webView.scrollView.minimumZoomScale = 1.0
            webView.scrollView.maximumZoomScale = 5.0
        }
        
        // Adding the to the web view process pool
        WebViewService.addWebViewToProcessPool(webView: webView)
        
        // Adding the tag to the dev and stage with version numbers
        if Singleton.appConfigConstants.environment.lowercased() != "prod" {
            addAppVersionLabel()
        }
    }
    
    //app version label for development
    func addAppVersionLabel() {
        appVersionLabel.frame = getAppVersionFrame()
        appVersionLabel.text = "\(Singleton.appConfigConstants.environment): \(Bundle.main.releaseVersionNumber!).\(Bundle.main.buildVersionNumber!)"
        appVersionLabel.adjustsFontForContentSizeCategory = true
        appVersionLabel.font = UIFont.boldSystemFont(ofSize: UIFont.smallSystemFontSize)
        appVersionLabel.backgroundColor = UIColor.fromHex(rgbValue: 0xfdffbd, alpha: 0.68)
        appVersionLabel.textAlignment = .center
        appVersionLabel.layer.borderWidth = 0.1
        appVersionLabel.layer.borderColor = UIColor.black.cgColor
        self.webView.addSubview(appVersionLabel)
    }
    
    func getAppVersionFrame() -> CGRect {
        return CGRect(x: appVersionLabelPadding, y: Int(UIScreen.main.bounds.height - CGFloat(appVersionLabelHeight + appVersionLabelPadding) - CGFloat(CGFloat(appVersionLabelHeight)) - CGFloat(CGFloat(appVersionLabelHeight))), width: 100, height: appVersionLabelHeight)
    }
    
    //reload function for webview
    func reload() {
        if path != nil && !(path?.starts(with: "fastrack/"))! {
            let params = path!.components(separatedBy: "?")
            if params.count > 1 {
                let paramsItems = params[1].components(separatedBy: "&")
                var paramsObject: [String:String] = [String:String]()
                
                for paramsItem in paramsItems {
                    let items = paramsItem.components(separatedBy: "=")
                    paramsObject[items[0]]=items[1]
                }
                
                self.webView.evaluateJavaScript("navigateTo('/\(params[0])',\(JSON(paramsObject)))")
            } else {
                path = path?.replacingOccurrences(of: baseURL, with: "")
                self.webView.evaluateJavaScript("navigateTo('/\(path!)')")
            }
        }
        return
    }
    // function to get requests and events from webview
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        print(message.body)
        do {
            let messageData = String(describing: message.body).data(using: .utf8)
            let messageJSON =  try JSONSerialization.jsonObject(with: messageData!, options: [])
            let messageType = JSON(messageJSON)["eventName"].stringValue
            
            print(JSON(messageJSON))
            let isIframeSupported = JSON(messageJSON)["data"]["isIframeSupported"].stringValue
            let resourceType = JSON(messageJSON)["data"]["resourceType"].stringValue.lowercased()
            if (isIframeSupported.lowercased() == "no" && JSON(messageJSON)["data"]["contentType"] == "Resource" && resourceType != "certification" || isIframeSupported.lowercased() == "maybe" && JSON(messageJSON)["data"]["contentType"] == "Resource" && resourceType != "certification"){
                
                let artifactUrl = JSON(messageJSON)["data"]["artifactUrl"].stringValue
                self.currentExternalURL = artifactUrl
                if resourceType == "tryout" {
                    // do nothing
                } else {
//                                        goToExternalWebView()
//                                        self.webView.goBack()
                }
            }
            
            
            
            if (JSON(messageJSON)["data"]["sourceName"] == "IEEE"){
                if(AppConstants.isExternalview == false){
                    let artifactUrl = JSON(messageJSON)["data"]["artifactUrl"].stringValue
                    self.currentExternalURL = artifactUrl
                    AppConstants.isExternalview = true
                    goToExternalWebView()
                    self.webView.goBack()
                }

            }
            
            
            
            
            if(messageType=="TOKEN_OUTGOING") {
                DispatchQueue.main.async {
                    print("Document directory url is ->",self.documentsDirectoryURL)
                    let tokenid = JSON(messageJSON)["data"].stringValue
                    Singleton.accessToken = tokenid
                    if !(Singleton.accessToken.isEmpty) {
                        APIServices.getWToken() {result in
                            let userLoggedInPref = [AppConstants.lastLoggedInKey : String.init(DateUtil.getUnixTimeFromDate(input: Date()))]
                            CoreDataService.saveUserPreferences(entries: userLoggedInPref, uuid: UserDetails.UID,wid:Singleton.wid, accessToken:Singleton.accessToken,  update: true)
                            print("the result", result)
                            
                        }
                    }


                    // Need to re-check this functionality
                    UserDetails.setUserDetails(tokenString: tokenid)
                    
                    // If token comes back, re-save the user preferences that the user has logged in
                    if !tokenid.isEmpty {
                        let userLoggedInPref = [AppConstants.lastLoggedInKey : String.init(DateUtil.getUnixTimeFromDate(input: Date()))]
                        let userLoggedInSaved = CoreDataService.saveUserPreferences(entries: userLoggedInPref, uuid: UserDetails.UID,wid:Singleton.wid, accessToken:Singleton.accessToken,  update: true)
                        
                        print("User pref saved details: \(String(describing: userLoggedInSaved))")
                        
                        if userLoggedInSaved! {
                            let usersLastLoggedInRows = CoreDataService.getAllUserPreferences(keyNames: [AppConstants.lastLoggedInKey]) as! [NSManagedObject]
                            if usersLastLoggedInRows.count>0 {
                                let lastLoggedInTimestampString = usersLastLoggedInRows[0].value(forKey: "value") as! String
                                let uuid = usersLastLoggedInRows[0].value(forKey: "userUuid") as! String
                                print(uuid)
                                let dateLastLoggedIn = Date(timeIntervalSince1970: Double(lastLoggedInTimestampString)!/1000 )
                                print(dateLastLoggedIn)
                            }
                        }
                        
                        // Setting the session ID immediately
                        self.setSessionID(delay: false)
                        
                        //checking if downloads is allowed
                       // self.checkDownloadsFunctionality()
                        
                    } else {
                        print("Token id for this session is empty")
                        // Remove the chatbot here
                        self.removeChatButton()
                    }
                    // Adding the app version. Change this to actual build number for later
                    print("##### Version ####", Bundle.main.releaseVersionNumber!, ":", Bundle.main.buildVersionNumber! )
                    self.webView.evaluateJavaScript("window.__app_version__=\(Bundle.main.releaseVersionNumber!).\(Bundle.main.buildVersionNumber!)", completionHandler: nil)
                }
            } else if(messageType=="SESSIONID_OUTGOING") {
                DispatchQueue.main.async {
                    let sessionid = JSON(messageJSON)["data"].stringValue
                    Singleton.sessionID = sessionid
                }
            } else if(messageType=="GO_OFFLINE") {
                self.goToDownloads()
            } else if messageType == "PORTAL_THEME" {
                let themeDetails = JSON(messageJSON)["data"]["themeDetails"]
                AppConstants.primaryTheme = themeDetails["primary"].stringValue
                AppConstants.primaryName = JSON(messageJSON)["data"]["themeName"].stringValue
                
                //                print(themeDetails)
                let themeColor = AppConstants.primaryTheme.replacingOccurrences(of: "#", with: "")
                ChatbotService.chatbotButton.backgroundColor = UIColor.fromHex(rgbValue: UInt32(String(themeColor),radix: 16)!,alpha: 1.0)
                
            } else if messageType == "RTMP_NAVIGATION" {
                if Connectivity.isConnectedToInternet() {
                    performSegue(withIdentifier: "segueHomeToLiveStreaming", sender: self)
                } else {
                    let alertController = UIAlertController(title: "Network Disconnected", message: AppConstants.noConnection, preferredStyle: .alert)
                    let noAction = UIAlertAction(title: "Okay", style: .default) { (action) -> Void in
                        //Do nothing
                        Singleton.tempCounter = ""
                    }
                    alertController.addAction(noAction)
                    alertController.preferredAction = noAction
                    
                    self.present(alertController, animated: true, completion: nil)
                    
                }
            }
            else if(messageType=="YAMMER_REQUEST"){
                let urlString = JSON(messageJSON)["data"].stringValue
                let url = URL(string: urlString)
                if UIApplication.shared.canOpenURL(url!){
                    UIApplication.shared.open(url!, options: [:], completionHandler: nil)
                    hideActivityIndicator()
                }
            } else if(messageType=="DOWNLOAD_REQUESTED") {
                let data = JSON(messageJSON)["data"].stringValue
                let networkType = NetworkUtil.getNetworkType()
                
                if networkType == .Cellular {
                    var size = 0
                    showActivityIndicator()
                    APIServices.getHierarchy(contentId: data, finished: { (hierarchyJson) in
                        if(JSON(hierarchyJson["result"]["content"]["size"]) != JSON.null){
                            size = Int(JSON(hierarchyJson["result"]["content"]["size"]).stringValue)! / 1000000
                        }
                        let sizeValue = String(size)
                        //                            print("Size is : ",size)
                        var message = AppConstants.continueOnCellular + "Size of Content is " + sizeValue + " Mb"
                        if size <= 5 {
                            message = AppConstants.continueOnCellular
                        }
                        let alertController = UIAlertController(title: "Confirm", message: message , preferredStyle: .alert)
                        alertController.view.layoutIfNeeded() //avoid Snapshotting error
                        
                        
                        // Initialize Actions
                        let yesAction = UIAlertAction(title: "Yes", style: .default) { (action) -> Void in
                            // After the method call completes, we will have all the data needed for downloading, hence remove the loader
                            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "showLoader"])
                            DispatchQueue.global().async {
                                DownloadService.downloadArtifact(withId: data, downloadtype: AppConstants.downloadType.DEFAULT.name())
                            }
                        }
                        
                        let noAction = UIAlertAction(title: "No", style: .default) { (action) -> Void in
                            print("Download request cancelled")
                        }
                        
                        // Add Actions to the alert controller
                        alertController.addAction(noAction)
                        alertController.addAction(yesAction)
                        alertController.preferredAction = yesAction
                        self.hideActivityIndicator()
                        self.present(alertController, animated: true, completion: nil)
                    }
                    )}else {
                    // After the method call completes, we will have all the data needed for downloading, hence remove the loader
                    if !downloadAllowed {
                        showActivityIndicator()
                        let alertController = UIAlertController(title: "Download Disabled", message: AppConstants.downloadNotAllowed , preferredStyle: .alert)
                        alertController.view.layoutIfNeeded()
                        let okayAction = UIAlertAction(title: "Okay", style: .default) { (action) -> Void in
                            print("Download request cancelled")
                        }
                        
                        // Add Actions to the alert controller
                        alertController.addAction(okayAction)
                        alertController.preferredAction = okayAction
                        self.hideActivityIndicator()
                        self.present(alertController, animated: true, completion: nil)
                    }
                    else {
                        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "showLoader"])
                        DispatchQueue.global().async {
                            DownloadService.downloadArtifact(withId: data, downloadtype: AppConstants.downloadType.DEFAULT.name())
                        }
                    }
                }
            } else if(messageType=="NAVIGATION_OCCURED") {
                path = JSON(messageJSON)["data"].stringValue
                
                // Checking if the navigation has occured from the player
                if (path?.starts(with: "/viewer"))! {
                    lastPlayingResourceId = (path?.components(separatedBy: "?")[0].components(separatedBy: "/")[2])!
                }
            } else if (messageType == "CHAT_BOT_VISIBILITY") {
                let value = JSON(messageJSON)["data"].stringValue
                
                if value.lowercased().trimmingCharacters(in: .whitespacesAndNewlines) == "yes" {
                    addChatButton()
                } else {
                    removeChatButton()
                }
            } else if (messageType == "IOS_OPEN_IN_BROWSER") {
                let browserString = JSON(messageJSON)["data"]["url"].stringValue
                
                // Asking the user the permission to open this url in browser
                if let browserUrl = URL(string: browserString) {
                    let alertController = UIAlertController(title: "Information", message: AppConstants.openInBrowserText, preferredStyle: .alert)
                    alertController.view.layoutIfNeeded() //avoid Snapshotting error
                    
                    // Initialize Actions
                    let yesAction = UIAlertAction(title: "Open in browser", style: .default) { (action) -> Void in
                        // Open this URL in browser.
                        if UIApplication.shared.canOpenURL(browserUrl) {
                            if #available(iOS 10.0, *) {
                                UIApplication.shared.open(browserUrl, options: convertToUIApplicationOpenExternalURLOptionsKeyDictionary([:]), completionHandler: nil)
                            } else {
                                UIApplication.shared.openURL(browserUrl)
                            }
                        }
                    }
                    let closeAction = UIAlertAction(title: "Cancel", style: .cancel){ (action) -> Void in
                        //No action
                    }
                    alertController.addAction(yesAction)
                    alertController.addAction(closeAction)
                    alertController.preferredAction = yesAction
                    self.present(alertController, animated: true, completion: nil)
                }
                // If open in browser requested, then the webview might have also gone to the page. Hence going back to the page which has trigerred this event.
                self.webView.goBack()
                
            }
        } catch _ {
            //print("Error----\(error)")
        }
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
        path = webView.url?.path
        
        if path != nil {
            let currentUrl = Singleton.appConfigConstants.appUrl + path!
            var paramPrefix = "?"
            if currentUrl.contains(paramPrefix) {
                paramPrefix = "&"
            }
            guard let reloadUrl = URL(string: Singleton.appConfigConstants.appUrl + path! + "\(paramPrefix)__ios__ts=\(Date().timeIntervalSinceNow)") else {
                return
            }
            URLCache.shared.removeAllCachedResponses()
            webView.evaluateJavaScript("window.location.reload(true)", completionHandler: nil)
            _ = URLRequest(url: reloadUrl)
            //            webView.load(reloadRequest)
        }
        refreshControl.endRefreshing()
        //        self.reload()
    }
    
    
    //device rotation method
    
    @objc func deviceRotated() {
        
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
            let chatbotButtonFrame = CGRect(x: xPos - ChatbotService.padding - ChatbotService.width, y: yPos - ChatbotService.padding - ChatbotService.width, width: ChatbotService.width, height: ChatbotService.width)
            ChatbotService.chatbotButton.frame = chatbotButtonFrame
        }
            
        else if UIDevice.current.orientation.isPortrait {
            let chatbotButtonFrame =  CGRect(x: xPos - 10 - 50, y: yPos - 50 - (yPos/10), width: 50, height: 50)
            ChatbotService.chatbotButton.frame = chatbotButtonFrame
        }
        self.appVersionLabel.frame = self.getAppVersionFrame()
    }
    
    // function for opening offline player
    func goToDownloads() {
        
        let dateLastLoggedIn = UserServices.getLastLoggedIn()
        var canDownloadsBeAccessible = false
        if dateLastLoggedIn != nil {
            
            let daysDifference = DateUtil.getDateDifferenceInDays(date1: Date(), date2: dateLastLoggedIn!)
            if daysDifference < Double(AppConstants.maxOfflineUseInDays) {
                canDownloadsBeAccessible = true
            }
        }
        
        if(canDownloadsBeAccessible) {
            performSegue(withIdentifier: "segueHomeToToC", sender: self)
        } else {
            var message = "It seems that internet is not available."
            
            let alertController = UIAlertController(title: "Network Disconnected", message: message, preferredStyle: .alert)
            alertController.view.layoutIfNeeded() //avoid Snapshotting error
            message = "\(message). \(AppConstants.lexOfflineDownloadsAccessCondition)"
            
            let okayAction = UIAlertAction(title: "Exit", style: .destructive) { (action) -> Void in
                // What to be done after the action has been performed
                exit(0)
            }
            alertController.addAction(okayAction)
            alertController.message = message
            self.present(alertController, animated: true, completion: nil)
        }
    }
    
    func setToken() {
        let source = "setTimeout(function(){ getToken() }, 7000);"
        let userScript = WKUserScript(source: source, injectionTime: WKUserScriptInjectionTime.atDocumentEnd , forMainFrameOnly: false)
        self.webView.configuration.userContentController.addUserScript(userScript)
        
    }
    
    func setSessionID(delay: Bool = true) {
        let source = "setTimeout(function(){ getSessionId() }, " + (delay==true ? "7000": "100") + ");"
        print(source)
        let userScript = WKUserScript(source: source, injectionTime: WKUserScriptInjectionTime.atDocumentEnd , forMainFrameOnly: false)
        self.webView.configuration.userContentController.addUserScript(userScript)
    }
    
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        checkIfOffline()
        showActivityIndicator()
    }
    
    func checkDownloadsFunctionality(){
        let httpsBase = Singleton.appConfigConstants.appUrl
        
        let apiBase = "clientApi/v4"
        let detailsEndpoint = "user/details"
        let accessTokenForAPI = "Bearer \(Singleton.accessToken)"
        let param = "?requiredFields=downloadAllowed"
        
        print(("\(httpsBase)/\(apiBase)/\(detailsEndpoint)/\(UserDetails.UID)/\(param)"))
        print(Singleton.accessToken)
        let headers: HTTPHeaders = [ "Authorization": accessTokenForAPI,"Accept": "application/json"]
        Alamofire.request(
            "\(httpsBase)/\(apiBase)/\(detailsEndpoint)/\(UserDetails.UID)/\(param)",
            method: .get,
            encoding: JSONEncoding.default,
            headers: headers
        ).responseJSON
            {
                response in
                
                switch response.result
                {
                case .success(let data):
                    let status = JSON(data)
                    print(status)
                    if(status["downloadAllowed"] != JSON.null){
                        self.downloadAllowed = status["downloadAllowed"].boolValue
                    }
                    print("Download Allowed",self.downloadAllowed)
                    if !self.downloadAllowed {
                        self.deleteContentForUser()
                    }
                    else {
                        print("Valid User")
                    }
                    
                case .failure( _):
                    print("failure in getting the data", self.downloadAllowed)
                    self.downloadAllowed = true
                }
        }
    }
    
    func deleteContentForUser() {
        
        let contentTypes = ["Course","Collection","Resource"]
        
        for content in contentTypes {
            let results = CoreDataService.getNonLeafLevelData(contentType: content, includeUserInitiated: true)
            let fetchData = results as! [NSManagedObject]
            var deletableResources: [String] = []
            
            for data in fetchData {
                deletableResources.append(data.value(forKeyPath: "content_id") as! String)
            }
            
            for contentId in deletableResources {
                let _ = DownloadedDataService.deleteWith(identifier: contentId)
            }
        }
    }
    
    //MARK: - webview methods
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        DispatchQueue.main.asyncAfter(deadline: .now() + 3, execute: {
            print("stop animation")
            let appDelegate = UIApplication.shared.delegate as! AppDelegate
            appDelegate.stopAnimation()
        })
        if Connectivity.isConnectedToInternet() {
            DispatchQueue.main.asyncAfter(deadline: .now()+10, execute: {
                
                if (!FileManager.default.fileExists(atPath: self.fileUrlForQuizResponse!.path)){
                    //Telemetry().uploadTelemetryData()
                }
                //Telemetry().uploadContinueLearningData()
                //CourseProgress().uploadData()
                QuizResponse().uploadData()
            })
        }
        setSessionID()
    }
    
    
    func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!) {
        hideActivityIndicator()
    }
    
    func goToExternalWebView() {
        // Since external content needs internet to be accessed. Checking if the user is connected to the internet when trying to access this content. If the user is not connected to the internet. Prompting that the link can be visited only when connected.
        if !Connectivity.isConnectedToInternet() {
            // Tell external content can only be visited when online
            let alertController = UIAlertController(title: "Network Disconnected", message: AppConstants.externalContentOnlineCondition, preferredStyle: .alert)
            alertController.view.layoutIfNeeded() //avoid Snapshotting error
            
            // Initialize Actions
            let closeAction = UIAlertAction(title: "Close", style: .default)
            
            alertController.addAction(closeAction)
            self.present(alertController, animated: true, completion: nil)
        } else {
            performSegue(withIdentifier: "segueToExternalViewer", sender: self)
        }
    }
    
    // This will send the data to the segue
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "segueToExternalViewer" {
            let externalViewerController = segue.destination as! ExternalContentViewController
            externalViewerController.webUrl = self.currentExternalURL
            externalViewerController.resourceId = self.lastPlayingResourceId
        }
        if segue.identifier == "segueHomeToToC" && WiFiUtil.result{
            let tocViewController = segue.destination as! ToCViewController
            tocViewController.isOpenedForOpenRap = true
            TestUtil.isOpenedForOpenrap = true
            tocViewController.didHomeViewLoad = didHomeLoad
        }
    }
    
    var currentExternalURL = ""
    
    //This opens external link, not from lex's web view, but in a new web view
    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void,completionHandler: () -> Void) {
        
        //print(navigationAction.request.url?.absoluteString)
        if (navigationAction.request.url?.absoluteString.contains("logout_redirect"))! {
            print("logout in action")
            self.removeChatButton()
            _ = CoreDataService.deleteUserPreferences(keyName: AppConstants.lastLoggedInKey)
        }
        
        //print(navigationAction.request.url?.scheme)
        if (navigationAction.request.url?.scheme == "mailto") {
            UIApplication.shared.open((navigationAction.request.url)!)
            decisionHandler(.cancel)
        }
        
        if navigationAction.request.url?.scheme == "tel" {
            UIApplication.shared.open((navigationAction.request.url)!)
            decisionHandler(.cancel)
        }
        
        if navigationAction.navigationType == .linkActivated {
            //Checking if the user is active
            // This logic will check if the link can be opened in a new instance of the system's default web browser. If the app has permissions to open the link, then the app will open a new safari window with these details
            
            //if session id is not nul check for external links
            if Singleton.sessionID != "" {
                guard let url = navigationAction.request.url else {
                    return
                }
                guard let hostname = url.host else {
                    return
                }
                let possibleExternalString = url.absoluteString
                
                if NetworkFunctions.whiteListChecker(hostToCheck: hostname.lowercased() ) {
                    if possibleExternalString.starts(with: Singleton.appConfigConstants.appUrl) {
                        let splittedPath = possibleExternalString.components(separatedBy:  Singleton.appConfigConstants.appUrl + "/")
                        if splittedPath.count > 1 {
                            path = splittedPath[1]
                            self.reload()
                        }
                    }
                } else {
                    self.currentExternalURL = possibleExternalString
                    goToExternalWebView()
                }
            }
        }
        else{
            completionHandler()
        }
    
        decisionHandler(.allow)
    }
    
    //MARK: - functions for the viewController
    func checkIfOffline() {
        if !Connectivity.isConnectedToInternet() && !WiFiUtil.result{
            
            if !Thread.isMainThread {
                DispatchQueue.main.sync {
                    let appDelegate = UIApplication.shared.delegate as! AppDelegate
                               appDelegate.stopAnimation()
                    
                }
            } else {
                 let appDelegate = UIApplication.shared.delegate as! AppDelegate
                           appDelegate.stopAnimation()
            }
            
            
            
            
           
            
            // Check if the user has ever logged in. If he has logged in and the last logged in is less than AppConstants offline availability days days, prompt him to go to the downloads page. Else tell the user to connect to the internet to use this app.
            let dateLastLoggedIn = UserServices.getLastLoggedIn()
            // Constants which decide what message to be shown
            var userHasLoggedInBefore = false
            var canDownloadsBeAccessible = false
            
            if dateLastLoggedIn != nil {
                userHasLoggedInBefore = true
                
                let daysDifference = DateUtil.getDateDifferenceInDays(date1: Date(), date2: dateLastLoggedIn!)
                if daysDifference<Double(AppConstants.maxOfflineUseInDays) {
                    canDownloadsBeAccessible = true
                }
            }
            var message = "It seems that internet is not available."
            
            let alertController = UIAlertController(title: "Network Disconnected", message: message, preferredStyle: .alert)
            alertController.view.layoutIfNeeded() //avoid Snapshotting error
            
            // User has logged in and downloads are accessible
            if userHasLoggedInBefore && canDownloadsBeAccessible && !Singleton.isOffline {
                message = "\(message). Would you like to go to Downloads instead?"
                
                // Initialize Actions for alert
                let yesAction = UIAlertAction(title: "Yes", style: .default) { (action) -> Void in
                    Singleton.isOffline = true
                    self.performSegue(withIdentifier: "segueHomeToToC", sender: self)
                }
                
                alertController.addAction(yesAction)
                alertController.preferredAction = yesAction
                
            } else if userHasLoggedInBefore && !canDownloadsBeAccessible { // User has logged in, but before the downloads accessiblility conditional number of days
                message = "\(message). \(AppConstants.lexOfflineDownloadsAccessCondition)"
                
                let okayAction = UIAlertAction(title: "Exit", style: .destructive) { (action) -> Void in
                    // What to be done after the action has been performed
                    exit(0)
                }
                
                alertController.addAction(okayAction)
            } else if !userHasLoggedInBefore {
                message = "\(message). Connect to the internet and Login to access Lex."
                let okayAction = UIAlertAction(title: "Okay", style: .default) { (action) -> Void in
                    // What to be done after the action has been perforemed
                    // User has never logged into Lex. Does not make sense to keep the app in momory. Will exit now
                    // Going back to the home screen
                    exit(0)
                }
                TestUtil.userLoggedInBefore = userHasLoggedInBefore
                TestUtil.isDownloadsAccessible = canDownloadsBeAccessible
                alertController.addAction(okayAction)
            }
            alertController.message = message
            if(!Singleton.isOffline){
                self.present(alertController, animated: true, completion: nil)
            }
        } else {}
    }
    
    @objc static func receiveNotification(obj: NSNotification) {
        //print(obj.object.debugDescription)
        //        print("Received the notif!!!")
        //        Notification.showAlert(title: "Download Finished",body: "Your Download has finished. Go to Downloads to see it.")
        //print("Received Notification")
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([.alert, .sound])
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        //print("Notif")
        //UIApplication.shared.applicationIconBadgeNumber = 0
        Singleton.tempCounter = ""
    }
    
    
    
    private static var startAnimCalledCount = 0
    private static var stopAnimCalledCount = 0
    
    
    //function to show activity indicator for loading
    func showActivityIndicator() {
        container.frame = self.view.frame
        container.center = self.view.center
        container.backgroundColor = UIColor(rgb: 0xffffff).withAlphaComponent(0.3)
        
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
        HomeViewController.startAnimCalledCount = HomeViewController.startAnimCalledCount + 1
    }
    
    //function to hide activity indicator
    func hideActivityIndicator() {
        activityIndicator.stopAnimating()
        HomeViewController.stopAnimCalledCount = HomeViewController.stopAnimCalledCount + 1
        container.removeFromSuperview()
    }
}

// Helper function inserted by Swift 4.2 migrator.
fileprivate func convertToUIApplicationOpenExternalURLOptionsKeyDictionary(_ input: [String: Any]) -> [UIApplication.OpenExternalURLOptionsKey: Any] {
    return Dictionary(uniqueKeysWithValues: input.map { key, value in (UIApplication.OpenExternalURLOptionsKey(rawValue: key), value)})
}
