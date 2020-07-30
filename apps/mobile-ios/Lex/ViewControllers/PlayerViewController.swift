//  PlayerViewController.swift
//  Lex
//  Created by Shubham Singh on 3/12/18.
//  Copyright © 2019 Infosys. All rights reserved.

import Foundation
import UIKit
import WebKit
import SwiftyJSON

//Global variables
var bundleURL = Bundle.main.bundleURL.appendingPathComponent("Players/Course")
let baseFolder = bundleURL

var documentDirectory = Singleton.fileManager.urls(for: .documentDirectory, in: .userDomainMask).first!
var cacheDirectory = Singleton.fileManager.urls(for: FileManager.SearchPathDirectory.cachesDirectory, in: .userDomainMask).first!


class PlayerViewController: UIViewController, WKScriptMessageHandler, WKNavigationDelegate {
    @IBOutlet weak var webView: WKWebView!
    
    var container: UIView = UIView()
    var loadingView: UIView = UIView()
    var activityIndicator: UIActivityIndicatorView = UIActivityIndicatorView()
    var isReloaded = false
    
    //Variables and Constants Declarations
    var presentURLID = ""
    var presentCourseID = ""
    var dataToSend = ""
    var mimeType = ""
    var lastNavigatedResourceId = ""
    var previousResourceId = ""
    var quizTitle = ""
    var currentLexId = ""
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let newPlayerPath = documentDirectory.appendingPathComponent("Media_Player").appendingPathComponent("index.html")
        //print(newPlayerPath)
        if Singleton.appConfigConstants.environment.lowercased() != "prod" {
            let oldPlayerPath = Bundle.main.bundleURL.appendingPathComponent("Players/mobile-Media")
            let fileManager = Singleton.fileManager
            let filePath =  documentDirectory.appendingPathComponent("Media_Player")
            if !fileManager.fileExists(atPath: filePath.path) {
                do {
                    try fileManager.copyItem(at: oldPlayerPath, to: filePath)
                } catch _ {
                    Singleton.tempCounter = "0"
                }
            }
        }
        
        self.webView.navigationDelegate = self
        self.webView.configuration.preferences.setValue(true, forKey: "allowFileAccessFromFileURLs")
        self.webView.configuration.allowsInlineMediaPlayback = true
        self.webView.configuration.preferences.javaScriptEnabled = true
        self.webView.configuration.preferences.javaScriptCanOpenWindowsAutomatically = true
        self.webView.configuration.userContentController.add(self, name: "appRef")
        self.webView.loadFileURL(newPlayerPath, allowingReadAccessTo: documentDirectory) //Bundle.main.bundleURL
        
        webView.scrollView.minimumZoomScale = 1.0
        webView.scrollView.maximumZoomScale = 5.0
        
        self.lastNavigatedResourceId = presentURLID
        loadThisResource(resourceID: presentURLID,courseID: presentCourseID)
    }
    
    //method handling Initial Call to the View
    func loadThisResource(resourceID: String, courseID: String) {
        self.presentURLID = resourceID
        self.presentCourseID = courseID
        initializePlayer()
    }
    
    func initializePlayer(){
        
        //showActivityIndicator
        let resourceObject = Resource(resourceID: presentURLID)
        
        //Setting the values
        self.mimeType = (resourceObject?.mimeType)!
        let json = resourceObject?.localJSON
        playerRouter(json: json!, mimeType: self.mimeType)
    }
    
    //method to call respective player functions based on mimeType
    func playerRouter(json: JSON,mimeType: String) {
        let content = json
        let artifactUrl = content["artifactUrl"].stringValue
        let identifier = content["identifier"].stringValue
        quizTitle = content["name"].stringValue
        let documentsDirectoryURL =  FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
        var manifestJsonData : Data?
        
        if mimeType == "application/web-module" || mimeType == "application/quiz" || mimeType == "application/vnd.ekstep.content-collection"{
            if artifactUrl.count>0 {
                var toc = Resource.JSONReader(resourceID: presentCourseID)
                if toc == "" {
                    toc = json
                }
                do {
                    let lastPathComponent = URL(fileURLWithPath: artifactUrl).lastPathComponent
                    let assetsPathComponent = documentsDirectoryURL.absoluteString + identifier + "/assets/" + lastPathComponent
                    
                    let assetsPath = String(assetsPathComponent.suffix(from: assetsPathComponent.index(assetsPathComponent.startIndex, offsetBy: 7)))
                    
                    //TODO: Change reference to SQLite Db
                    if FileManager.default.fileExists(atPath : artifactUrl){
                        manifestJsonData = try Data(contentsOf: URL(fileURLWithPath: artifactUrl), options: .alwaysMapped)
                    } else if FileManager.default.fileExists(atPath: assetsPath){
                        manifestJsonData = try Data(contentsOf: URL(fileURLWithPath: assetsPath), options: .alwaysMapped)
                    }
                    let manifestJsonObj = try JSON(data: manifestJsonData!)
                    
                    var supportData = manifestJsonObj
                    
                    var shouldLoad : Bool = true
                    
                    //Change the JSON if it is quiz, re-direct in case of assessment
                    
                    if mimeType == "application/quiz" {
                        if !checkifAssessment(inputJSON: manifestJsonObj){
                            supportData = quizWithImageJSONModifier(inputJSON: manifestJsonObj)
                        } else {
                            // If connected to Lex-hotspot, do not take user online, else take it online
                            if WiFiUtil.result || !Connectivity.isConnectedToInternet() {
                                supportData = JSON()
                                let alertController = UIAlertController(title: "Not available in offline mode", message: AppConstants.assessmentOnlyOfflineMsg, preferredStyle: .alert)
                                
                                // Initialize Actions
                                
                                let noAction = UIAlertAction(title: "Okay", style: .default) { (action) -> Void in
                                    //Go back to Toc
                                    self.webView.configuration.userContentController.removeScriptMessageHandler(forName: "appRef")
                                    self.navigationController?.popViewController(animated: true)
                                    self.dismiss(animated: true, completion: nil)
                                }
                                alertController.addAction(noAction)
                                self.present(alertController, animated: true, completion: nil)
                            } else {
                                shouldLoad = false
                                let alertController = UIAlertController(title: "Action Required", message: AppConstants.onlineAssesments, preferredStyle: .alert)
                                alertController.view.layoutIfNeeded() //avoid Snapshotting error
                                let yesAction = UIAlertAction(title: "Okay", style: .default) { (action) -> Void in
                                    
                                    self.webView.configuration.userContentController.removeScriptMessageHandler(forName: "appRef")
                                    self.dataToSend = "viewer/\(self.presentURLID)"
                                    self.performSegue(withIdentifier: "unwindToHome", sender: self)
                                }
                                let closeAction = UIAlertAction(title: "Cancel", style: .default){ (action) -> Void in
                                    self.navigationController?.popViewController(animated: true)
                                    self.dismiss(animated: true, completion: nil)
                                }
                                
                                alertController.addAction(yesAction)
                                alertController.addAction(closeAction)
                                alertController.preferredAction = yesAction
                                self.present(alertController, animated: true, completion: nil)
                                
                            }
                        }
                    }
                    if shouldLoad{
                        loadPlayer(content: content, toc: toc,supportData: supportData)
                        
                    }
                } catch _ {
                    Singleton.tempCounter = "0"
                    //print("JSONReadError---");//print(error.localizedDescription)
                }
            }
        } else {
            //print(json)
            let content = json
            var toc = Resource.JSONReader(resourceID: presentCourseID)
            if toc.isEmpty {
                toc = json
            }
            loadPlayer(content: content, toc: toc,supportData: JSON.null)
        }
    }
    func loadPlayer(content: JSON,toc: JSON,supportData: JSON){
        //Writing the content to config.js
        let dict = ["content" : content,
                    "toc":toc,
                    "supportData":supportData]
        let dataToSend = JSON(dict)
        let content = "initApp(\(dataToSend))"
        DispatchQueue.main.asyncAfter(deadline: .now() + 1, execute: {
            //self.hideActivityIndicator()
            self.webView.configuration.userContentController.removeAllUserScripts()
            let userScript = WKUserScript(source: content, injectionTime: WKUserScriptInjectionTime.atDocumentEnd , forMainFrameOnly: false)
            self.webView.configuration.userContentController.addUserScript(userScript)
            self.webView.reload()
            //Saving Telemetry Impression data
            Telemetry().AddImpressionTelemetry(envType: "player", type: "default", pageID: "lex.player", id: self.presentURLID, url:"")
            //Saving Course Progress Data
            CourseProgress().processData(dataToSave: self.presentURLID)
        })
        
    }
    func checkifAssessment(inputJSON: JSON) -> Bool{
        if inputJSON["isAssessment"].stringValue == "true" {
            return true
        }
        return false
    }
    
    
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        ////print(message.body)
        
        do {
            let messageData = String(describing: message.body).data(using: .utf8)
            let messageJSON =  try JSONSerialization.jsonObject(with: messageData!, options: [])
            let messageType = JSON(messageJSON)["eventName"].stringValue
            
            //            print(JSON(messageJSON))
            
            if(messageType=="NAVIGATION_DATA_OUTGOING") {
                previousResourceId = lastNavigatedResourceId
                let lastNavigated = JSON(messageJSON)["data"]["url"].stringValue.split(separator: "/")
                lastNavigatedResourceId = String(lastNavigated[lastNavigated.count-1])
                print("last navigated resource",lastNavigatedResourceId)
                
                let eventType = JSON(messageJSON)["data"]["url"].stringValue.components(separatedBy: "/")[1]
                if(eventType == "viewer"){
                    //print("\(cid)----\(rid)")
                    let cid = JSON(messageJSON)["data"]["params"]["courseId"].stringValue
                    let rid = JSON(messageJSON)["data"]["url"].stringValue.components(separatedBy: "/")[2]
                    self.currentLexId = JSON(messageJSON)["data"]["url"].stringValue.components(separatedBy: "/")[2]
                    self.loadThisResource(resourceID: rid,courseID: cid)
                } /*else if eventType.trimmingCharacters(in: .whitespacesAndNewlines).lowercased() == "home" && WiFiUtil.getWiFiSsid()?.lowercased() == "lex-hotspot" {
                     //                    performSegue(withIdentifier: "playerToOpenRap", sender: self)
                     self.navigationController?.popViewController(animated: true)
                     self.dismiss(animated: true, completion: nil)
                     } */
                else {
                    if !Connectivity.isConnectedToInternet() {
                        let alertController = UIAlertController(title: "Network Disconnected", message: AppConstants.featureOnlyAvailableOnline, preferredStyle: .alert)
                        
                        // Initialize Actions
                        let yesAction = UIAlertAction(title: "Yes", style: .default) { (action) -> Void in
                            self.webView.configuration.userContentController.removeScriptMessageHandler(forName: "appRef")
                            self.navigationController?.popViewController(animated: true)
                            self.dismiss(animated: true, completion: nil)
                        }
                        
                        let noAction = UIAlertAction(title: "Stay Here", style: .default) { (action) -> Void in
                            //Do nothing
                        }
                        
                        // Add Actions
                        alertController.addAction(noAction)
                        alertController.addAction(yesAction)
                        alertController.preferredAction = yesAction
                        
                        self.present(alertController, animated: true, completion: nil)
                    }
                    else{
                        self.webView.configuration.userContentController.removeScriptMessageHandler(forName: "appRef")
                        self.dataToSend = JSON(messageJSON)["data"]["url"].stringValue
                        self.performSegue(withIdentifier: "unwindToHome", sender: self)
                    }
                }
            }
             else if(messageType=="TELEMETRY_DATA_OUTGOING") {
                DispatchQueue.main.async {
                    _ = JSON(messageJSON)["data"].dictionaryObject
                    var quizData = JSON(messageJSON)["data"]
                    print("json->",messageJSON)
                    print(quizData)
                    
                    print(Singleton.fileManager.urls(for: .documentDirectory, in: .userDomainMask).first!)
                    if JSON(messageJSON)["data"]["type"].stringValue.lowercased() == "submit" && JSON(messageJSON)["data"]["plugin"].stringValue.lowercased() == "quiz" {
                        
                        var quizDataSent = JSON()
                        quizDataSent["data"]["identifier"].stringValue = self.lastNavigatedResourceId
                        quizDataSent["data"]["title"] = JSON(self.quizTitle)
                        quizDataSent["data"]["isAssessment"] =  quizData["data"]["isAssessment"]
                        quizDataSent["data"]["timeLimit"] = quizData["data"]["timeLimit"]
                        
                        
                        
                        quizDataSent["data"]["questions"] = quizData["data"]["questions"]
                        quizData["data"]["identifier"] =  JSON(self.lastNavigatedResourceId)
                        //quizData["data"]["userEmail"] = JSON(UserDetails.email)
                        quizData["data"]["title"] = JSON(self.quizTitle)
                       
                        quizData["request"] = JSON(quizData["data"])
                        
                        var quizSentData = JSON()
                        quizSentData["request"] = JSON(quizData["data"])
                        print(quizSentData)
                        
                        let quizResponse = quizSentData.dictionaryObject
                        QuizResponse().processData(dataToSave: quizResponse!)
                    } else if (quizData["plugin"].stringValue.lowercased() == "quiz" && quizData["type"].stringValue.lowercased() == "done") {
                        quizData["data"]["force"] = true
                        quizData["data"]["isIdeal"] = false
                        quizData["data"]["lostFocus"] = false
                        quizData["data"]["courseId"] = JSON.null
                        quizData["data"]["identifier"] = JSON(self.lastNavigatedResourceId)
                        
                        quizData["data"]["mimeType"] = JSON("application/quiz")
                        if(Singleton.quizApiResult.count > 0){
                            print(Singleton.quizApiResult)
                         
                            if(!(Singleton.quizApiResult[self.lastNavigatedResourceId]! as AnyObject).isEmpty){
                                quizData["data"]["details"] = JSON(Singleton.quizApiResult[self.lastNavigatedResourceId]!)
                                let quizJSON = JSON(Singleton.quizApiResult[self.lastNavigatedResourceId]!)
                                let result = quizJSON["result"].intValue
                                let passPercentage = quizJSON["passPercent"].intValue
                                
                                var isCompleted = false
                                if result > passPercentage {
                                    isCompleted = true
                                }
                                
                                quizData["data"]["isCompleted"] = JSON(isCompleted)
                                
                            }
                             
                        }
                       
                        
                        
                        
                        print("after changing  --",quizData)
                    }
                    Telemetry().AddPlayerTelemetry(json: quizData.dictionaryObject!, cid: self.presentCourseID, rid: self.presentURLID,mimeType: self.mimeType)
                }
            }
            else if (messageType == "NAVIGATION_INTENT_OUTGOING") {
                self.webView.configuration.userContentController.removeScriptMessageHandler(forName: "appRef")
                self.navigationController?.popViewController(animated: true)
                self.dismiss(animated: true, completion: nil)
            }
        } catch _ {
            Singleton.tempCounter = "0"
            //print("Error----\(error)")
        }
        //}
    }
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "unwindToHome" {
            let destViewController = segue.destination as! HomeViewController
            destViewController.path = dataToSend
            if dataToSend.lowercased() != "/home" {
                destViewController.reload()
            }
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
        }
        
        if navigationAction.navigationType == .linkActivated {
            print(navigationAction.request.url!)
            showToast(message: AppConstants.offlinePlayerNavigationMessage, force: true)
            if Singleton.sessionID != "" {
                if let url = navigationAction.request.url, let host = url.host?.lowercased() , !NetworkFunctions.whiteListChecker(hostToCheck: host ) {
                    /*if UIApplication.shared.canOpenURL(url){
                     UIApplication.shared.open(url, options: [:], completionHandler: nil)
                     }*/
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
    
    func showActivityIndicator() {
        
        container.frame = self.view.frame
        container.center = self.view.center
        container.backgroundColor = UIColorFromHex(rgbValue: 0xffffff, alpha: 0.3)
        
        
        loadingView.frame = CGRect(x:0, y: 0, width: 80, height: 80)
        loadingView.center = self.view.center
        loadingView.backgroundColor = UIColorFromHex(rgbValue: 0x444444, alpha: 0.7)
        loadingView.clipsToBounds = true
        loadingView.layer.cornerRadius = 10
        
        activityIndicator.frame = CGRect(x: 0.0, y: 0.0, width: 40.0, height: 40.0);
        activityIndicator.style =
            UIActivityIndicatorView.Style.whiteLarge
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
    func UIColorFromHex(rgbValue:UInt32, alpha:Double=1.0)->UIColor {
        let red = CGFloat((rgbValue & 0xFF0000) >> 16)/256.0
        let green = CGFloat((rgbValue & 0xFF00) >> 8)/256.0
        let blue = CGFloat(rgbValue & 0xFF)/256.0
        return UIColor(red:red, green:green, blue:blue, alpha:CGFloat(alpha))
    }
    //This function Chnages the src of images im html from internet reference to local path reference
    func quizWithImageJSONModifier(inputJSON: JSON ) -> JSON {
        var jsonToReturn = inputJSON
        for var i in 0..<inputJSON["questions"].count {
            let questionItem = inputJSON["questions"][i]["question"].stringValue
            //let regex = "<img([\\w\\W]+?)[\\/]?>"
            let regex = "<img\\s+[^>]*src=\"([^\"]*)\"[^>]*>" //Filtering images which have the src tag, not by css assignment
            let images = RegexUtility.matches(for: regex, in: questionItem)
            for img in images{
                let components1 = img.components(separatedBy: "src=")
                if components1.count > 1 {
                    let components2 = components1[1].components(separatedBy: "\"")
                    if components2.count > 1 {
                        let oldImageUrl = components2[1]
                        let components3 = oldImageUrl.components(separatedBy: presentURLID)
                        if components3.count > 1 {
                            let relativeFilePath = components3[1]
                            let newPath = documentDirectory.appendingPathComponent(presentURLID).appendingPathComponent(relativeFilePath)
                            
                            if Singleton.fileManager.fileExists(atPath: newPath.path){
                                let newQuestionItem = inputJSON["questions"][i]["question"].stringValue.replacingOccurrences(of: oldImageUrl, with: newPath.absoluteString)
                                jsonToReturn["questions"][i]["question"].stringValue = newQuestionItem
                            } else {
                                let components3 = oldImageUrl.components(separatedBy: "/")
                                let relativeFilePath = components3[components3.count-1]
                                if components3.count > 1 {
                                    let newPath = documentDirectory.appendingPathComponent(presentURLID).appendingPathComponent("assets/Images/\(relativeFilePath)")
                                    let newQuestionItem = inputJSON["questions"][i]["question"].stringValue.replacingOccurrences(of: oldImageUrl, with: newPath.absoluteString)
                                    jsonToReturn["questions"][i]["question"].stringValue = newQuestionItem
                                }
                            }
                        } else {
                            let components3 = oldImageUrl.components(separatedBy: "/")
                            let relativeFilePath = components3[components3.count-1]
                            if components3.count > 1 {
                                let newPath = documentDirectory.appendingPathComponent(presentURLID).appendingPathComponent("assets/Images/\(relativeFilePath)")
                                let newQuestionItem = inputJSON["questions"][i]["question"].stringValue.replacingOccurrences(of: oldImageUrl, with: newPath.absoluteString)
                                jsonToReturn["questions"][i]["question"].stringValue = newQuestionItem
                            }
                        }
                    }
                }
            }
            i += 1
        }
        return jsonToReturn
    }
}
class Resource {
    //Declarations
    var mimeType = ""
    var localJSON = JSON()
    var fileName = ""
    
    init?(resourceID: String) {
        let json = Resource.JSONReader(resourceID: resourceID)
        //Handle JSON and  assign stuff
        guard let mType = json["mimeType"].string else {
            return nil
        }
        mimeType = mType
        localJSON = json
    }
    static func JSONReader(resourceID: String) -> JSON {
        
        let playerInitData = DownloadedDataService.getDownloadedResource(withId: resourceID)
        
        if playerInitData.count>0 {
            print(JSON(playerInitData[0]["content"] ?? "" ))
            return JSON(playerInitData[0]["content"] ?? "" )
        }
        else{
            return JSON("")
        }
    }
}
