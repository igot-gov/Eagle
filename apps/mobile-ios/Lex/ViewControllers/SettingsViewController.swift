//  SettingsViewController.swift
//  Lex
//  Created by Shubham Singh on 3/12/18.
//  Copyright © 2019 Infosys. All rights reserved.


import UIKit
import WebKit
import SwiftyJSON
import CoreData

class SettingsViewController : UIViewController,WKNavigationDelegate,WKScriptMessageHandler{
    
    @IBOutlet weak var webView: WKWebView!
    var isReloaded = false
    let downloadPersistanceEntityName = "DownloadPersistance"
    var loadingTimer : Timer!
    var lastNavigatedResourceId = ""
    var previousResourceId = ""
    var documentDirectory = Singleton.fileManager.urls(for: .documentDirectory, in: .userDomainMask).first!
    var returnJSON = JSON()
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let newPlayerPath = documentDirectory.appendingPathComponent("mobile_apps").appendingPathComponent("index.html")
        
        webView.frame = CGRect(x: 0,y:0, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height)
        self.webView.navigationDelegate = self
        self.webView.configuration.preferences.setValue(true, forKey: "allowFileAccessFromFileURLs")
        self.webView.configuration.allowsInlineMediaPlayback = true
        self.webView.configuration.preferences.javaScriptEnabled = true
        self.webView.configuration.preferences.javaScriptCanOpenWindowsAutomatically = true
        self.webView.configuration.userContentController.add(self, name: "appRef")
        self.webView.loadFileURL(newPlayerPath, allowingReadAccessTo: documentDirectory)
    }
    
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage){
        print(message.body)
        do{
            
            let messageData = String(describing: message.body).data(using: .utf8)
            let messageObject =  try JSONSerialization.jsonObject(with: messageData!, options: [])
            var messageJSON = JSON(messageObject)
            let messageType = JSON(messageJSON)["type"].stringValue
            
            print(messageType)
            
            if messageType == "APP_LOADED" {
                print("APP LOADED found")
                setTheme()
            } else if messageType == "DATA_REQUEST" {
                let dataRequestType = messageJSON["data"]["type"]
                print(dataRequestType)
                
                if dataRequestType == "APP_DATA" {
                    self.makeResponse()
                    messageJSON["type"] = JSON("DATA_RESPONSE")
                    messageJSON["data"]["response"] = JSON(returnJSON)
                    
                    self.webView.evaluateJavaScript("window.postMessage(\(messageJSON) ,'*')") { (result, error) in
                        if error != nil {
                            print("Error while evaluating the JS")
                            print(error!)
                        } else {
                            print("Evaluated again")
                        }
                    }
                }
                else if dataRequestType == "CONTENT_MANIFEST_REQUEST" {
                    let contentId = messageJSON["data"]["contentId"].stringValue
                    lastNavigatedResourceId = contentId
                    print("Content Id is " ,contentId)
                    
                    messageJSON["type"] = JSON("DATA_RESPONSE")
                    messageJSON["data"]["type"] = JSON("CONTENT_MANIFEST_RESPONSE")
                    let resourceObject = Resource(resourceID: contentId)
                    let json = resourceObject?.localJSON
                    let artifactUrl = json!["artifactUrl"].stringValue
                    var supportData : JSON = JSON()
                    
                    print(artifactUrl)
                    
                    let mimeType = json!["mimeType"].stringValue
                    let resourceType = json!["resourceType"].stringValue
                    
                    
                    if (mimeType == "application/web-module" || mimeType == "application/quiz" && resourceType != "Assessment"){
                        let documentsDirectoryURL =  FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
                        var manifestJsonData : Data?
                        
                        do {
                            let lastPathComponent = URL(fileURLWithPath: artifactUrl).lastPathComponent
                            let assetsPathComponent = documentsDirectoryURL.absoluteString + contentId + "/assets/" + lastPathComponent
                            
                            let assetsPath = String(assetsPathComponent.suffix(from: assetsPathComponent.index(assetsPathComponent.startIndex, offsetBy: 7)))
                            
                            //TODO: Change reference to SQLite Db
                            if FileManager.default.fileExists(atPath : artifactUrl){
                                manifestJsonData = try Data(contentsOf: URL(fileURLWithPath: artifactUrl), options: .alwaysMapped)
                            } else if FileManager.default.fileExists(atPath: assetsPath){
                                manifestJsonData = try Data(contentsOf: URL(fileURLWithPath: assetsPath), options: .alwaysMapped)
                            }
                            let manifestJsonObj = try JSON(data: manifestJsonData!)
                            supportData = manifestJsonObj
                        }catch _ as NSError{}
                    } else if (resourceType == "Assessment"){
                        
                        let alertController = UIAlertController(title: "Not available in offline mode", message: AppConstants.assessmentOnlyOfflineMsg, preferredStyle: .alert)
                        
                        let noAction = UIAlertAction(title: "Okay", style: .default) { (action) -> Void in
                            //Go back to Toc
                            self.webView.goBack()
                        }
                        alertController.addAction(noAction)
                        self.present(alertController, animated: true, completion: nil)
                    }
                    
                    var dataResponse: JSON = JSON()
                    dataResponse["id"] = JSON(contentId)
                    
                    print(messageJSON)
                    
                    var manifestData: JSON = JSON()
                    
                    manifestData["manifestUrl"] = JSON(artifactUrl)
                    manifestData["supportData"] = supportData
                    
                    messageJSON["data"]["response"] = manifestData
                    print(messageJSON)
                    
                    self.webView.evaluateJavaScript("window.postMessage(\(messageJSON) ,'*')") { (result, error) in
                        if error != nil {
                            print("Error while evaluating the JS")
                            print(error!)
                        } else {
                            print("Evaluated again")
                        }
                    }
                }
            } else if messageType == "NAVIGATION_REQUEST" {
                
                let pathData = JSON(messageJSON)["data"]["params"].stringValue
                print("Path data to visit",pathData)
                DispatchQueue.main.async {
                    NotificationCenter.default.post(name: NSNotification.Name(rawValue: "offlinePlayer"), object: self, userInfo: ["path": pathData])
                }
                navigationController?.popViewController(animated: true)
                dismiss(animated: true, completion: nil)
                
            } else if messageType == "OFFLINE_API_DATA" {
                
                let type = JSON(messageJSON)["data"]["data"]["eid"]
                
                if type == "CP_ACTIVITY" {
                    print("CP ACTIVITY found")
                    
                    let data = JSON(messageJSON)["data"]["data"]["eventData"]
                    let mimeType = data["data"]["mimeType"].stringValue
                    let identifier = data["data"]["identifier"].stringValue
                    let courseId = data["data"]["courseId"].stringValue
                    let pluginType = data["plugin"]
                    let dataType = data["type"]
                    var quizData = data["data"]
                    
                    if (pluginType == "quiz" && dataType == "done") {
                        let quizJSON = JSON(Singleton.quizApiResult[identifier]!)
                        let result = quizJSON["result"].intValue
                        let passPercentage = quizJSON["passPercent"].intValue
                        
                        var isCompleted = false
                        if result > passPercentage {
                            isCompleted = true
                        }
                        quizData["data"]["isCompleted"] = JSON(isCompleted)
                        print("after changing  --",quizData)
                    }
                    
                    Telemetry().AddPlayerTelemetryForOffline(json: quizData.dictionaryObject!, cid: courseId, rid: identifier, mimeType: mimeType)
                }
                else if JSON(messageJSON)["data"]["type"] == "QUIZ_SUBMIT" {
                    print("QUIZ SUBMISSION ONGOING")
                    var data = JSON(messageJSON)["data"]["data"]
                    data["userEmail"] = JSON(UserDetails.email)
                    var quizSentData = JSON()
                    quizSentData["request"] = JSON(data)
                    
                    let quizResponse = quizSentData.dictionaryObject
                    QuizResponse().processData(dataToSave: quizResponse!)
                    
                }
                else if type == "CP_IMPRESSION" {
                    print("CP IMPRESSION found")
                    
                    var cpImpressionDictionary: [String:Any] = [:]
                    cpImpressionDictionary["eid"] = JSON(messageJSON)["data"]["data"]["eid"].stringValue
                    cpImpressionDictionary["ets"] = JSON(messageJSON)["data"]["data"]["ets"].intValue
                    cpImpressionDictionary["ver"] = JSON(messageJSON)["data"]["data"]["ver"].doubleValue
                    cpImpressionDictionary["pdataId"] = JSON(messageJSON)["data"]["data"]["pdata"]["id"].stringValue
                    cpImpressionDictionary["eid"] = JSON(messageJSON)["data"]["data"]["eid"].stringValue
                    cpImpressionDictionary["env"] = JSON(messageJSON)["data"]["data"]["edata"]["eks"]["env"].stringValue
                    cpImpressionDictionary["type"] = JSON(messageJSON)["data"]["data"]["edata"]["eks"]["type"].stringValue
                    cpImpressionDictionary["pageId"] = JSON(messageJSON)["data"]["data"]["edata"]["eks"]["pageId"].stringValue
                    if JSON(messageJSON)["data"]["data"]["edata"]["eks"]["id"] != JSON.null {
                        cpImpressionDictionary["id"] = JSON(messageJSON)["data"]["data"]["edata"]["eks"]["id"].stringValue
                        CourseProgress().processData(dataToSave: cpImpressionDictionary["id"] as! String)
                    }
                    cpImpressionDictionary["url"] = JSON(messageJSON)["data"]["data"]["edata"]["eks"]["url"].stringValue
                    
                    
                    Telemetry().AddImpressionTelemetryForOffline(dataDictionary: cpImpressionDictionary)
                    print(cpImpressionDictionary)
                }
                else if JSON(messageJSON)["data"]["type"] == "CONTINUE_LEARNING" {
                    print("Continue_learning")
                    var continueLearningDictionary: [String:Any] = [:]
                    
                    continueLearningDictionary["contextPathId"] = JSON(messageJSON)["data"]["data"]["contextPathId"].stringValue
                    continueLearningDictionary["resourceId"] = JSON(messageJSON)["data"]["data"]["resourceId"].stringValue
                    continueLearningDictionary["percentComplete"] = JSON(messageJSON)["data"]["data"]["percentComplete"].stringValue
                    continueLearningDictionary["data"]  = JSON(messageJSON)["data"]["data"]["data"].stringValue
                    
                    Telemetry().addContinueLearningTelemetryForOffline(continueLearningDictionary: continueLearningDictionary)
                }
            } else if messageType == "DOWNLOAD_ACTION" {
                let downloadAction = messageJSON["data"]["downloadAction"]
                print(downloadAction)
                
                // if the downloadAction is delete, then remove the content
                if downloadAction == "DELETE" {
                    
                    let resourceID = JSON(messageJSON)["data"]["contentId"].stringValue
                    
                    let updatedJson = self.getUpdatedContentStatus(contentId: resourceID, status: "DELETED")
                    
                    
                    if DownloadedDataService.deleteWith(identifier: resourceID){
                        //Notification.showAlert(title:"Success", body: "Resource have been removed from your device.")
                        Telemetry().AddDownloadTelemetry(rid: resourceID, mimeType: "", contentType: "", status: "removed", mode: AppConstants.downloadType.DEFAULT.name())
                        
                        messageJSON["type"] = JSON("DATA_RESPONSE")
                        messageJSON["data"]["type"] = JSON("DATA_UPDATE")
                        messageJSON["data"]["response"] = JSON(updatedJson)
                        
                        //  messageJSON
                        print(messageJSON)
                        
                        self.webView.evaluateJavaScript("window.postMessage(\(messageJSON) ,'*')") { (result, error) in
                            if error != nil {
                                print("Error while evaluating the JS")
                                print(error!)
                            } else {
                                print("Evaluated again")
                            }
                        }
                        
                    } else {
                        print("Problem deleting.....")
                    }
                    
                    
                } else if downloadAction == "CANCEL" {
                    print("Cancel button called")
                    
                    let resourceID = JSON(messageJSON)["data"]["contentId"].stringValue
                    
                    self.stopOngoingTimer()
                    
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
                            //                        self.loadDownloads()
                        }
                        
                    }else{
                        print("Some Error")
                    }
                    
                    let updatedJson = self.getUpdatedContentStatus(contentId: resourceID,status: "CANCELLED")
                    messageJSON["type"] = JSON("DATA_RESPONSE")
                    messageJSON["data"]["type"] = JSON("DATA_UPDATE")
                    messageJSON["data"]["response"] = JSON(updatedJson)
                    print(messageJSON)
                    
                    self.webView.evaluateJavaScript("window.postMessage(\(messageJSON) ,'*')") { (result, error) in
                        if error != nil {
                            print("Error while evaluating the JS")
                            print(error!)
                        } else {
                            print("Evaluated again")
                        }
                    }
                    
                } else if downloadAction == "RETRY" {
                    if !Connectivity.isConnectedToInternet() {
                        showToast(message: AppConstants.noConnection, force: true)
                    }else{
                        let resourceID = JSON(messageJSON)["data"]["contentId"].stringValue
                        let updatedJson = self.getUpdatedContentStatus(contentId: resourceID,status: "DOWNLOADING")
                        messageJSON["type"] = JSON("DATA_RESPONSE")
                        messageJSON["data"]["type"] = JSON("DATA_UPDATE")
                        messageJSON["data"]["response"] = JSON(updatedJson)
                        print(messageJSON)
                        
                        self.webView.evaluateJavaScript("window.postMessage(\(messageJSON) ,'*')") { (result, error) in
                            if error != nil {
                                print("Error while evaluating the JS")
                                print(error!)
                            } else {
                                print("Evaluated again")
                            }
                        }
                        
                        //removing the old data from the core data and calling download artifact again
                        if DownloadedDataService.deleteWith(identifier: resourceID){
                            DownloadService.downloadArtifact(withId: resourceID,forceDownload: true, downloadtype: AppConstants.downloadType.DEFAULT.name())
                            //loading the downloads page after some time
                            showToast(message: "Download Re-initited", force: true)
                            
                        }
                        Timer.scheduledTimer(withTimeInterval: 1, repeats: false, block: { (void) in
                            self.webView.reload()
                        })
                    }
                }
            }
            
        } catch let error as NSError {
            print("Some Error Occured -> ",error)
        }
        
    }
    
    func webViewWebContentProcessDidTerminate(_ webView: WKWebView) {
        print("terminated")
        self.webView.reload()
        self.isReloaded = true
    }
    
    //This opens external link, not from lex, in a browser window
    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        
        if isReloaded{
            showToast(message: AppConstants.offlinePlayerNavigationMessage, force: true)
            isReloaded = false
            self.webView.reload()
        }
        
        if navigationAction.navigationType == .linkActivated {
            print(navigationAction.request.url!)
            showToast(message: AppConstants.offlinePlayerNavigationMessage, force: true)
            if Singleton.sessionID != "" {
                if let url = navigationAction.request.url, let host = url.host?.lowercased() , !NetworkFunctions.whiteListChecker(hostToCheck: host ) {
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
    
    // function to get the updated download status for the content Id
    func getUpdatedContentStatus(contentId : String,status: String) -> JSON {
        var returnJson = JSON()
        let results = CoreDataService.getNonLeafLevelDataForUpdate()
        for data in results as! [NSManagedObject] {
            
            let userUuid = (data.value(forKeyPath: "userUuid") as! String)
            let fetchId = (data.value(forKey: "content_id") as! String)
            
            if (UserDetails.UID == userUuid && contentId == fetchId) {
                
                var childrenIds : [String] = []
                let id = data.value(forKey: "content_id") as! String
                let resourceObject = Resource(resourceID: id)
                let expiresOn = DateUtil.getUnixTimeFromDate(input: DateUtil.addDaysToDate(inputDate: (data.value(forKeyPath: "modified_date") as! Date), noOfDays: AppConstants.contentExpiryInDays))
                var statusCheck = data.value(forKeyPath: "status") as! String
                let name = data.value(forKey: "content_type") as! String
                
                //          Setting the values
                let json = resourceObject?.localJSON
                let artifactUrl = json!["artifactUrl"].stringValue
                let thumbnailUrl = json!["thumbnail"].stringValue
                let stringJson = CoreDataService.convertBinaryToString(input: data.value(forKeyPath: "json") as! Data)
                var dataToConvert = JsonUtil.convertJsonFromJsonString(inputJson: stringJson)
                let childrenArray = dataToConvert!["children"].array
                if (childrenArray?.count)! > 0 {
                    for child in childrenArray! {
                        let childContentId: String? = child["identifier"].stringValue
                        childrenIds.append(childContentId!)
                    }
                }
                
                var returnJsonEntry = dataToConvert?.dictionaryObject
                returnJsonEntry!["children"] = nil
                returnJsonEntry!["artifactUrl"] = artifactUrl
                returnJsonEntry!["appIcon"] = thumbnailUrl
                
                //checking for status of the content
                
                // if the content is course or collection then update the status accordingly
                if (name == "Collection" || name == "Course") {
                    let resourceDict = DownloadedDataService.showDownloaded(identifier: id)
                    if resourceDict.count == 0 {
                        // This means that all the resources in the collection or course have been deleted.
                        // We will delete the parent as well now
                        _ = DownloadedDataService.deleteWith(identifier: id)
                        continue
                    }
                    
                    var allResourcesIds:[String] = []
                    DownloadedDataService.getAllChildResourceIds(contentJson: JSON(data.value(forKeyPath: "json") ?? JSON()) , childrenList: &allResourcesIds)
                    
                    var percentageProgressOfCourse: Double = 0.0
                    var count = 0;
                    
                    for (_, result) in resourceDict {
                        count = count + 1
                        percentageProgressOfCourse = percentageProgressOfCourse + result["progress"].doubleValue
                    }
                    
                    percentageProgressOfCourse = percentageProgressOfCourse/Double(allResourcesIds.count)
                    
                    var partialDownloadStatus = statusCheck
                    if resourceDict.count == 0 && allResourcesIds.count>0 {
                        partialDownloadStatus = "ALL RESOURCES DELETED BY USER"
                    }
                    if resourceDict.count>0 && allResourcesIds.count>resourceDict.count {
                        partialDownloadStatus = "DOWNLOADING"
                    }
                    
                    statusCheck = percentageProgressOfCourse >= Double(100) ? "DOWNLOADED" : statusCheck.count>0 ?partialDownloadStatus : statusCheck
                }
                
                
                
                do{
                    let jsonData = try JSONSerialization.data(withJSONObject:returnJsonEntry!, options:[])
                    var dataJSON = JSON(jsonData)
                    dataJSON["childrenIds"] = JSON(childrenIds)
                    dataJSON["expires"] = JSON(expiresOn)
                    dataJSON["downloadStatus"] = JSON(status)
                    returnJson = dataJSON
                    
                } catch {
                    print("error occured while converting dictionary to Json")
                }
            }
        }
        print(returnJson)
        return returnJson
    }
    
    // this function sets the theme of the offline player
    func setTheme(){
        do{
            let jsonString = String(describing: "{\"app\":\"OFFLINE_APP\",\"plugin\":\"NONE\",\"state\":\"LOADED\",\"type\":\"OFFLINE_THEME_UPDATE\",\"data\":\"\(AppConstants.primaryName)\"}").data(using: .utf8)
            let jsonObject =  try JSONSerialization.jsonObject(with: jsonString!, options: [])
            let json = JSON(jsonObject)
            print(json)
            self.webView.evaluateJavaScript("window.postMessage(\(json) ,'*')") { (result, error) in
                if error != nil {
                    print("Error while evaluating the JS for OFFLINE_THEME_UPDATE")
                    print(error!)
                } else {
                    print("Evaluated OFFLINE_THEME_UPDATE")
                }
            }
        }catch let error as NSError {
            print(error)
        }
    }

    // this function makes the response to be sent on DATA_RESPONSE to show downloaded content from mobile.
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
        
        returnJSON["download"] = downloadJson
        returnJSON["content"] = contentJson
        
        print(returnJSON)
    }
    
    func makeContentMetaForOffline(name : String,contentJSON : inout JSON,userinitiated : Bool) {
        let results = CoreDataService.getNonLeafLevelData(contentType: name, includeUserInitiated: userinitiated)
        for data in results as! [NSManagedObject] {
            
            let userUuid = (data.value(forKeyPath: "userUuid") as! String)
            
            if UserDetails.UID == userUuid {
                
                var childrenIds : [String] = []
                let id = data.value(forKey: "content_id") as! String
                let resourceObject = Resource(resourceID: id)
                let expiresOn = DateUtil.getUnixTimeFromDate(input: DateUtil.addDaysToDate(inputDate: (data.value(forKeyPath: "modified_date") as! Date), noOfDays: AppConstants.contentExpiryInDays))
                var status = data.value(forKeyPath: "status") as! String
                
                
                
                //          Setting the values
                let json = resourceObject?.localJSON
                let artifactUrl = json!["artifactUrl"].stringValue
                let thumbnailUrl = json!["thumbnail"].stringValue
                let stringJson = CoreDataService.convertBinaryToString(input: data.value(forKeyPath: "json") as! Data)
                var dataToConvert = JsonUtil.convertJsonFromJsonString(inputJson: stringJson)
                let childrenArray = dataToConvert!["children"].array
                if (childrenArray?.count)! > 0 {
                    for child in childrenArray! {
                        let childContentId: String? = child["identifier"].stringValue
                        childrenIds.append(childContentId!)
                    }
                }
                
                var returnJsonEntry = dataToConvert?.dictionaryObject
                returnJsonEntry!["children"] = nil
                returnJsonEntry!["artifactUrl"] = artifactUrl
                returnJsonEntry!["appIcon"] = thumbnailUrl
                
                
                //checking for status of the content
                
                // if the content is course or collection then update the status accordingly
                if (name == "Collection" || name == "Course") {
                    let resourceDict = DownloadedDataService.showDownloaded(identifier: id)
                    if resourceDict.count == 0 {
                        // This means that all the resources in the collection or course have been deleted.
                        // We will delete the parent as well now
                        _ = DownloadedDataService.deleteWith(identifier: id)
                        continue
                    }
                    
                    var allResourcesIds:[String] = []
                    DownloadedDataService.getAllChildResourceIds(contentJson: JSON(data.value(forKeyPath: "json") ?? JSON()) , childrenList: &allResourcesIds)
                    
                    var percentageProgressOfCourse: Double = 0.0
                    var count = 0;
                    
                    for (_, result) in resourceDict {
                        count = count + 1
                        percentageProgressOfCourse = percentageProgressOfCourse + result["progress"].doubleValue
                    }
                    
                    percentageProgressOfCourse = percentageProgressOfCourse/Double(allResourcesIds.count)
                    
                    var partialDownloadStatus = status
                    if resourceDict.count == 0 && allResourcesIds.count>0 {
                        partialDownloadStatus = "ALL RESOURCES DELETED BY USER"
                    }
                    if resourceDict.count>0 && allResourcesIds.count>resourceDict.count {
                        partialDownloadStatus = "DOWNLOADING"
                    }
                    
                    status = percentageProgressOfCourse >= Double(100) ? "DOWNLOADED" : status.count>0 ?partialDownloadStatus : status
                }
                
                
                
                do{
                    let jsonData = try JSONSerialization.data(withJSONObject:returnJsonEntry!, options:[])
                    var dataJSON = JSON(jsonData)
                    dataJSON["childrenIds"] = JSON(childrenIds)
                    dataJSON["expires"] = JSON(expiresOn)
                    dataJSON["downloadStatus"] = JSON(status)
                    contentJSON[id] = JSON(dataJSON)
                    
                    
                } catch {
                    print("error occured while converting dictionary to Json")
                }
            }
        }
    }
    
    func makeDownloadMetaForOffline(name : String, contentArray : inout [JSON],userInitiated : Bool){
        let results = CoreDataService.getNonLeafLevelData(contentType: name, includeUserInitiated: userInitiated)
        
        for data in results as! [NSManagedObject] {
            
            let userUuid = (data.value(forKeyPath: "userUuid") as! String)
            
            if UserDetails.UID == userUuid {
                
                let downloadFinishedOn = DateUtil.getUnixTimeFromDate(input: (data.value(forKeyPath: "modified_date") as! Date))
                let downloadInitOn = DateUtil.getUnixTimeFromDate(input: data.value(forKeyPath: "requested_date") as! Date)
                let id = data.value(forKey: "content_id") as! String
                
                var moduleJson = JSON()
                moduleJson["id"] = JSON(id)
                moduleJson["downloadFinishedOn"] = JSON(downloadFinishedOn)
                moduleJson["downloadInitOn"] = JSON(downloadInitOn)
                
                
                contentArray.append(moduleJson)
            }
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
            //            print("download persistance id :", (row.value(forKey: "contentId") as! String))
            
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
                    //                    print("timer called for ongoing downloads")
                    //                    print("download completed")
                })
            }
            SnackBarUtil.createSnackBarForOffline(webview: self.webView, message: "\(counter) Ongoing Download(s), Please Wait")
        }
            //stopping the timer if there are no ongoing downloads
        else{
            self.stopOngoingTimer()
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
                var dataToConvert = JsonUtil.convertJsonFromJsonString(inputJson: stringJson)
                
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
    
}
