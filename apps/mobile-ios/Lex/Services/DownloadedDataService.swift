//  DownloadedDataService.swift
//  Lex
//  Created by Abhishek Gouvala / Shubham Singh on 3/23/18.
//  Copyright © 2018 Infosys. All rights reserved.

import Foundation
import SwiftyJSON
import CoreData

class DownloadedDataService: NSObject {
    private static let thumbnailPrefix:String = "http://" + Singleton.appConfigConstants.internalIp + ":5903"
    private static let expiryDays = AppConstants.contentExpiryInDays
    private static let documentsDirectoryURL =  FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
    
    enum ContentTypes: String {
        case Course, Collection, Resource
        
        func name() -> String {
            return self.rawValue
        }
    }
    
    static func getDownloads(type: ContentTypes) -> [[String:JSON]] {
        switch type {
        case .Resource:
            return getAllDownloadedResources()
        case .Collection:
            return getAllDownloadedThingsWithChildren(type: .Collection)
        case .Course:
            return getAllDownloadedThingsWithChildren(type: .Course)
        }
    }
    
    static func getTocFor(identifier: String, showOnlyDownloaded: Bool = false) -> JSON {
        return getCourseToc(contentId: identifier, showOnlyDownloaded: showOnlyDownloaded)
    }
    
    static func deleteWith(identifier: String) -> Bool {
        // Deleting all the children first
        let allResources = getDownloadedResource(withId: identifier)
        
        
        if allResources.count>0 {
            let contentObj = allResources[0]
            var children:[String] = []
            getAllChildrenIds(contentJson: contentObj["content"]!, childrenList: &children)
            
            for child in children {
                if !deleteContentAndDataWith(identifier: child) {
                    return false
                }
            }
            // Now deleting the content itself
            return deleteContentAndDataWith(identifier: identifier)
        }
        return false
    }
    
    static func hasParentDownloaded(contentId: String) -> Bool {
        let resources: [[String: JSON]] = getDownloadedResource(withId: contentId)
        
        var parentIds: [String] = []
        if resources.count>0{
            let resource = resources[0]
            let jsonResponse = resource["content"]
            
            if jsonResponse != nil && !(jsonResponse?.isEmpty)! {
                let parents = jsonResponse!["collections"].arrayValue
                
                for parent in parents {
                    parentIds.append(parent["identifier"].stringValue)
                }
                
                if parentIds.count>0 {
                    let parentDataFromCoreData = CoreDataService.getCoreDataContent(withIdentifiers: parentIds) as! [NSManagedObject]
                    if parentDataFromCoreData.count>0{
                        return true
                    }
                }
            }
        }
        return false
    }
    
    static func deleteExpiredResourcesInBackground() {
        DispatchQueue.global(qos: .background).async {
            //print("This is run on the background queue")
            
            if let result = CoreDataService.getAllExpiredRows() {
                
                let nonNullResults: [NSManagedObject] = result as! [NSManagedObject]
                
                var deletableResources: [String] = []
                if nonNullResults.count>0 {
                    for data in nonNullResults {
                        if(!(data.value(forKeyPath: "content_id") as! String).isEmpty){
                             deletableResources.append(data.value(forKeyPath: "content_id") as! String)
                        }
                       
                        //print("Expiry: " + String(describing: data.value(forKeyPath: "expiry_date") as! Date) + " | Now: " + Date().description)
                    }
                }
                if(deletableResources.count>0){
                    for contentId in deletableResources {
                            let _ = DownloadedDataService.deleteWith(identifier: contentId)
                        }
                    }
                }
                
        }
    }
    
    static func getContentWith(identifier: String) -> JSON? {
        
        let dataArr = CoreDataService.getDownloadedResource(withIdentifier: identifier) as! [NSManagedObject]
        var nowJson = JSON.null
        
        if dataArr.count>0 {
            let data = dataArr[0]
            
            // Updating the json to the new json
            nowJson = JSON(data)
        }
        return nowJson
    }
    private static func deleteContentAndDataWith(identifier: String) -> Bool {
        // Deleted the core data for the identifier
        //        let isDeleted = CoreDataService.markCoreDataDeletedWith(identifier: identifier)
        let userPreferences = CoreDataService.getAllUserPreferences(keyNames: [AppConstants.lastLoggedInKey]) as! [NSManagedObject]
        let uuid = userPreferences[0].value(forKey: "userUuid") as! String
        let isDeleted = CoreDataService.deleteCoreDataWith(identifier: identifier, uuid: uuid)
        
        if isDeleted {
            // Delete the content directory
            do {
                let documentsDirectoryURL =  FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
                
                // var tempFolderPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!.path
                let filePaths = try FileManager.default.contentsOfDirectory(atPath: documentsDirectoryURL.path)
                for filePath in filePaths {
                    if (filePath.hasSuffix(identifier)) {
                        try FileManager.default.removeItem(atPath: documentsDirectoryURL.appendingPathComponent(filePath).path)
                        //print("Deleted the item: \(documentsDirectoryURL.appendingPathComponent(filePath).path)")
                        break
                    }
                }
                return true
            } catch _ {
                //print(error.localizedDescription)
            }
        }
        return false
    }
    
    private static var childrenOfParent: [String:[String:JSON]] = [:]
    
    private static func updateJsonToOffline(json: JSON, parentId: String) -> JSON {
        var localJson = JSON(json.rawString()!)
        let contentId = json["identifier"].stringValue
        
        let childOfParentEntry = childrenOfParent[parentId]
        
        let savedJson:JSON = childOfParentEntry![contentId]!
        
        let offlineThumbnailUrl = savedJson["offlineThumbnail"].stringValue
        let offlineArtifactUrl = savedJson["offlineArtifact"].stringValue
        
        if savedJson.null == nil {
            localJson["thumbnail"] = JSON(offlineThumbnailUrl)
            localJson["appIcon"] = JSON("\(thumbnailPrefix)\(offlineThumbnailUrl)")
            localJson["artifactUrl"] = JSON(offlineArtifactUrl)
            
            // If the file is a pdf file, then appending the file protocal to the path, so that pdf.js would work
            if localJson["artifactUrl"].stringValue.lowercased().hasSuffix("pdf") {
                localJson["artifactUrl"] = JSON("file:///" + localJson["artifactUrl"].stringValue)
            }
        }
        return localJson
    }
    
    private static func getCourseToc(contentId: String, showOnlyDownloaded: Bool) -> JSON {
        if childrenOfParent[contentId] == nil {
            // Dictionary to read the percentage progress for a course
            let resourceDict = showDownloaded(identifier: contentId)
            
            // Setting it in the childrenOfParent
            childrenOfParent[contentId] = resourceDict
        }
        
        // Saving the children of a course here
        let dataArr = CoreDataService.getDownloadedResource(withIdentifier: contentId) as! [NSManagedObject]
        
        var nowJson = JSON()
        
        if dataArr.count>0 {
            let data = dataArr[0]
            
            // Adding the data of the content
            // Getting and updating the content
            let stringJson = CoreDataService.convertBinaryToString(input: data.value(forKeyPath: "json") as! Data)
            var contentJson = JsonUtil.convertJsonFromJsonString(inputJson: stringJson)
            
            // Changing the thumbnail and the artifact url here
            var thumbnailUrl = data.value(forKeyPath: "stringOne") as! String
            if thumbnailUrl.count>0 {
                thumbnailUrl = documentsDirectoryURL.appendingPathComponent(thumbnailUrl).path
            }
            
            var artifactUrl = (data.value(forKeyPath: "stringTwo") as! String)
            if artifactUrl.count>0 {
                artifactUrl = documentsDirectoryURL.appendingPathComponent(artifactUrl).path
            }
            //            print("Artifact URL is: " + artifactUrl);
            
            contentJson!["thumbnail"] = JSON(thumbnailUrl)
            contentJson!["appIcon"] = JSON(thumbnailUrl)
            contentJson!["artifactUrl"] = JSON(artifactUrl)
            
            // If the file is a pdf file, then appending the file protocal to the path, so that pdf.js would work
            if contentJson!["artifactUrl"].stringValue.lowercased().hasSuffix("pdf") {
                contentJson!["artifactUrl"] = JSON("file:///" + contentJson!["artifactUrl"].stringValue)
            }
            
            // Updating the json to the new json
            nowJson = JSON(contentJson!)
            
            // Reading the data for the children and updating the json respectively
            let children = JSON(nowJson["children"])
            
            // Making the copy of the children, will save and modify the data in these children
            var childrenCopy = JSON(children).arrayValue
            
            for child in children.array! {
                // Updated the child
                var nowChild = getCourseToc(contentId: child["identifier"].stringValue, showOnlyDownloaded: showOnlyDownloaded)
                
                if nowChild.isEmpty {
                    nowChild = JSON(child)
                    nowChild["artifactUrl"] = ""
                    nowChild["appIcon"] = ""
                    nowChild["thumbnailUrl"] = ""
                }
                
                // Updated the parent of this child with the new child
                var index: Int = 0;
                for changedChild in children.array! {
                    // Iterating through the array and changing the value of the children to the one with updated artifact and thumbnail url
                    if (changedChild["identifier"].stringValue == nowChild["identifier"].stringValue) {
                        childrenCopy.remove(at: index)
                        childrenCopy.insert(nowChild, at: index)
                        break
                    }
                    index = index + 1
                }
            }
            // Checking if there are any children without any artifactUrl or which are not downloaded yet and remove them from the data that is being displayed
            if (showOnlyDownloaded) {
                var deletableIndices:[Int] = []
                var deleteIndex = 0
                for jsonChild in childrenCopy {
                    if jsonChild["contentType"].stringValue.lowercased()=="resource"
                        && (jsonChild["artifactUrl"].stringValue.starts(with: "http") || jsonChild["artifactUrl"].stringValue.isEmpty) {
                        deletableIndices.append(deleteIndex)
                    }
                    deleteIndex = deleteIndex + 1
                }
                // Removing the children which do not have any data
                childrenCopy = removeItemsFromJSONArray(indices: deletableIndices, jsonArr: childrenCopy)
            }
            
            // Updating the json with the new children
            nowJson["children"] = JSON(childrenCopy)
        }
        
        return JSON(nowJson)
    }
    
    private static func removeItemsFromJSONArray(indices: [Int], jsonArr: [JSON]) -> [JSON] {
        var returnArr:[JSON] = []
        
        var index = 0
        for jsonObj in jsonArr {
            if !indices.contains(index) {
                returnArr.append(jsonObj)
            }
            index = index + 1
        }
        return returnArr
    }
    
    private static func getAllDownloadedThingsWithChildren(type: ContentTypes) -> [[String:JSON]]{
        // Creating the return data
        var returnResultsArr = [[String:JSON]]();
        
        var name = ContentTypes.Course.name()
        switch type {
        case .Collection:
            name = type.name()
        case .Course:
            name = type.name()
        default:
            break
        }
        autoreleasepool {
            let results = CoreDataService.getNonLeafLevelData(contentType: name, includeUserInitiated: true)
            let userPreferences = CoreDataService.getAllUserPreferences(keyNames: [AppConstants.lastLoggedInKey]) as! [NSManagedObject]
            let emailId = userPreferences[0].value(forKey: "userUuid") as! String
            
            for data in results as! [NSManagedObject] {
                if data.value(forKey: "userUuid") as! String == emailId {
                    var returnEntry = [String: JSON]()
                    
                    print(data)
                    
                    //print(data.value(forKeyPath: "content_id") as! String)
                    
                    // Dictionary to read the percentage progress for a course
                    let resourceDict = showDownloaded(identifier: data.value(forKeyPath: "content_id") as! String)
                    
                    
                    if resourceDict.count == 0 {
                        // This means that all the resources in the collection or course have been deleted.
                        // We will delete the parent as well now
                        _ = DownloadedDataService.deleteWith(identifier: data.value(forKeyPath: "content_id") as! String)
                        continue
                    }
                    
                    var allResourcesIds:[String] = []
                    getAllChildResourceIds(contentJson: JSON(data.value(forKeyPath: "json") ?? JSON()) , childrenList: &allResourcesIds)
                    
                    // Varible to find if any resource of the collection is partially deleted.
                    var percentageProgressOfCourse: Double = 0.0
                    var count = 0;
                    /*
                     for (index, result) in resourceDict {
                     count = count + 1
                     percentageProgressOfCourse = percentageProgressOfCourse + result["progress"].doubleValue
                     
                     // Debugging info
                     if result["progress"].doubleValue == 0.0 {
                     print("Download not finished yet for: \(String(describing: resourceDict[index]))")
                     }
                     }
                     */
                    for (_, result) in resourceDict {
                        count = count + 1
                        percentageProgressOfCourse = percentageProgressOfCourse + result["progress"].doubleValue
                    }
                    
                    percentageProgressOfCourse = percentageProgressOfCourse/Double(allResourcesIds.count)
                    
                    // Creating the meta
                    var metaJson = JSON()
                    
                    let downloadFinishedOn = DateUtil.getUnixTimeFromDate(input: (data.value(forKeyPath: "modified_date") as! Date))
                    let downloadInitOn = DateUtil.getUnixTimeFromDate(input: data.value(forKeyPath: "requested_date") as! Date)
                    let expiresOn = DateUtil.getUnixTimeFromDate(input: DateUtil.addDaysToDate(inputDate: (data.value(forKeyPath: "modified_date") as! Date), noOfDays: expiryDays))
                    let progress = percentageProgressOfCourse
                    let status = data.value(forKeyPath: "status") as! String
                    
                    metaJson["downloadFinishedOn"] = JSON(downloadFinishedOn)
                    metaJson["downloadInitOn"] = JSON(downloadInitOn)
                    metaJson["expires"] = JSON(expiresOn)
                    metaJson["progress"] = JSON(progress)
                    metaJson["status"] = JSON(status)
                    
                    var partialDownloadStatus = status
                    if resourceDict.count == 0 && allResourcesIds.count>0 {
                        partialDownloadStatus = "ALL RESOURCES DELETED BY USER"
                    }
                    if resourceDict.count>0 && allResourcesIds.count>resourceDict.count {
                        partialDownloadStatus = "DOWNLOAD INITIATED"
                    }
                    
                    metaJson["status"] = percentageProgressOfCourse >= Double(100) ? JSON("DOWNLOADED") : status.count>0 ?JSON(partialDownloadStatus) : JSON(status)
                    
                    // Adding the content to return entry
                    returnEntry["meta"] = metaJson
                    
                    // Adding the data of the content
                    // Getting and updating the content
                    let stringJson = CoreDataService.convertBinaryToString(input: data.value(forKeyPath: "json") as! Data)
                    var dataToConvert = JsonUtil.convertJsonFromJsonString(inputJson: stringJson)
                    
                    // Changing the thumbnail and the artifact url here
                    let thumbnailUrl = documentsDirectoryURL.appendingPathComponent((data.value(forKeyPath: "stringOne") as! String)).path
                    let artifactUrl = documentsDirectoryURL.appendingPathComponent((data.value(forKeyPath: "stringTwo") as! String)).path
                    
                    dataToConvert!["thumbnail"] = JSON(thumbnailUrl)
                    dataToConvert!["appIcon"] = JSON(thumbnailUrl)
                    dataToConvert!["artifactUrl"] = JSON(artifactUrl)
                    
                    // If the file is a pdf file, then appending the file protocal to the path, so that pdf.js would work
                    if dataToConvert!["artifactUrl"].stringValue.lowercased().hasSuffix("pdf") {
                        dataToConvert!["artifactUrl"] = JSON("file:///" + dataToConvert!["artifactUrl"].stringValue)
                    }
                    
                    // Adding the content to return entry
                    returnEntry["content"] = dataToConvert
                    
                    // Adding to the return array
                    returnResultsArr.append(returnEntry)
                }
            }
        }
        return returnResultsArr
    }
    
    private static func getAllDownloadedResources() -> [[String:JSON]] {
        let results = CoreDataService.getDownloadedResources(includeUserInitiated: true)
        let userPreferences = CoreDataService.getAllUserPreferences(keyNames: [AppConstants.lastLoggedInKey]) as! [NSManagedObject]
        let emailId = userPreferences[0].value(forKey: "userUuid") as! String
        
        var returnResultsArr = [[String:JSON]]();
        for data in results as! [NSManagedObject] {
            if data.value(forKey: "userUuid") as! String == emailId {
                var returnEntry = [String: JSON]()
                // Getting and updating the content
                let stringJson = CoreDataService.convertBinaryToString(input: data.value(forKeyPath: "json") as! Data)
                var dataToConvert = JsonUtil.convertJsonFromJsonString(inputJson: stringJson)
                
                
                _ = showDownloaded(identifier: data.value(forKeyPath: "content_id") as! String)
                
                // Changing the thumbnail and the artifact url here
                //            print("String one url: " + (data.value(forKeyPath: "stringOne") as! String))
                
                print(documentsDirectoryURL.appendingPathComponent((data.value(forKeyPath: "stringOne") as! String)))
                
                let thumbnailUrl = documentsDirectoryURL.appendingPathComponent((data.value(forKeyPath: "stringOne") as! String)).path
                let artifactUrl = documentsDirectoryURL.appendingPathComponent((data.value(forKeyPath: "stringOne") as! String)).path
                
                dataToConvert!["thumbnail"] = JSON(thumbnailUrl)
                dataToConvert!["appIcon"] = JSON(thumbnailUrl)
                dataToConvert!["artifactUrl"] = JSON(artifactUrl)
                
                // If the file is a pdf file, then appending the file protocal to the path, so that pdf.js would work
                if dataToConvert!["artifactUrl"].stringValue.lowercased().hasSuffix("pdf") {
                    dataToConvert!["artifactUrl"] = JSON("file:///" + dataToConvert!["artifactUrl"].stringValue)
                }
                
                // Adding the content to return
                returnEntry["content"] = dataToConvert
                
                // Creating the meta
                var metaJson = JSON()
                
                let downloadFinishedOn = DateUtil.getUnixTimeFromDate(input: (data.value(forKeyPath: "modified_date") as! Date))
                let downloadInitOn = DateUtil.getUnixTimeFromDate(input: (data.value(forKeyPath: "requested_date") as! Date))
                let expiresOn = DateUtil.getUnixTimeFromDate(input: DateUtil.addDaysToDate(inputDate: (data.value(forKeyPath: "modified_date") as! Date), noOfDays: expiryDays))
                let progress = data.value(forKeyPath: "percent_complete") as! Int
                let status = data.value(forKeyPath: "status") as! String
                
                metaJson["downloadFinishedOn"] = JSON(downloadFinishedOn)
                metaJson["downloadInitOn"] = JSON(downloadInitOn)
                metaJson["expires"] = JSON(expiresOn)
                metaJson["progress"] = JSON(progress)
                metaJson["status"] = JSON(status)
                
                // Adding the content to return entry
                returnEntry["meta"] = metaJson
                
                // Adding the support json
                returnEntry["supportData"] = JSON()
                
                if dataToConvert!["mimeType"].stringValue.lowercased() == "application/web-module"
                    ||  dataToConvert!["mimeType"].stringValue.lowercased() == "application/quiz"{
                    let jsonPathToRead = dataToConvert!["artifactUrl"].stringValue
                    
                    do {
                        let manifestJsonData = try Data(contentsOf: URL(fileURLWithPath: jsonPathToRead), options: .alwaysMapped)
                        let manifestJsonObj = try JSON(data: manifestJsonData)
                        returnEntry["supportData"] = manifestJsonObj
                    } catch _ {
                        
                    }
                }
                
                // Adding the TOC to the return array
                returnEntry["toc"] = dataToConvert
                // Adding to the return array
                returnResultsArr.append(returnEntry)
            }
        }
        return returnResultsArr
    }
    
    static func showDownloaded(identifier: String) -> [String:JSON] {
        // Printing the children data here
        // Change this to read data from index 0 only if the array exists
        let contentObjArr = getDownloadedResource(withId: identifier)
        
        var resourceDic: [String: JSON] = [:]
        if contentObjArr.count>0 {
            let contentObj = contentObjArr.first
            var finalList:[String] = []
            getAllChildrenIds(contentJson: contentObj!["content"]!, childrenList: &finalList)
            
            finalList = Array(Set(finalList))
            let downloadedResources = CoreDataService.getDownloadedResource(withIdentifiers: finalList)
            
            if downloadedResources != nil {
                for downloadedResource in downloadedResources as! [NSManagedObject] {
                    
                    let downloadFinishedOn = DateUtil.getUnixTimeFromDate(input: (downloadedResource.value(forKeyPath: "modified_date") as! Date))
                    let downloadInitOn = DateUtil.getUnixTimeFromDate(input: (downloadedResource.value(forKeyPath: "requested_date") as! Date))
                    let expiresOn = DateUtil.getUnixTimeFromDate(input: DateUtil.addDaysToDate(inputDate: (downloadedResource.value(forKeyPath: "modified_date") as! Date), noOfDays: expiryDays))
                    let progress = downloadedResource.value(forKeyPath: "percent_complete") as! Int
                    let status = downloadedResource.value(forKeyPath: "status") as! String
                    
                    let contentId = downloadedResource.value(forKeyPath: "content_id") as! String
                    let thumbnailUrl = documentsDirectoryURL.appendingPathComponent((downloadedResource.value(forKeyPath: "stringOne") as! String)).path
                    var artifactUrl = documentsDirectoryURL.appendingPathComponent((downloadedResource.value(forKeyPath: "stringTwo") as! String)).path
                    
                    // If the file is a pdf file, then appending the file protocal to the path, so that pdf.js would work
                    if artifactUrl.hasSuffix("pdf") {
                        artifactUrl = "file:///" + artifactUrl
                    }
                    
                    var metaJson = JSON()
                    metaJson["downloadFinishedOn"] = JSON(downloadFinishedOn)
                    metaJson["downloadInitOn"] = JSON(downloadInitOn)
                    metaJson["expires"] = JSON(expiresOn)
                    metaJson["progress"] = JSON(progress)
                    metaJson["status"] = JSON(status)
                    metaJson["offlineThumbnail"] = JSON(thumbnailUrl)
                    metaJson["offlineArtifact"] = JSON(artifactUrl)
                    
                    resourceDic[contentId] = metaJson
                }
            }
        }
        return resourceDic
    }
    
    public static func getDownloadedResource(withId: String) -> [[String:JSON]] {
        let results = CoreDataService.getDownloadedResource(withIdentifier: withId)
        
        var returnResultsArr = [[String:JSON]]();
        for data in results as! [NSManagedObject] {
            var returnEntry = [String: JSON]()
            
            // Getting and updating the content
            let stringJson = CoreDataService.convertBinaryToString(input: data.value(forKeyPath: "json") as! Data)
            var dataToConvert = JsonUtil.convertJsonFromJsonString(inputJson: stringJson)
            
            if dataToConvert != nil {
                // Changing the thumbnail and the artifact url here
                let thumbnail = documentsDirectoryURL.appendingPathComponent((data.value(forKeyPath: "stringOne") as! String)).path
                let artifactUrl = documentsDirectoryURL.appendingPathComponent((data.value(forKeyPath: "stringTwo") as! String)).path
                dataToConvert!["thumbnail"] = JSON(thumbnail)
                dataToConvert!["appIcon"] = JSON(thumbnail)
                dataToConvert!["artifactUrl"] = JSON(artifactUrl)
                
                // If the file is a pdf file, then appending the file protocal to the path, so that pdf.js would work
                if dataToConvert!["artifactUrl"].stringValue.lowercased().hasSuffix("pdf") {
                    dataToConvert!["artifactUrl"] = JSON("file:///" + dataToConvert!["artifactUrl"].stringValue)
                }
                
                // Adding the content to return
                returnEntry["content"] = dataToConvert
                
                // Creating the meta
                var metaJson = JSON()
                
                let downloadFinishedOn = DateUtil.getUnixTimeFromDate(input: (data.value(forKeyPath: "modified_date") as! Date))
                let downloadInitOn = DateUtil.getUnixTimeFromDate(input: (data.value(forKey: "requested_date") as! Date))
                let expiresOn = DateUtil.getUnixTimeFromDate(input: DateUtil.addDaysToDate(inputDate: (data.value(forKeyPath: "modified_date") as! Date), noOfDays: expiryDays))
                let progress = data.value(forKeyPath: "percent_complete") as! Int
                let status = data.value(forKeyPath: "status") as! String
                
                metaJson["downloadFinishedOn"] = JSON(downloadFinishedOn)
                metaJson["downloadInitOn"] = JSON(downloadInitOn)
                metaJson["expires"] = JSON(expiresOn)
                metaJson["progress"] = JSON(progress)
                metaJson["status"] = JSON(status)
                
                // Adding the content to return entry
                returnEntry["meta"] = metaJson
                
                // Adding to the return array
                returnResultsArr.append(returnEntry)
            }
        }
        
        return returnResultsArr
    }
    
    public static func getAllChildrenIds(contentJson: JSON, childrenList: inout [String]) {
        autoreleasepool {
            for child in contentJson["children"].array! {
                // Adding the children to the existing list
                childrenList.append(child["identifier"].stringValue)
                
                autoreleasepool {
                    for grandChild in child["children"].array! {
                        var newGrandChild = JSON()
                        newGrandChild["identifier"] = grandChild["identifier"]
                        newGrandChild["children"] = grandChild["children"]
                        
                        autoreleasepool {
                            getAllChildrenIds(contentJson: newGrandChild, childrenList: &childrenList)
                        }
                    }
                }
            }
        }
        // Adding the self to the array
        childrenList.append(contentJson["identifier"].stringValue)
    }
    
    public static func getAllChildResourceIds(contentJson: JSON, childrenList: inout [String]) {
        autoreleasepool {
            for child in contentJson["children"].array! {
                // Adding the children to the existing list
                if child["contentType"].stringValue.lowercased()=="resource" {
                    childrenList.append(child["identifier"].stringValue)
                }
                
                autoreleasepool {
                    for grandChild in child["children"].array! {
                        var newGrandChild = JSON()
                        newGrandChild["identifier"] = grandChild["identifier"]
                        newGrandChild["children"] = grandChild["children"]
                        
                        autoreleasepool {
                            getAllChildrenIds(contentJson: newGrandChild, childrenList: &childrenList)
                        }
                    }
                }
            }
        }
        // Adding the self to the array
        if contentJson["contentType"].stringValue.lowercased()=="resource" {
            childrenList.append(contentJson["identifier"].stringValue)
        }
    }
}

