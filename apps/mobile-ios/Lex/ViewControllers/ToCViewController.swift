//  ToCViewController.swift
//  Lex
//  Created by prakash.chakraborty/ Shubham Singh on 3/12/18.
//  Copyright © 2019 Infosys. All rights reserved.

import Foundation
import UIKit
import WebKit
import SwiftyJSON
import QuartzCore
import CoreData
import Alamofire
import UserNotifications
import Reachability

class ToCViewController: UIViewController, WKScriptMessageHandler, WKNavigationDelegate,UITableViewDataSource,UITableViewDelegate {
    
    @IBOutlet weak var webView: WKWebView!
    @IBOutlet weak var coursesBtn: UIButton!
    @IBOutlet weak var modulesBtn: UIButton!
    @IBOutlet weak var resourcesBtn: UIButton!
    @IBOutlet weak var tocView: UIView!
    @IBOutlet weak var dismissToCBtn: UIButton!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var noContentView: UIView!
    @IBOutlet weak var navigationBar: UINavigationBar!
    @IBOutlet weak var tocHam: UIBarButtonItem!
    
    @IBOutlet weak var catalogBarButtonItem: UIBarButtonItem!
    @IBOutlet var marketingBarButtonItem: UIBarButtonItem!
    
    //added infyTVButton
    @IBOutlet weak var infyTVBarButtonItem: UIBarButtonItem!
    // Data for letting the view know if it is opened for openRap
    
    //Views for migration loader
    var migrationContainer: UIView = UIView()
    var migrationLoadingView: UIView = UIView()
    var migrationActivityIndicator: UIActivityIndicatorView = UIActivityIndicatorView()
    var migrationLabel: UILabel = UILabel()
    var migrationPadding = 10.0
    var migrationActivityIndicatorWidth = 40.0
    var migrationActivityIndicatorHeight = 40.0
    
    var maxWidthLoadingView:CGFloat = 0.0
    var dataMigrationCompletedOrNotCalled:Bool = false
    
    var isOpenedForOpenRap = false
    
    var container: UIView = UIView()
    var loadingView: UIView = UIView()
    var activityIndicator: UIActivityIndicatorView = UIActivityIndicatorView()
    
    var dataToSend : String? = nil
    var globalitemID : String = ""
    var resourseID : String = ""
    var courseID: String = ""
    var lastCategorySelected = "Course"
    
    let downloadPersistanceEntityName = "DownloadPersistance"
    
    var sideTableData: [SideBarItem] = [SideBarItem("About","")]
    
    var counter = 0
    var didHomeViewLoad = true
    var loadingTimer : Timer!
    
    var documentDirectory = Singleton.fileManager.urls(for: .documentDirectory, in: .userDomainMask).first!
    
    let reachability = Reachability()!
    
    @objc func reachabilityChanged(note: NSNotification) {
        let reachability = note.object as! Reachability
        
        switch reachability.connection {
        case .wifi:
            WiFiUtil.openRapSuccess() { isConnected in
                if isConnected == true {
                    WiFiUtil.result = true
                    if !self.webView.subviews.contains(OpenRapUtil.hotspotButton!) {
                        OpenRapUtil.createHotSpotButton()
                    }
                    self.addHotspotButton()
                } else {
                    self.removeHotspotButton()
                }
            }
            
        case .cellular:
            self.removeHotspotButton()
            
        case .none:
            self.removeHotspotButton()
            print("No network connection...")
        }
    }
    
    override func willTransition(to newCollection: UITraitCollection, with coordinator: UIViewControllerTransitionCoordinator) {
        DispatchQueue.main.async {
            let orient = UIApplication.shared.statusBarOrientation
            switch orient {
                
            case .portrait:
                SnackBarUtil.snackBarLabel.frame = SnackBarUtil.getSnackBarLabelFrame()
                self.updateMigrationActivityIndicator()
                break
                
            default:
                SnackBarUtil.snackBarLabel.frame = SnackBarUtil.getSnackBarLabelFrame()
                self.updateMigrationActivityIndicator()
                self.migrationLoadingView.frame = CGRect(x: CGFloat((self.view.frame.width/2)) - self.maxWidthLoadingView/2, y: self.view.center.y, width: self.maxWidthLoadingView , height: CGFloat(self.migrationActivityIndicatorHeight) + CGFloat(2*self.migrationPadding))
            }}
    }
    
    //network notifier for adding and removing hotspot button
    func startNetworkNotifier() {
        NotificationCenter.default.addObserver(self, selector: #selector(reachabilityChanged(note:)), name: .reachabilityChanged, object: reachability)
        do {
            try reachability.startNotifier()
        } catch {
            print("could not start reachability notifier")
        }
    }
    
    //method for removing hotspot button
    func removeHotspotButton(){
        OpenRapUtil.hotspotButton?.removeTarget(self, action: #selector(launchOpenRap), for: .touchUpInside)
        OpenRapUtil.hotspotButton?.removeFromSuperview()
        WiFiUtil.result = false
    }
    
    //method for adding hotspot button
    func addHotspotButton(){
        OpenRapUtil.createHotSpotButton()
        OpenRapUtil.hotspotButton?.addTarget(self, action: #selector(launchOpenRap), for: .touchUpInside)
        self.webView.insertSubview(OpenRapUtil.hotspotButton!, aboveSubview: self.webView)
    }
    
    //method to be called when the application is moved to Foreground
    @objc func appMovedToForeground() {
        
        WiFiUtil.openRapSuccess() { isConnected in
            if isConnected == true {
                WiFiUtil.result = true
                if !self.webView.subviews.contains(OpenRapUtil.hotspotButton!) {
                    OpenRapUtil.createHotSpotButton()
                }
                self.addHotspotButton()
            }
            else {
                self.removeHotspotButton()
            }
        }
    }
    
    
    @objc func receiveNotifi(obj: NSNotification) {
        performUserLoggedInCheck()
    }
    
    //viewDidAppear method of viewController
    override func viewDidAppear(_ animated: Bool) {
        // Enabling the hotspot button if the wifi ssid is lex-hotspot
        startNetworkNotifier()
        print("Starting the network observer in TOC")
        _ = NotificationCenter.default.addObserver(self, selector: #selector(appMovedToForeground), name:UIApplication.willEnterForegroundNotification, object: nil)
        print("Starting the foreground observer in TOC")
        if WiFiUtil.result{
            loadDownloads()
            OpenRapUtil.createHotSpotButton()
            addHotspotButton()
        }
    }
    override func viewWillAppear(_ animated: Bool) {
        performUserLoggedInCheck()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.removeHotspotButton()
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        print("Removed the notification from Toc View Controller")
        
        NotificationCenter.default.removeObserver(self, name: .reachabilityChanged, object: reachability)
    }
    
    func performUserLoggedInCheck() {
        
        // Getting the user last logged in
        let dateLastLoggedIn = UserServices.getLastLoggedIn()
        
        // User has logged in before. Check if the logged in date is greater than 7 days
        if dateLastLoggedIn != nil {
            
            print(dateLastLoggedIn!)
            print(Date())
            
            let daysDifference = DateUtil.getDateDifferenceInDays(date1: Date(), date2: dateLastLoggedIn!)
            
            if daysDifference>Double(AppConstants.maxOfflineUseInDays) {
                let alertController = UIAlertController(title: "Login required", message: AppConstants.lexOfflineDownloadsAccessCondition, preferredStyle: .alert)
                
                let exitAction = UIAlertAction(title: "Exit", style: .cancel) { (action) -> Void in
                    UIControl().sendAction(#selector(NSXPCConnection.suspend),
                                           to: UIApplication.shared, for: nil)
                }
                
                let goToHomeAction = UIAlertAction(title: "Go to Home", style: .default) { (action) -> Void in
                    if !self.checkAndShowMsgIfOffline(){
                        self.navigationController?.popViewController(animated: true)
                        self.dismiss(animated: true, completion: nil)
                    }
                }
                
                alertController.addAction(exitAction)
                
                // Adding the go to home only if there is internet connection
                if Connectivity.isConnectedToInternet() {
                    alertController.addAction(goToHomeAction)
                    alertController.preferredAction = goToHomeAction
                }
                self.present(alertController, animated: true, completion: nil)
            }
            print("Difference in days: \(daysDifference)")
        }
    }
    
    //checking if there is any downloaded data for user
    func checkDownloads() {
        var results = CoreDataService.getNonLeafLevelData(contentType: self.lastCategorySelected, includeUserInitiated: true)
        var data = results as! [NSManagedObject]
        if data.count == 0 {
            self.lastCategorySelected = "Collection"
            results = CoreDataService.getNonLeafLevelData(contentType: self.lastCategorySelected, includeUserInitiated: true)
            data = results as! [NSManagedObject]
            if data.count == 0 {
                resourcesBtn.setBorder()
                modulesBtn.removeBorder()
                coursesBtn.removeBorder()
                self.lastCategorySelected = "Resource"
            }
            else{
                modulesBtn.setBorder()
                resourcesBtn.removeBorder()
                coursesBtn.removeBorder()
                self.lastCategorySelected = "Module"
            }
        }
        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        OpenRapUtil.createHotSpotButton()
        OpenRapUtil.hotspotButton?.accessibilityIdentifier = "openRapButton"
        self.tocHam.accessibilityIdentifier = "tocHam"
        self.tocView.isHidden = true
        
        let navigationBarTintColor = AppConstants.primaryTheme.replacingOccurrences(of: "#", with: "")
        navigationBar.barTintColor = UIColor.fromHex(rgbValue: UInt32(String(navigationBarTintColor), radix: 16)!, alpha: 1.0)
        
        
        tableView.isHidden = true
        noContentView.isHidden = true
        self.view.bringSubviewToFront(tableView)
        self.tableView.delegate = self
        self.tableView.dataSource = self
        let newPlayerPath = documentDirectory.appendingPathComponent("ToC_Player").appendingPathComponent("index.html")
        
        checkForExpiringContent()
        
        if Singleton.appConfigConstants.environment.lowercased() != "prod" {
            let oldPlayerPath = Bundle.main.bundleURL.appendingPathComponent("Players/ToC")
            let fileManager = Singleton.fileManager
            let filePath =  documentDirectory.appendingPathComponent("ToC_Player")
            if !fileManager.fileExists(atPath: filePath.path) {
                do {
                    try fileManager.copyItem(at: oldPlayerPath, to: filePath)
                } catch _ {
                    Singleton.tempCounter = "0"
                }
            }
        }
        
        self.webView.configuration.preferences.setValue(true, forKey: "allowFileAccessFromFileURLs")
        self.webView.configuration.preferences.javaScriptEnabled = true
        self.webView.configuration.preferences.javaScriptCanOpenWindowsAutomatically = true
        self.webView.loadFileURL(newPlayerPath, allowingReadAccessTo: documentDirectory)
        self.webView.configuration.userContentController.add(self, name: "appRef")
        
        setStyles()
        
        //Calling the service for migrating LexResource to LexResourceV2
        let userPreferences = CoreDataService.getAllUserPreferences(keyNames: [AppConstants.lastLoggedInKey]) as! [NSManagedObject]
        if userPreferences.count > 0 {
            let uuid = userPreferences[0].value(forKey: "userUuid") as! String
            if uuid != "" {
                let lexResourceEntityRows = CoreDataService.getAllRows(entity: AppConstants.lexResourceEntityName) as! [NSManagedObject]
                if(lexResourceEntityRows.count > 0) {
                    self.showMigrationActivityIndicator()
                    CoreDataMigrateService.reloadMigrationData()
                    Timer.scheduledTimer(withTimeInterval: 5, repeats: false, block: { (Timer) in
                        self.hideMigrationActivityIndicator()
                        self.dataMigrationCompletedOrNotCalled = true
                        self.checkDownloads()
                        self.loadDownloads()
                        
                    })
                } else {
                    dataMigrationCompletedOrNotCalled = true
                    self.checkDownloads()
                    self.loadDownloads()
                }
            }
        }
        webView.scrollView.addSubview(self.refreshControl)
        
        // Taking to openrap view if the request id made to open downloads for openrap
        if isOpenedForOpenRap {
            isOpenedForOpenRap = false
            
            let hotspotAlert = UIAlertController(title: "Lex-Hotspot", message: "Do you want to continue to Lex-hotspot", preferredStyle : UIAlertController.Style.alert)
            
            let goToHotspotAction = UIAlertAction(title: "Yes", style: .default, handler: {(action : UIAlertAction!) in
                self.performSegue(withIdentifier: "downloadsToOpenrap", sender: self)
            })
            let stayHereAction = UIAlertAction(title: "Stay here", style: .cancel, handler: { (action : UIAlertAction!) in
                //DO NOTHING
            })
            hotspotAlert.addAction(goToHotspotAction)
            hotspotAlert.addAction(stayHereAction)
            hotspotAlert.preferredAction = goToHotspotAction
            self.present(hotspotAlert, animated: true, completion: nil)
            
        }
    }
    
    @objc func launchOpenRap(){
        //        print("pressed the button to launch Open Rap page")
        self.performSegue(withIdentifier: "downloadsToOpenrap", sender: self)
        
    }
    func checkForExpiringContent() {
        let oldestResourceDate = CoreDataService.getOldestResourceExpiry()
        
        if oldestResourceDate != nil {
            let dateDifference = DateUtil.getDateDifferenceInDays(date1: Date(), date2: oldestResourceDate!)
            
            if dateDifference>Double(AppConstants.contentExpiryInDays-5) {
                let message = AppConstants.contentExpiringSoon
                let alertController = UIAlertController(title: "Info", message: message, preferredStyle: .alert)
                
                let ignoreAction = UIAlertAction(title: "Ignore", style: .cancel) { (action) -> Void in
                    //Do nothing
                    Singleton.tempCounter = ""
                }
                let extendOption = UIAlertAction(title: "Extend", style: .default) { (action) -> Void in
                    let isContentExpiryUpdated = CoreDataService.updateContentExpiry()
                    if isContentExpiryUpdated {
                        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "Expiry extended further to \(AppConstants.contentExpiryInDays) days./force"])
                    } else {
                        
                    }
                }
                // Add Actions
                alertController.addAction(ignoreAction)
                alertController.addAction(extendOption)
                alertController.preferredAction = extendOption
                
                self.present(alertController, animated: true, completion: nil)
            }
        }
    }
    
    func setStyles()  {
        if counter == 0 {
            coursesBtn.clipsToBounds = true
            modulesBtn.clipsToBounds = true
            resourcesBtn.clipsToBounds = true
            resourcesBtn.removeBorder()
            modulesBtn.removeBorder()
            coursesBtn.setBorder()
            //For Side Bar
            tableView.layer.masksToBounds = false
            tableView.layer.shadowOffset = CGSize(width: 0, height: 3)
            tableView.layer.shadowColor = UIColor.black.cgColor
            tableView.layer.shadowOpacity = 0.23
            tableView.layer.shadowRadius = 4
        }
        counter += 1
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func loadTocData(itemID: String, finished: @escaping (String) -> ()) {
        DispatchQueue.global(qos: .background).async {
            let dataJSON = JSON(DownloadedDataService.getTocFor(identifier: itemID))
            let content = "initApp('toc',\(dataJSON))"
            
            finished(content)
        }
    }
    
    func loadToC(itemID: String){
        showActivityIndicator()
        tocView.isHidden = false
        DispatchQueue.main.async {
            let content = "initApp('toc',''))"
            var userScript = WKUserScript(source: content, injectionTime: WKUserScriptInjectionTime.atDocumentEnd , forMainFrameOnly: false)
            self.webView.configuration.userContentController.addUserScript(userScript)
            self.webView.reload()
            
            self.loadTocData(itemID: itemID, finished: { contentData in
                DispatchQueue.main.async {
                    self.hideActivityIndicator()
                    userScript = WKUserScript(source: contentData, injectionTime: WKUserScriptInjectionTime.atDocumentEnd , forMainFrameOnly: false)
                    self.webView.configuration.userContentController.addUserScript(userScript)
                    self.webView.reload()
                }
            })
        }
        DispatchQueue.main.asyncAfter(deadline: .now()+1, execute: {
            Telemetry().AddImpressionTelemetry(envType: self.lastCategorySelected, type: "toc", pageID: "lex.toc", id: itemID, url: "")
        })
    }
    func loadDownloadData(itemType: String, finished: @escaping ([[String:JSON]]) -> ()) {
        DispatchQueue.global(qos: .background).async {
            if itemType == "Course" {
                finished(DownloadedDataService.getDownloads(type: .Course))
            } else if itemType == "Module" {
                finished(DownloadedDataService.getDownloads(type: .Collection))
            } else if itemType == "Resource" {
                finished(DownloadedDataService.getDownloads(type: .Resource))
            }
        }
    }
    
    //method for getting the parent of the Resource / Module
    func getParentJson(contentId : String) -> JSON{
        let result = CoreDataService.getCoreDataRow(identifier: contentId, uuid: UserDetails.UID)
        if(result != nil){
            let nonNullResults = result as! [NSManagedObject]
            //                print(nonNullResults)
            
            //converting the Binary json to json string
            if(nonNullResults.count > 0){
                let stringJson = CoreDataService.convertBinaryToString(input: nonNullResults[0].value(forKeyPath: "json") as! Data)
                let dataToConvert = JsonUtil.convertJsonFromJsonString(inputJson: stringJson)
                
                //getting the parent Content Id
                let parentJson = (dataToConvert!["collections"])
                return parentJson
            }
        }
        return JSON.null
    }
    
    func stopOngoingTimer(){
        
        if loadingTimer != nil && loadingTimer.isValid {
            loadingTimer.invalidate()
            loadingTimer = nil
            SnackBarUtil.removeSnackBarLabel()
        }
    }
    
    //method for showing OngoingD Downloads in the offline page
    func showOngoingDownloads(){
        //local variables
        var counter = 0
        var parentIds = [String]()
        var parentJson : JSON
        var CourseJson : JSON
        
        //getting all the rows from the Download Persistance table
        let rows = CoreDataService.getAllRows(entity : self.downloadPersistanceEntityName) as! [NSManagedObject]
        for row in rows{
            //            print("download persistance :", (row.value(forKey: "taskId") as! Int32))
            let downloadContentId = row.value(forKey: "contentId") as! String
            print("download persistance id :", (row.value(forKey: "contentId") as! String))
            
            //getting the Lex Resource entry for the content ID obtained from download persistance
            parentJson = getParentJson(contentId: downloadContentId)
            
            //checking if the resource belongs to some module
            if(parentJson.count > 0){
                let parentId = parentJson[0]["identifier"].string
                
                //checking if the module also belongs to a Course
                CourseJson = getParentJson(contentId: parentId!)
                
                //if the module belongs to the course add the Id in the array and append the counter by one
                if(CourseJson.count > 0){
                    let CourseId = CourseJson[0]["identifier"].string
                    if(!parentIds.contains(CourseId!)){
                        parentIds.append(CourseId!)
                        counter+=1
                    }
                }
                    // if the module does not belong to the course add the module Id in the array and append the counter by one
                else{
                    if(!parentIds.contains(parentId!)){
                        parentIds.append(parentId!)
                        counter+=1
                    }
                }
            }
                // if the resource is not belonging to a module or course then increment the counter by 1
            else{
                counter += 1
            }
        }
        //if the counter is more than 0 then load the timer and display the ongoing download message
        if(counter>0){
            if loadingTimer == nil {
                // Adding the telemetry events
                loadingTimer = Timer.scheduledTimer(withTimeInterval: 10, repeats: true, block: { (Void)
                    in
                    print("timer called for ongoing downloads")
                    self.loadDownloads()
                })
            }
            SnackBarUtil.createSnackBarForDownloads(webview: self.webView, message: "\(counter) Ongoing Download(s), Please Wait")
        }
            //stopping the timer if there are no ongoing downloads
        else{
            self.stopOngoingTimer()
        }
    }
    
    //function for loading Downloaded Content
    func loadDownloads() {
        autoreleasepool {
            showActivityIndicator()
            var content = ""
            tocView.isHidden = true
            showOngoingDownloads()
            self.loadDownloadData(itemType: lastCategorySelected, finished: { dataJSON in
                DispatchQueue.main.asyncAfter(deadline: .now() + 1, execute: {
                    self.hideActivityIndicator()
                    if dataJSON.count==0 {
                        self.noContentView.isHidden = false
                    } else {
                        self.noContentView.isHidden = true
                    }
                    let dataToSend = JSON(dataJSON)
                    //print(dataJSON)
                    content = "initApp('downloads',\(dataToSend))"
                    self.webView.configuration.userContentController.removeAllUserScripts()
                    self.hideActivityIndicator()
                    let userScript = WKUserScript(source: content, injectionTime: WKUserScriptInjectionTime.atDocumentEnd , forMainFrameOnly: false)
                    self.webView.configuration.userContentController.addUserScript(userScript)
                    self.webView.reload()
                })
            })
            
            DispatchQueue.main.asyncAfter(deadline: .now()+1, execute: {
                Telemetry().AddImpressionTelemetry(envType: "downloads", type: "list", pageID: "lex.downloads.list", id: "", url: "")
            })
        }
    }
    
    //function to goto player for offline content
    func gotoPlayer(rID: String,cID: String) {
        resourseID = rID
        courseID = cID
        performSegue(withIdentifier: "segueToCTOPlayer", sender: self)
    }
    @IBAction func hamPressed(_ sender: Any) {
        //view.bringSubview(toFront: tableView)
        if tableView.isHidden{
            tableView.isHidden = false
        }
        else{
            tableView.isHidden = true
        }
    }
    
    //    @IBAction func searchClicked(_ sender: Any) {
    //        dataToSend = "search"
    //        if !checkAndShowMsgIfOffline(){
    //            if(checkIfHotspotIsOn()){
    //                showToast(message: "Please Connect to the Internet ",force: true)
    //            }else{
    //                performSegue(withIdentifier: "segueTwo", sender: self)
    //            }
    //        }
    //    }
    //    @IBAction func catalogClicked(_ sender: Any) {
    //        dataToSend = "catalog"
    //        if !checkAndShowMsgIfOffline(){
    //            if(checkIfHotspotIsOn()){
    //                showToast(message: "Please Connect to the Internet ",force: true)
    //            }else{
    //                performSegue(withIdentifier: "segueTwo", sender: self)
    //            }
    //        }
    //    }
    //    @IBAction func marketingClicked(_ sender: Any) {
    //        dataToSend = "catalog/marketing/Brand Assets"
    //        if !checkAndShowMsgIfOffline(){
    //            if(checkIfHotspotIsOn()){
    //                showToast(message: "Please Connect to the Internet ",force: true)
    //            }else{
    //                performSegue(withIdentifier: "segueTwo", sender: self)
    //            }
    //        }
    //    }
    //    //added infyTVClicked for infyTV
    //    @IBAction func infyTVClicked(_ sender: Any) {
    //        dataToSend = "infytv"
    //        if !checkAndShowMsgIfOffline(){
    //            if(checkIfHotspotIsOn()){
    //                showToast(message: "Please Connect to the Internet ",force: true)
    //            }else{
    //                performSegue(withIdentifier: "segueTwo", sender: self)
    //            }
    //        }
    //    }
    
    @IBAction func homeClicked(_ sender: Any) {
        if !checkAndShowMsgIfOffline() {
            if didHomeViewLoad {
                navigationController?.popViewController(animated: true)
                dismiss(animated: true, completion: nil)
            } else {
                DispatchQueue.main.async {
                    // Removing the current view
                    self.performSegue(withIdentifier: "tocToHome", sender: nil)
                    //                    self.dismiss(animated: true, completion: nil)
                }
            }
        }
    }
    @IBAction func dismissToC(_ sender: Any) {
        loadDownloads()
    }
    @IBAction func coursesClicked(_ sender: Any) {
        coursesBtn.setBorder()
        modulesBtn.removeBorder()
        resourcesBtn.removeBorder()
        lastCategorySelected = "Course"
        loadDownloads()
    }
    @IBAction func modulesClicked(_ sender: Any) {
        coursesBtn.removeBorder()
        modulesBtn.setBorder()
        resourcesBtn.removeBorder()
        lastCategorySelected = "Module"
        loadDownloads()
    }
    @IBAction func resourcesClicked(_ sender: Any) {
        resourcesBtn.setBorder()
        modulesBtn.removeBorder()
        coursesBtn.removeBorder()
        lastCategorySelected = "Resource"
        loadDownloads()
    }
    
    //overiding the usual segue between controllers
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "segueTwo" {
            let destViewController = segue.destination as! HomeViewController
            destViewController.path = dataToSend
            destViewController.reload()
        }
        else if segue.identifier == "segueToCTOPlayer" {
            let destViewController = segue.destination as! PlayerViewController
            destViewController.presentURLID = resourseID
            destViewController.presentCourseID = courseID
        }
    }
    
    //function for receiving webView actions
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        //print(message.body)
        do {
            let messageData = String(describing: message.body).data(using: .utf8)
            let messageJSON =  try JSONSerialization.jsonObject(with: messageData!, options: [])
            let messageType = JSON(messageJSON)["eventName"].stringValue
            
            if(messageType=="NAVIGATION_DATA_OUTGOING") {
                self.stopOngoingTimer()
                let cid = JSON(messageJSON)["data"]["params"]["courseId"].stringValue
                let rid = JSON(messageJSON)["data"]["url"].stringValue.components(separatedBy: "/")[2]
                let eventType = JSON(messageJSON)["data"]["url"].stringValue.components(separatedBy: "/")[1]
                if(eventType == "viewer") {
                    
                    if(eventType == "viewer") {
                        if(JSON(messageJSON)["data"]["params"] != JSON.null) {
                            Telemetry().AddContinueLearningTelemetry(contextId: JSON(messageJSON)["data"]["params"]["courseId"].stringValue, rid: rid)
                        }else {
                            if(cid == "") {
                                Telemetry().AddContinueLearningTelemetry(contextId: rid, rid: rid)
                            } else {
                                Telemetry().AddContinueLearningTelemetry(contextId: cid, rid: rid)
                            }
                            
                        }
                        // If the control is going to a player with a resource whici is not downloaded yet, force show a toast message that the content is not downloaded yet.
                        let userPreferences = CoreDataService.getAllUserPreferences(keyNames: [AppConstants.lastLoggedInKey]) as! [NSManagedObject]
                        let uuid = userPreferences[0].value(forKey: "userUuid") as! String
                        let resourceCoreDataRows = CoreDataService.getCoreDataRow(identifier: rid, uuid: uuid) as! [NSManagedObject]
                        
                        if resourceCoreDataRows.count>0 {
                            let data = resourceCoreDataRows[0]
                            
                            let downloadStatus = data.value(forKeyPath: "status") as! String
                            
                            if downloadStatus.lowercased() == "downloaded" {
                                gotoPlayer(rID: rid,cID: cid)
                            } else {
                                // Show toast message saying that the resource is not downloaded yet.
                                NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "showToast/\(AppConstants.contentNotDownloadedYet)/force"])
                            }
                        } else {
                            // If any error, showing the tost message
                            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "showToast/\(AppConstants.contentDownloadError)/force"])
                        }
                    }
                }
                else{
                    loadToC(itemID: rid)
                }
            } else if (messageType=="DOWNLOAD_REMOVE") {
                //Download Remove event for content
                self.stopOngoingTimer()
                let resourceID = JSON(messageJSON)["data"]["id"].stringValue
                
                var msg = AppConstants.confirmDelete
                
                if DownloadedDataService.hasParentDownloaded(contentId: resourceID) {
                    msg = AppConstants.confirmDeleteBelongsToParent
                }
                //Download Remove
                let alertController = UIAlertController(title: "Are you sure?", message: msg, preferredStyle: .alert)
                
                let noAction = UIAlertAction(title: "No", style: .default) { (action) -> Void in
                    //Do nothing
                    Singleton.tempCounter = ""
                }
                let yesAction = UIAlertAction(title: "Yes", style: .destructive) { (action) -> Void in
                    
                    if DownloadedDataService.deleteWith(identifier: resourceID){
                        //Notification.showAlert(title:"Success", body: "Resource have been removed from your device.")
                        Telemetry().AddDownloadTelemetry(rid: resourceID, mimeType: "", contentType: "", status: "removed", mode: AppConstants.downloadType.DEFAULT.name())
                        
                    } else {
                        print("Problem deleting.....")
                    }
                    self.loadDownloads()
                }
                
                alertController.addAction(yesAction)
                alertController.addAction(noAction)
                alertController.preferredAction = noAction
                
                self.present(alertController, animated: true, completion: nil)
            } else if (messageType=="DOWNLOAD_RETRY"){
                //Download Retry
                self.stopOngoingTimer()
                if !Connectivity.isConnectedToInternet() {
                    showToast(message: AppConstants.noConnection, force: true)
                }else{
                    let resourceID = JSON(messageJSON)["data"]["id"].stringValue
                    
                    //removing the old data from the core data and calling download artifact again
                    if DownloadedDataService.deleteWith(identifier: resourceID){
                        DownloadService.downloadArtifact(withId: resourceID,forceDownload: true, downloadtype: AppConstants.downloadType.DEFAULT.name())
                        //loading the downloads page after some time
                        showToast(message: "Download Re-initited", force: true)
                    }
                    Timer.scheduledTimer(withTimeInterval: 1, repeats: false, block: { (void) in
                        self.loadDownloads()
                    })
                }
            }
            else if (messageType=="DOWNLOAD_CANCEL"){
                //added code for cancelling download
                self.stopOngoingTimer()
                let resourceID = JSON(messageJSON)["data"]["id"].stringValue
                let userPreferences = CoreDataService.getAllUserPreferences(keyNames: [AppConstants.lastLoggedInKey]) as! [NSManagedObject]
                let uuid = userPreferences[0].value(forKey: "userUuid") as! String
                let results = CoreDataService.getCoreDataRow(identifier: resourceID,uuid: uuid)
                var taskIdArray = [Int32]()
                
                //updating the content meta to show cancelled as the download status
                if results != nil {
                    let nonNulResults = results as! [NSManagedObject]
                    //                    print(nonNulResults)
                    
                    let json = CoreDataService.convertBinaryToString(input: nonNulResults[0].value(forKeyPath: "json") as! Data)
                    guard let jsonData = JsonUtil.convertJsonFromJsonString(inputJson: json) else {return}
                    
                    var currentChildIds:[String] = []
                    DownloadedDataService.getAllChildrenIds(contentJson: jsonData, childrenList: &currentChildIds)
                    
                    let rows = CoreDataService.getAllRows(entity : self.downloadPersistanceEntityName) as! [NSManagedObject]
                    for row in rows{
                        if(row.value(forKey: "contentId") != nil){
                            if(currentChildIds.contains((row.value(forKey: "contentId") as! String))){
                                taskIdArray.append(row.value(forKey: "taskId") as! Int32)
                                let deleted = CoreDataService.deleteCoreDataForDownloadPersistance(identifier: row.value(forKey: "contentId") as! String)
                                if deleted{
                                    print("deleted")
                                }
                            }
                        }
                    }
                    
                    //getting all the download tasks which started for downloading the content and cancelling them
                    let session = DownloadManager.shared.activate()
                    session.getAllTasks(completionHandler: {(tasks) in
                        for task in tasks {
                            if(taskIdArray.contains(Int32(task.taskIdentifier))){
                                print("task ID : \(task.taskIdentifier)" )
                                task.cancel()
                            }
                        }
                    })
                    
                    let thumbnailURL = (nonNulResults[0].value(forKey: "stringOne") as? String)
                    let artifactURL = nonNulResults[0].value(forKey: "stringTwo") as? String
                    let contentType = nonNulResults[0].value(forKey: "content_type") as? String
                    let userInitiated = nonNulResults[0].value(forKey: "requestedByUser") as? Bool
                    let status = "CANCELLED"
                    let percentComplete = 0
                    
                    let contentMetaObj = ContentModel(identifier: resourceID, thumbnailURL: thumbnailURL!, artifactURL: artifactURL! , contentType: contentType!, resourceJSON: json , userInitiated: userInitiated!, requestedDate: Date(), expiryDate: DateUtil.addDaysToDate(inputDate: Date(), noOfDays: AppConstants.contentExpiryInDays), status: status, percentComplete: percentComplete)
                    
                    let isUpdated = CoreDataService.updateRowInCoreData(withIdentifier: resourceID, newRow: CoreDataService.createCoreDataFromContentModel(contentObj: contentMetaObj))
                    if isUpdated{
                        print("cancelled the download")
                        self.loadDownloads() }
                    
                }else{
                    print("Some Error")
                }
            }
        } catch _ {
            Singleton.tempCounter = "0"
            //print("Error----\(error)")
        }
    }
    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        //print(navigationAction.request.url)
        //print(navigationAction.navigationType.rawValue)
        if navigationAction.navigationType == .linkActivated {
            if Singleton.sessionID != "" {
                if let url = navigationAction.request.url , let host = url.host?.lowercased() , !NetworkFunctions.whiteListChecker(hostToCheck: host ) {
                    if UIApplication.shared.canOpenURL(url){
                        UIApplication.shared.open(url, options: [:], completionHandler: nil)
                    }
                    decisionHandler(.cancel)
                } else {
                    decisionHandler(.allow)
                }
            }
            else{
                decisionHandler(.allow)
            }
        }
        else{
            decisionHandler(.allow)
        }
    }
    func checkAndShowMsgIfOffline() -> Bool {
        if !Connectivity.isConnectedToInternet() {
            var alertController : UIAlertController
            if WiFiUtil.result{
                alertController = UIAlertController(title: "Lex Hotspot", message: AppConstants.inOpenRapMode, preferredStyle: .alert)
            }else {
                alertController = UIAlertController(title: "Network Disconnected", message: AppConstants.inOfflineMode, preferredStyle: .alert)
            }
            let noAction = UIAlertAction(title: "Okay", style: .default) { (action) -> Void in
                //Do nothing
                Singleton.tempCounter = ""
            }
            alertController.addAction(noAction)
            alertController.preferredAction = noAction
            
            self.present(alertController, animated: true, completion: nil)
            return true
        }
        return false
    }
    
    func showActivityIndicator(){
        container.frame = self.view.frame
        container.center = self.view.center
        container.backgroundColor = UIColor.fromHex(rgbValue: 0xffffff, alpha: 0.3)
        
        loadingView.frame = CGRect(x:0, y: 0, width: 80, height: 80)
        loadingView.center = self.view.center
        loadingView.backgroundColor = UIColor.fromHex(rgbValue: 0x444444, alpha: 0.7)
        loadingView.clipsToBounds = true
        loadingView.layer.cornerRadius = 10
        
        activityIndicator.frame = CGRect(x: 0.0, y: 0.0, width: 40.0, height: 40.0);
        activityIndicator.style = UIActivityIndicatorView.Style.whiteLarge
        
        activityIndicator.center = CGPoint(x: loadingView.frame.size.width / 2, y:
            loadingView.frame.size.height / 2);
        loadingView.addSubview(activityIndicator)
        container.addSubview(loadingView)
        self.view.addSubview(container)
        activityIndicator.startAnimating()
    }
    
    func hideActivityIndicator() {
        activityIndicator.stopAnimating()
        container.removeFromSuperview()
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
        self.loadDownloads()
        refreshControl.endRefreshing()
    }
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return sideTableData.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "sideBarCell", for: indexPath)
        cell.textLabel?.text = sideTableData[indexPath.row].name
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if indexPath.row == 0 {
            tableView.isHidden = true
            guard let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String else {
                return
            }
            let alertController = UIAlertController(title: "About Lex", message: "Infosys Lex. Version: \(version) \nContact: ", preferredStyle: .alert)
            
            let noAction = UIAlertAction(title: "Okay", style: .default) { (action) -> Void in
                //Do nothing
                Singleton.tempCounter = "0"
            }
            
            alertController.addAction(noAction)
            alertController.preferredAction = noAction
            
            self.present(alertController, animated: true, completion: nil)
        }
    }
    
    func checkIfHotspotIsOn() -> Bool{
        if WiFiUtil.result{
            return true
        }
        return false
    }
    
    
    func showMigrationActivityIndicator() {
        
        migrationContainer.frame = self.view.frame
        migrationContainer.center = self.view.center
        migrationContainer.backgroundColor = UIColor(rgb: 0xffffff).withAlphaComponent(0.3)
        
        //For the activity indicator
        migrationActivityIndicator.frame = CGRect(x: migrationPadding, y: migrationPadding, width: migrationActivityIndicatorWidth, height: migrationActivityIndicatorHeight)
        
        migrationActivityIndicator.style = UIActivityIndicatorView.Style.whiteLarge
        
        //For the loading view
        maxWidthLoadingView = CGFloat(migrationContainer.frame.width) -  CGFloat(2 * migrationPadding)
        migrationLoadingView.frame = CGRect(x: CGFloat(migrationPadding), y: self.view.center.y, width: CGFloat(migrationContainer.frame.width) -  CGFloat(2 * migrationPadding) , height: CGFloat(migrationActivityIndicatorHeight) + CGFloat(2*migrationPadding))
        migrationLoadingView.backgroundColor = UIColor(rgb: 0x444444).withAlphaComponent(0.8)
        migrationLoadingView.clipsToBounds = true
        migrationLoadingView.layer.cornerRadius = 10
        
        //For the text label
        migrationLabel.frame = CGRect(x: CGFloat((migrationPadding*2) + migrationActivityIndicatorWidth), y: CGFloat(migrationPadding), width: migrationLoadingView.frame.width - (CGFloat(2*migrationPadding) + migrationActivityIndicator.frame.width), height: CGFloat(migrationActivityIndicatorHeight))
        migrationLabel.adjustsFontSizeToFitWidth = true
        migrationLabel.textColor = UIColor.white
        migrationLabel.textAlignment = .justified
        migrationLabel.text = AppConstants.migratingDataText
        migrationLoadingView.addSubview(migrationLabel)
        migrationLoadingView.addSubview(migrationActivityIndicator)
        migrationActivityIndicator.startAnimating()
        migrationContainer.addSubview(migrationLoadingView)
        self.view.addSubview(migrationContainer)
        
    }
    
    func updateMigrationActivityIndicator(){
        //Updating for transitions
        migrationContainer.frame = self.view.frame
        migrationActivityIndicator.frame = CGRect(x: migrationPadding, y: migrationPadding, width: migrationActivityIndicatorWidth, height: migrationActivityIndicatorHeight)
        migrationLoadingView.frame = CGRect(x: CGFloat(migrationPadding), y: self.view.center.y, width: CGFloat(migrationContainer.frame.width) -  CGFloat(2 * migrationPadding) , height: CGFloat(migrationActivityIndicatorHeight) + CGFloat(2*migrationPadding))
        migrationLabel.frame = CGRect(x: CGFloat((migrationPadding*2) + migrationActivityIndicatorWidth), y: CGFloat(migrationPadding), width: migrationContainer.frame.width - (CGFloat(2*migrationPadding) + migrationActivityIndicator.frame.width), height: CGFloat(migrationActivityIndicatorHeight))
        
    }
    
    func hideMigrationActivityIndicator() {
        migrationActivityIndicator.stopAnimating()
        migrationContainer.removeFromSuperview()
    }
    
    func makeResponse(){
        var downloadJson = JSON()
        var contentJson = JSON()
        
        var courseDetailsArray : [JSON] = []
        var moduleDetailsArray : [JSON] = []
        var resourceDetailsArray : [JSON] = []
        
        self.makeDownloadMetaForOffline(name: "Course", contentArray: &courseDetailsArray,userInitiated: true)
        self.makeDownloadMetaForOffline(name: "Collection", contentArray: &moduleDetailsArray,userInitiated: true)
        self.makeDownloadMetaForOffline(name: "Resource", contentArray: &resourceDetailsArray,userInitiated: true)
        
        
        downloadJson["course"] = JSON(courseDetailsArray)
        downloadJson["module"] = JSON(moduleDetailsArray)
        downloadJson["resource"] = JSON(resourceDetailsArray)
        
        makeContentMetaForOffline(name : "Course",contentJSON: &contentJson,userinitiated: true)
        makeContentMetaForOffline(name : "Course",contentJSON: &contentJson,userinitiated: false)
        makeContentMetaForOffline(name : "Collection",contentJSON: &contentJson,userinitiated: true)
        makeContentMetaForOffline(name : "Collection",contentJSON: &contentJson,userinitiated: false)
        makeContentMetaForOffline(name : "Resource",contentJSON: &contentJson,userinitiated: true)
        makeContentMetaForOffline(name : "Resource",contentJSON: &contentJson,userinitiated: false)
        
        print(contentJson)
        var returnJSON = JSON()
        returnJSON["download"] = downloadJson
        returnJSON["content"] = contentJson
        print(returnJSON)
    }
    
    
    func makeContentMetaForOffline(name : String,contentJSON : inout JSON,userinitiated : Bool) {
        let results = CoreDataService.getNonLeafLevelData(contentType: name, includeUserInitiated: userinitiated)
        for data in results as! [NSManagedObject] {
            var childrenIds : [String] = []
            let id = data.value(forKey: "content_id") as! String
            
            let stringJson = CoreDataService.convertBinaryToString(input: data.value(forKeyPath: "json") as! Data)
            
            let dataToConvert = JsonUtil.convertJsonFromJsonString(inputJson: stringJson)
            
            let childrenArray = dataToConvert!["children"].array
            if (childrenArray?.count)! > 0 {
                for child in childrenArray! {
                    print(child)
                    let childContentId: String? = child["identifier"].stringValue
                    childrenIds.append(childContentId!)
                }
            }
            var a = dataToConvert?.dictionaryObject
            a!["children"] = nil
        
            do{
                let jsonData = try JSONSerialization.data(withJSONObject:a!, options:[])
                var dataJSON = JSON(jsonData)
                dataJSON["childrenIds"] = JSON(childrenIds)
                print(dataJSON)
                
                contentJSON[id] = JSON(dataJSON)
                
            }catch {
                print("error occured while converting dictionary to Json")
            }
        }
        
        
    }
    
    
    func makeDownloadMetaForOffline(name : String, contentArray : inout [JSON],userInitiated : Bool){
        let results = CoreDataService.getNonLeafLevelData(contentType: name, includeUserInitiated: userInitiated)
        for data in results as! [NSManagedObject] {
            
            let downloadFinishedOn = DateUtil.getUnixTimeFromDate(input: (data.value(forKeyPath: "modified_date") as! Date))
            let downloadInitOn = DateUtil.getUnixTimeFromDate(input: data.value(forKeyPath: "requested_date") as! Date)
            let expiresOn = DateUtil.getUnixTimeFromDate(input: DateUtil.addDaysToDate(inputDate: (data.value(forKeyPath: "modified_date") as! Date), noOfDays: AppConstants.contentExpiryInDays))
            let status = data.value(forKeyPath: "status") as! String
            let id = data.value(forKey: "content_id") as! String
            var moduleJson = JSON()
            moduleJson["id"] = JSON(id)
            moduleJson["downloadFinishedOn"] = JSON(downloadFinishedOn)
            moduleJson["downloadInitOn"] = JSON(downloadInitOn)
            moduleJson["expires"] = JSON(expiresOn)
            moduleJson["status"] = JSON(status)
            contentArray.append(moduleJson)
        }
    }
}


class SideBarItem {
    var name : String = ""
    var pathToGoTo: String = ""
    init(_ name: String,_ pathToGoTo: String) {
        self.name = name
        self.pathToGoTo = pathToGoTo
    }
}
class Connectivity {
    class func isConnectedToInternet() ->Bool {
        if NetworkReachabilityManager()!.isReachable {
            //            print("Reachable...")
            if WiFiUtil.result {
                print("SSID  is lex-hotspot...")
                return false
            }
            return true
        }
        print("Not reachable...")
        return false
    }
}
extension UIButton {
    func setBorder() {
        let border = CALayer()
        let width = CGFloat(2.0)
        let tintColor = AppConstants.primaryTheme.replacingOccurrences(of: "#", with: "")
        
        border.borderColor = UIColor.fromHex(rgbValue: UInt32(String(tintColor), radix: 16)!, alpha: 1.0).cgColor
        border.frame = CGRect(x: 0, y: self.frame.size.height - width, width:  self.frame.size.width, height: self.frame.size.height)
        border.borderWidth = width
        self.layer.addSublayer(border)
    }
    func removeBorder() {
        let border = CALayer()
        let width = CGFloat(2.0)
        border.borderColor = UIColor.lightGray.cgColor
        border.frame = CGRect(x: 0, y: self.frame.size.height - width, width:  self.frame.size.width, height: self.frame.size.height)
        border.borderWidth = width
        self.layer.addSublayer(border)
    }
}

extension UIColor {
    func fromHex(rgbValue:UInt32, alpha:Double=1.0) -> UIColor {
        let red = CGFloat((rgbValue & 0xFF0000) >> 16)/256.0
        let green = CGFloat((rgbValue & 0xFF00) >> 8)/256.0
        let blue = CGFloat(rgbValue & 0xFF)/256.0
        return UIColor(red:red, green:green, blue:blue, alpha:CGFloat(alpha))
    }
}

