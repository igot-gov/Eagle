//  DownloadService.swift
//  Lex
//  Created by Abhishek Gouvala / Shubham Singh on 3/21/18.
//  Copyright © 2018 Infosys. All rights reserved.

import Foundation
import SwiftyJSON
import Alamofire
import CoreData

class DownloadService: NSObject {
    
    static var isUserInitiatedItem = false
    static var baseURL = Singleton.appConfigConstants.appUrl
    static let documentsDirectoryURL =  FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
    static let internalContentStoreIpUrl = ""
    static let internalStaticDirStroreUrl = ""
    
    static func downloadArtifact(withId: String, forceDownload: Bool = false, userInitiated: Bool = false, downloadtype: String) {
        if(downloadtype == AppConstants.downloadType.OPEN_RAP.name()) {
            //open rap content downloading
            DispatchQueue.main.async {
                isUserInitiatedItem = true
                self.downloadHierarically(contentId: withId, DownloadType: AppConstants.downloadType.OPEN_RAP.name())
            }
        } else {
            DispatchQueue.main.async {
                isUserInitiatedItem = true
                self.downloadHierarically(contentId: withId, DownloadType: AppConstants.downloadType.DEFAULT.name())
            }
        }
    }
    
    //methods for updating downloaded data which is downloaded from open rap
    static func updateArtifact(lexId : String, filePath : URL , userInitiated : Bool = false){
        DispatchQueue.main.async {
            let deleted =  CoreDataService.deleteCoreDataForDownloadPersistance(identifier: lexId)
            if deleted{
                print("Deleted core data entry")
                print(documentsDirectoryURL)
                let openRapFileUrl = documentsDirectoryURL.appendingPathComponent(lexId + ".lex")
                if FileManager.default.fileExists(atPath: openRapFileUrl.path) {
                    try? FileManager.default.removeItem(at: openRapFileUrl)
                }
            }
            self.updateHierarically(contentId : lexId, filePath: filePath)
        }
    }
    
    static func updateHierarically(contentId: String, filePath: URL?) {
        print("Directory Found")
        let sourceUrl = filePath?.appendingPathComponent(contentId+"/\(contentId)" + ".json")
        do {
            // Get the contents of the unzipped file
            let data = try String(contentsOf: sourceUrl!)
            guard let jsonData = JsonUtil.convertJsonFromJsonString(inputJson: data) else {return}
            let contentJson = JSON(jsonData["result"]["content"])
            DownloadService.initiateAndUpdateDatabase(contentJson : contentJson, userInitiated : true)
        }catch let error as NSError {
            print("Something went wrong: \(error)")
        }
        
    }
    
    //lex-hotspot content updation
    static func initiateAndUpdateDatabase(contentJson: JSON, userInitiated: Bool = false) {
        // Taking the data from the JSON to create a model out of it.
        let contentId: String? = contentJson["identifier"].stringValue
        let thumbnailUrl: String? = contentJson["appIcon"].stringValue
        let artifactUrl: String? = contentJson["artifactUrl"].stringValue
        let contentType: String? = contentJson["contentType"].stringValue
        let resourceJSONStr = contentJson.rawString()
        
        let contentMetaObj = ContentModel(identifier: contentId!, thumbnailURL: thumbnailUrl!, artifactURL: artifactUrl!, contentType: contentType!, resourceJSON: resourceJSONStr!, userInitiated: userInitiated, requestedDate: Date(), expiryDate: DateUtil.addDaysToDate(inputDate: Date(), noOfDays: AppConstants.contentExpiryInDays), status: "DOWNLOADED", percentComplete: 100)
        let resourceJson = JsonUtil.convertJsonFromJsonString(inputJson: resourceJSONStr!)
        
        var replacedThumbnailURL = resourceJson!["appIcon"].stringValue.replacingOccurrences(of: internalContentStoreIpUrl, with: baseURL)
        replacedThumbnailURL = replacedThumbnailURL.replacingOccurrences(of: internalStaticDirStroreUrl, with: baseURL)
        
        var replacedArtifactUrl = resourceJson!["artifactUrl"].stringValue.replacingOccurrences(of: internalContentStoreIpUrl, with: baseURL)
        replacedArtifactUrl = replacedArtifactUrl.replacingOccurrences(of: internalStaticDirStroreUrl, with: baseURL)
        
        if replacedThumbnailURL.hasPrefix("http://") || replacedThumbnailURL.hasPrefix("https://") {
            replacedThumbnailURL = replacedThumbnailURL.trimmingCharacters(in: .whitespacesAndNewlines)
        }
        if resourceJson!["contentType"].stringValue.lowercased() == "resource" {
            if replacedArtifactUrl.hasPrefix("http://") || replacedArtifactUrl.hasPrefix("https://") {
                replacedArtifactUrl = replacedArtifactUrl.trimmingCharacters(in: .whitespacesAndNewlines)
            }
        }
        
        var newThumbnailURL = replacedThumbnailURL.split(separator: "?")
        print(newThumbnailURL)
        newThumbnailURL = newThumbnailURL[0].split(separator: "/")
        
        if replacedArtifactUrl != "" {
            let newArtifactURL = replacedArtifactUrl.split(separator: "/")
            replacedArtifactUrl = "\(contentId!)/\(newArtifactURL[newArtifactURL.count-1])"
            if !replacedArtifactUrl.hasSuffix(".json"){
                replacedArtifactUrl.insert("/", at: newArtifactURL[0].startIndex)
            }
            if (replacedArtifactUrl.contains("?")){
                let replacedUrls = replacedArtifactUrl.split(separator: "?")
                print(replacedUrls)
                contentMetaObj.artifactURL = String(replacedUrls[0])
            }
            else{
                contentMetaObj.artifactURL = replacedArtifactUrl
            }
        }
        contentMetaObj.thumbnailURL = "/\(contentId!)/\(newThumbnailURL[newThumbnailURL.count-1])"
        
        //checking for thumbnail at location
        let documentDirectoryPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
        
        let thumbnailCheckPath = documentDirectoryPath.path+contentMetaObj.thumbnailURL
        if !FileManager.default.fileExists(atPath: thumbnailCheckPath){
            print("error in finding the thumbnail at the path in JSON")
            // changing the content meta thumbnail url because the thumbnail url in the JSON is not matching with the downloaded file
            let alternateArtifactUrls = newThumbnailURL[newThumbnailURL.count-1].split(separator: ".")
            let fileExtension = alternateArtifactUrls[alternateArtifactUrls.count-1]
            let newUrl = "/\(contentId!)/\(contentId!)"+".\(fileExtension)"
            
            if !FileManager.default.fileExists(atPath: documentDirectoryPath.path+newUrl){
                let newUrl = "/\(contentId!)/\(contentId!)"+".png"
                contentMetaObj.thumbnailURL = newUrl
            }
            else{
                contentMetaObj.thumbnailURL = newUrl
            }
        }
        //checking for artifact at location
        if (contentMetaObj.artifactURL?.contains(".mp4"))!{
            print("error in finding the artifact at the path in JSON")
            
            // changing the content meta artifact url because the artifact url in the JSON is not matching with the downloaded file
            let alternateArtifactUrls = contentMetaObj.artifactURL?.split(separator: ".")
            let fileExtension = String(alternateArtifactUrls![(alternateArtifactUrls!.count)-1])
            let newUrl = "/\(contentId!)/\(contentId!)"+".\(fileExtension)"
            
            contentMetaObj.artifactURL = newUrl
        }
        
        let isSaved = CoreDataService.saveDataToCoreData(coreDataObj: CoreDataService.createCoreDataFromContentModel(contentObj: contentMetaObj))        
        
        //iterate over the children and call the method recursifvely.
        let childrenArray = contentJson["children"].array
        for child in childrenArray!{
            let childContentId: String? = child["identifier"].stringValue
            if childContentId != nil && !(childContentId?.isEmpty)! {
                self.initiateAndUpdateDatabase(contentJson: child, userInitiated: false)
            }
        }
    }
    
    static func checkIfAllArtifactsAreDownloadable(content: JSON) -> Bool {
        // let isExternal = content["isExternal"].stringValue.lowercased().trimmingCharacters(in: .whitespacesAndNewlines)
        let downloadURL = content["downloadUrl"].stringValue
        let contentType = content["contentType"].stringValue.lowercased()
        let courseType = content["courseType"].stringValue.lowercased()
        
        if contentType == "resource" {
            if downloadURL.trimmingCharacters(in: .whitespacesAndNewlines).count>0 {
                return true
            }
            return false
        } else if(contentType == "course" && courseType == "classroom"){
            return false
        }else {
            let children = content["children"].arrayValue
            var returnVal = true
            for child in children {
                if !checkIfAllArtifactsAreDownloadable(content: child) {
                    returnVal = false
                    break
                }
            }
            return returnVal
        }
    }
    
    private static func downloadHierarically(contentId: String, DownloadType: String, json: JSON? = nil) {
        // if the download request is coming from Open Rap
        if(DownloadType == AppConstants.downloadType.OPEN_RAP.name()){
            print("DOWNLOADING OPENRAP CONTENT")
            
            // Download url for the open Rap Content
         //   "
//            guard let encryptedFileDowloadURL = URL(string: openRapDownloadUrl) else { return }
//
//            let session = DownloadManager.shared.activate()
//            let task = session.downloadTask(with: encryptedFileDowloadURL)
//            if let sessionId = session.configuration.identifier {
//                // Saving the download request so that the same can be retrieved when the download comes back through the delegates of the DownloadManager.
//                DownloadManager.DownloadPersistanceModel(url: openRapDownloadUrl, contentId: contentId, sessionId: sessionId, taskId: task.taskIdentifier, fileType: "", needsUnzipping: true).save()
//            }
//            task.resume()
        } else {
            if !NotificationManager.isEventsListened {
                NotificationCenter.default.addObserver(HomeViewController.self, selector: #selector(HomeViewController.receiveNotification(obj: )), name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil)
                NotificationManager.isEventsListened = true
            }
            if json == nil {
                // If the json is nil, then the data is got for the first time saying that the user has initiated this download and that the data is not part of any hierarchy data already got.
                APIServices.getHierarchy(contentId: contentId, finished: { (hierarchyJson) in
                    
                    print(hierarchyJson)
                    if JSON.null == hierarchyJson {
                        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "hideLoader"])
                        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "showToast/\(AppConstants.fetchDetailsError)/force"])
                    } else {
                        // Taking the needed fields from the response
                        let jsonNeeded = JSON(hierarchyJson)
                        let canDownload = DownloadService.checkIfAllArtifactsAreDownloadable(content: jsonNeeded)
                        
                        print("Download accessible: ", canDownload)
                        
                        if canDownload {
                            DownloadService.initiateAndStartDownload(contentJson: jsonNeeded, userInitiated: true)
                        } else {
                            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "showToast/\(AppConstants.containsExternal)/force"])
                        }
                        // After the method call completes, we will have all the data needed for downloading, hence remove the loader
                        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "hideLoader"])
                    }
                })
            } else {
                DownloadService.initiateAndStartDownload(contentJson: json!)
            }
        }
        
    }
    
    private static func initiateAndStartDownload(contentJson: JSON, userInitiated: Bool = false) {
        // Taking the data from the JSON to create a model out of it.
        let contentId: String? = contentJson["identifier"].stringValue
        var thumbnailUrl: String? = contentJson["appIcon"].stringValue
        var artifactUrl: String? = contentJson["artifactUrl"].stringValue
        let contentType: String? = contentJson["contentType"].stringValue
        let mimeType: String? = contentJson["mimeType"].stringValue
        var json = contentJson
        let downloadUrl: String? = contentJson["downloadUrl"].stringValue
        let displayContentType: String? = contentJson["displayContentType"].stringValue.lowercased()
        // let downloadLink: String? = contentJson["mimeType"].stringValue
        AppConstants.downloadThumbnailUrl = thumbnailUrl!
        var audiovideo = false
        var properDownloadurl = false
        let audioVideoMimeType = ["audio/m4a","audio/mpeg","video/mp4","application/x-mpegURL","video/interactive","video/x-youtube"]
        
       if(audioVideoMimeType.contains(contentJson["mimeType"].stringValue)){
            audiovideo = true
        }
        
        
        
        if (mimeType == "application/x-mpegURL"){
            json["mimeType"] = "video/mp4"
            
        }
        if(json["mimeType"] == "video/mp4"){
            audiovideo = true
        }
        
        
//        if(displayContentType == "podcast"){
//            audiovideo = true
//        }
        let resourceJSONStr = json.rawString()
        
        
        if(!audiovideo){
            if((downloadUrl!.contains("%252F")) || (downloadUrl!.contains("%2F"))){
                properDownloadurl = true
            }
        }
        
        if(!audiovideo){
            if(!properDownloadurl){
                NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "showToast/\(AppConstants.fetchDetailsError)/force"])
                 NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "hideLoader"])
                return
            }
        }
        
        
        if(mimeType == "application/pdf"){
            var fileName = URL(string:downloadUrl!)?.lastPathComponent ?? ""
             if(fileName.contains("%252F")){
                 let furtherFileName = fileName.components(separatedBy: "%252F")
                 fileName = furtherFileName.last!
                 if(furtherFileName.last!.contains("?type=")){
                     
                     let splitfileName = furtherFileName.last!.components(separatedBy: "?type=")
                     fileName = splitfileName.first!
                 }
                 
             }
            if(fileName.contains(".zip")){
                 NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "showToast/\(AppConstants.fetchDetailsError)/force"])
                 NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "hideLoader"])
                return
                
            }
        }
        
        
        if isUserInitiatedItem {
            Telemetry().AddDownloadTelemetry(rid: contentId!, mimeType: mimeType!, contentType: contentType!, status: "initiated", mode : AppConstants.downloadType.DEFAULT.name())
            Telemetry().uploadTelemetryData()
            isUserInitiatedItem = false
        }
        
        if thumbnailUrl!.hasPrefix("http") {
            thumbnailUrl = ""
        }
        if artifactUrl!.hasPrefix("http") {
            artifactUrl = ""
        }
        
        // Creating the model. This model will be passed to various different methods ahead
        let contentMetaObj = ContentModel(identifier: contentId!, thumbnailURL: thumbnailUrl!, artifactURL: artifactUrl!, contentType: contentType!, resourceJSON: resourceJSONStr!, userInitiated: userInitiated, requestedDate: Date(), expiryDate: DateUtil.addDaysToDate(inputDate: Date(), noOfDays: AppConstants.contentExpiryInDays), status: "INITIATED", percentComplete: 0)
        
        var includeParent = true
        // If the resource or the module already exists, then do not do anything. Else save the entry into the database
        let results = CoreDataService.getCoreDataRow(identifier: contentId!, uuid: UserDetails.UID)
        if results != nil {
            let nonNulResults = results as! [NSManagedObject]
            
            if nonNulResults.count<=1 {
                
                var shouldDownload = false
                
                if nonNulResults.count==0 {
                    shouldDownload = true
                } else if nonNulResults.count==1 {
                    
                    if contentType?.lowercased()=="collection" || contentType?.lowercased()=="course" {
                        // Checking the status of all children of the colletion of the course
                        var currentChildIds:[String] = []
                        DownloadedDataService.getAllChildrenIds(contentJson: contentJson, childrenList: &currentChildIds)
                        
                        let currentChildResults = CoreDataService.getDownloadedResource(withIdentifiers: currentChildIds)
                        let results = currentChildResults as! [NSManagedObject]
                        
                        var allChildIds:[String] = []
                        DownloadedDataService.getAllChildResourceIds(contentJson: contentJson, childrenList: &allChildIds)
                        
                        // If any of the children are deleted, make the should download as true, else if it is equal, check if all the resources of the collection or course are complete or not
                        if results.count<allChildIds.count { // Few resources are deleted
                            shouldDownload = true
                            includeParent = false
                        } else if results.count == allChildIds.count {
                            var completePercent = 0.0;
                            
                            for child in results {
                                completePercent = completePercent + (child.value(forKey: "percent_complete") as! Double)
                            }
                            completePercent = completePercent/Double(results.count)
                            
                            if completePercent >= 100.0 {
                                // If initiated by user, making it true
                                // Changing the user initiated value to true for this and updating the core data, so that the user can see this resource in the coure/module/resource list directly
                                contentMetaObj.thumbnailURL = (nonNulResults[0].value(forKey: "stringOne") as? String)!
                                contentMetaObj.artifactURL = nonNulResults[0].value(forKey: "stringTwo") as? String
                                //                                contentMetaObj.userInitiated = nonNulResults[0].value(forKey: "requestedByUser") as? Bool ?? true
                                contentMetaObj.userInitiated = true
                                contentMetaObj.expiryDate = DateUtil.addDaysToDate(inputDate: Date(), noOfDays: AppConstants.contentExpiryInDays)
                                contentMetaObj.status = "DOWNLOADED"
                                contentMetaObj.percentComplete = 100
                                
                                _ = CoreDataService.getAllUserPreferences(keyNames: [AppConstants.lastLoggedInKey]) as! [NSManagedObject]
                                //                                let emailId = userPreferences[0].value(forKey: "userEmail") as! String
                                
                                let isUpdated = CoreDataService.updateRowInCoreData(withIdentifier: contentId!, newRow: CoreDataService.createCoreDataFromContentModel(contentObj: contentMetaObj))
                                
                                if isUpdated {
                                    // Show a toast that this resource is already downloaded
                                    
                                    
                                    NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "showToast/\(contentType ?? "Resource") exists, added it to your list/force"])
                                } else {
                                    // Show a toast that this resource is already downloaded
                                    NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "showToast/\(contentType ?? "Resource") exists./force"])
                                }
                            }
                        }
                    }
                    else {
                        let status = (nonNulResults[0].value(forKeyPath: "status") as! String).lowercased()
                        
                        if status.lowercased()=="failed" || status.lowercased()=="deleted" {
                            shouldDownload = true
                        } else if status.lowercased()=="downloaded" {
                            // Changing the user initiated value to true for this and updating the core data, so that the user can see this resource in the course/module/resource list directly
                            contentMetaObj.thumbnailURL = (nonNulResults[0].value(forKey: "stringOne") as? String)!
                            contentMetaObj.artifactURL = nonNulResults[0].value(forKey: "stringTwo") as? String
                            //                            contentMetaObj.userInitiated = nonNulResults[0].value(forKey: "requestedByUser") as? Bool ?? true
                            contentMetaObj.userInitiated = true
                            contentMetaObj.expiryDate = DateUtil.addDaysToDate(inputDate: Date(), noOfDays: AppConstants.contentExpiryInDays)
                            contentMetaObj.status = "DOWNLOADED"
                            contentMetaObj.percentComplete = 100
                            
                            let isUpdated = CoreDataService.updateRowInCoreData(withIdentifier: contentId!, newRow: CoreDataService.createCoreDataFromContentModel(contentObj: contentMetaObj))
                            
                            if isUpdated {
                                // Show a toast that this resource is already downloaded
                                NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "showToast/\(contentType ?? "Resource") exists, added it to your list/force"])
                            } else {
                                // Show a toast that this resource is already downloaded
                                NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "showToast/downloading the resource/force"])
                            }
                        } else if (status == "initiated") {
                            let userPreferences = CoreDataService.getAllUserPreferences(keyNames: [AppConstants.lastLoggedInKey]) as! [NSManagedObject]
                            if let uuid = userPreferences[0].value(forKey: "userUuid") as? String {
                                let deleted = CoreDataService.deleteCoreDataWith(identifier: contentId!, uuid: uuid)
                                print("Deleted ? ", deleted)
                                shouldDownload = true
                            }
                        }
                    }
                }
                
                if shouldDownload {
                    if includeParent {
                        // Saving the data into the DB (Core data), that the download has been initialized.
                        let isSaved = CoreDataService.saveDataToCoreData(coreDataObj: CoreDataService.createCoreDataFromContentModel(contentObj: contentMetaObj))
                        
                        if isSaved {
                            // print("Download initialized data saved into the core data for: \(contentId ?? "EMPTY||ERROR")")
                            
                            let result = CoreDataService.getCoreDataRow(identifier: contentId!, uuid: UserDetails.UID)
                            
                            // Replacing the IP of the endpoints which will be sent back to the API to download. Change this later to accept a regex and change the URL of it.
                            let replacedThumbnailURL = contentMetaObj.thumbnailURL.replacingOccurrences(of: internalContentStoreIpUrl, with: baseURL)
                            let replacedArtifactUrl = contentMetaObj.artifactURL?.replacingOccurrences(of: internalContentStoreIpUrl, with: baseURL)
                            
                            // Sending this file to the download manager.
                            let resultsArr = result as! [NSManagedObject]
                            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "showToast/Download initiated"])
                            DownloadService.download(filesToDownload: [replacedThumbnailURL, replacedArtifactUrl!], contentId: contentMetaObj.identifier, coreDataRow: resultsArr[0])
                        }
                    }
                    
                    // Will iterate over the children and call the method recursively.
                    let childrenArray = contentJson["children"].array
                    for child in childrenArray! {
                        let childContentId: String? = child["identifier"].stringValue
                        if childContentId != nil && !(childContentId?.isEmpty)! {
                            self.downloadHierarically(contentId: childContentId!, DownloadType: AppConstants.downloadType.DEFAULT.name(), json: child)
                        }
                    }
                }
            }
        }
    }
    
    static func getDownloadSize(url: URL, completion: @escaping (Int64, Error?) -> Void) {
        let timeoutInterval = 5.0
        var request = URLRequest(url: url,
                                 cachePolicy: .reloadIgnoringLocalAndRemoteCacheData,
                                 timeoutInterval: timeoutInterval)
        request.httpMethod = "HEAD"
        URLSession.shared.dataTask(with: request) { (data, response, error) in
            let contentLength = response?.expectedContentLength ?? NSURLSessionTransferSizeUnknown
            completion(contentLength, error)
        }.resume()
    }
    
    private static func download(filesToDownload:[String], contentId: String, coreDataRow: NSManagedObject) {
        // Getting the required details from the data that is got from the saved core data
        let resourceJsonData = coreDataRow.value(forKeyPath: "json") as! Data
        let resourceJsonStr = CoreDataService.convertBinaryToString(input: resourceJsonData)
        let resourceJson = JsonUtil.convertJsonFromJsonString(inputJson: resourceJsonStr)
        let downloadUrl = URL(string:resourceJson!["downloadUrl"].stringValue.lowercased())
        AppConstants.downloadLexId = resourceJson!["identifier"].stringValue.lowercased()
        AppConstants.downloadArtifactUrl = resourceJson!["artifactUrl"].stringValue.lowercased()
        
        var replacedThumbnailURL = resourceJson!["appIcon"].stringValue.replacingOccurrences(of: internalContentStoreIpUrl, with: baseURL)
        var replacedArtifactUrl = ""
        replacedThumbnailURL = replacedThumbnailURL.replacingOccurrences(of: internalStaticDirStroreUrl, with: baseURL)
        if(replacedThumbnailURL.contains("")){
                 replacedThumbnailURL = replacedThumbnailURL.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!
             }
        
        // Variable to pass to Download manager to unzip the file to a location or not
        var needsUnzipping: Bool = false
        
        // Checking if the requested artifact is a webmodule and a resource, so that we can download that resource and ask the download manager to unzip it after it finished the download
        //        if resourceJson!["contentType"].stringValue.lowercased() == "resource" && (resourceJson!["mimeType"].stringValue.lowercased() == "application/web-module" || resourceJson!["mimeType"].stringValue.lowercased()=="application/quiz") {
        
        // For temporary until quiz works properly
        if resourceJson!["contentType"].stringValue.lowercased() == "resource" && (resourceJson!["mimeType"].stringValue.lowercased() == "application/web-module" || resourceJson!["mimeType"].stringValue.lowercased() == "application/quiz" ||
            resourceJson!["mimeType"].stringValue.lowercased() == "application/html" ) {
            needsUnzipping = true
            
            // If the download artifact needs unzipping, then the artifact url must not be downloaded, but the download url should be used instead
            replacedArtifactUrl = resourceJson!["downloadUrl"].stringValue.replacingOccurrences(of: internalContentStoreIpUrl, with: baseURL)
            replacedArtifactUrl = replacedArtifactUrl.replacingOccurrences(of: internalStaticDirStroreUrl, with: baseURL)
        } else {
            replacedArtifactUrl = resourceJson!["downloadUrl"].stringValue.replacingOccurrences(of: internalContentStoreIpUrl, with: baseURL)
            replacedArtifactUrl = replacedArtifactUrl.replacingOccurrences(of: internalStaticDirStroreUrl, with: baseURL)
        }
        
        if(replacedArtifactUrl.contains("private-content-service")){
            replacedArtifactUrl = replacedArtifactUrl.replacingOccurrences(of: "private-content-service", with: "/apis/proxies/v8")
        }
        if(!replacedArtifactUrl.contains(baseURL)){
            replacedArtifactUrl = baseURL + replacedArtifactUrl
        }
        
        
        replacedArtifactUrl = replacedArtifactUrl.replacingOccurrences(of: " ", with: "%20")
        
        var updatedFilesToDownload:[String] = []
        
        if replacedThumbnailURL.hasPrefix("http://") || replacedThumbnailURL.hasPrefix("https://") {
            updatedFilesToDownload.append(replacedThumbnailURL.trimmingCharacters(in: .whitespacesAndNewlines))
        }
        // Adding the artifact url for any a resource. Rest all do not have any artifact url
        if resourceJson!["contentType"].stringValue.lowercased() == "resource" {
            if replacedArtifactUrl.hasPrefix("http://") || replacedArtifactUrl.hasPrefix("https://") {
                updatedFilesToDownload.append(replacedArtifactUrl.trimmingCharacters(in: .whitespacesAndNewlines))
            }
        }
        
        //        print("Files to download = \(filesToDownload.count)")
        //changing the download urls to accomodate the streaming request. Download should not be streamed, hence sending the flag
        var noStreamURLs : [String] = []
        
        for url in updatedFilesToDownload{
            if url.contains("?type=") && (url.contains(".pdf")){
                noStreamURLs.append(url.replacingOccurrences(of: "?type=", with: "?ns=true&type="))
            }else{
                if(url.contains("?type=main")){
                    noStreamURLs.append(url.replacingOccurrences(of: "?type=main", with: ""))
                }
                else{
                    noStreamURLs.append(url)
                }
                
            }
        }
        
        print("URLs", noStreamURLs)
        
//        for (_, url) in noStreamURLs.enumerated() {
//            getDownloadSize(url: URL(string: url)!) {(size, error) in
//                print("Size", size, "error", error)
//            }
//        }
        
        for (_,url ) in noStreamURLs.enumerated() {
            //Remove Spaces between add escapestring URL
         print(url)
            var downloadURL : URL
            if(url.contains(" ")){
                downloadURL = URL(string: url.replacingOccurrences(of: " ", with: "%20"))!
                // let url = url.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!
            }
            else{
                downloadURL = URL(string: url)!
            }
                       
            if ((downloadURL) != nil) {
                
                let documentsDirectoryURL =  FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
                if !FileManager.default.fileExists(atPath: documentsDirectoryURL.appendingPathComponent(contentId).path) {
                    
                    do {
                        try FileManager.default.createDirectory(atPath: documentsDirectoryURL.appendingPathComponent(contentId).path, withIntermediateDirectories: false, attributes: nil)
                    } catch let error as NSError {
                        print(error.localizedDescription);
                    }
                } else {
                }
                
                // Place where the thumbail or the artifact will be stored
                let destinationUrl = documentsDirectoryURL.appendingPathComponent(contentId + "/\(downloadURL.lastPathComponent)")
                
                // to check if it exists before downloading it
                var coreDataOfflinePathExists = false
                if url.hasSuffix("assets") || url.hasSuffix("ecar_files") {
                    // Check if artifact url is existing in Core data entry
                    if (coreDataRow.value(forKeyPath: "stringTwo") as! String).hasPrefix("/") {
                        coreDataOfflinePathExists = true
                    }
                } else if url.hasSuffix("artifacts") {
                    // Check if the thumbnail exists in the Core data entry
                    if (coreDataRow.value(forKeyPath: "stringOne") as! String).hasPrefix("/") {
                        coreDataOfflinePathExists = true
                    }
                }
                if FileManager.default.fileExists(atPath: destinationUrl.path) && coreDataOfflinePathExists {
                    print("The file already exists at path. Will not remove it for now. Will just update the meta that it has been asked to be downloaded again")
                }
                else {
                    //                        print("File does not exist in the path. Will download and save the file now")
                    
                    // Alternatively downloading in backgroud using the DownloadManager by nagasai_govula
                    let session = DownloadManager.shared.activate()
                    let task = session.downloadTask(with: downloadURL)
                    if let sessionId = session.configuration.identifier {
                        // Saving the download request so that the same can be retrieved when the download comes back through the delegates of the DownloadManager.
                        DownloadManager.DownloadPersistanceModel(url: downloadURL.path, contentId: contentId, sessionId: sessionId, taskId: task.taskIdentifier, fileType: "", needsUnzipping: needsUnzipping).save()
                    }
                    task.resume()
                }
            } else {
                print("Error while preparing the URL. Increase the download attempts for now.")
            }
        }
    }
    
    private static var firstLoad: Bool = true
    private static func clearDownloadedContent() {
        do {
            let documentsDirectoryURL =  FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
            
            // var tempFolderPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!.path
            let filePaths = try FileManager.default.contentsOfDirectory(atPath: documentsDirectoryURL.path)
            for filePath in filePaths {
                try FileManager.default.removeItem(atPath: documentsDirectoryURL.appendingPathComponent(filePath).path)
                //                print("Deleted the item: \(documentsDirectoryURL.appendingPathComponent(filePath).path)")
            }
            //            print("Deleted all document directories.")
        } catch _ {
            //            print(error.localizedDescription)
        }
    }
    
    @objc static func receiveNotification(obj: NSNotification) {
        print("Received Notification")
    }
}

