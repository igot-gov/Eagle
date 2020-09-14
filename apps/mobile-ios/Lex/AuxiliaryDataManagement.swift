//  AuxiliaryDataManagement.swift
//  Lex
//  Created by Abhishek Gouvala / Shubham Singh / Ipshita Chatterjee on 3/14/18.
//  Copyright © 2018 Infosys. All rights reserved.
//  Manages Telemetry, Quiz Response and Course Progress Data

import Foundation
import SwiftyJSON
import Alamofire
import CoreData

class Telemetry {
    
    let fileUrl = Singleton.fileManager.urls(for: .documentDirectory, in: .userDomainMask).first?.appendingPathComponent("Data_Telemetry.json")
    var itemsCount = 0
    var count = 0
    
    let continueLearningFileUrl = Singleton.fileManager.urls(for: .documentDirectory, in: .userDomainMask).first?.appendingPathComponent("Data_continueLearning.json")
    
    let deviceInfo : [String:Any] = [
        "device": UIDevice.current.systemName,
        "osVersion": UIDevice.current.systemVersion,
        "screenResolution": "\(UIScreen.main.bounds.height) * \(UIScreen.main.bounds.width)",
        "deviceName": UIDevice.current.localizedModel,
        "UA": ""
    ]
    let user : [String:String] = [
        "email": UserDetails.email,
        "location": UserDetails.location,
        "unit": UserDetails.unit
    ]
    var toCheckChildrenData:[String] = []
    
    
    func AddPlayerTelemetry(json: [String:Any],cid: String, rid: String, mimeType: String) {
        let event = preparePlayerEvent(json: json,cid: cid, rid: rid,mimeType: mimeType)
        let validation = validateTelemetryEvents(event: event)
        print("player telemetry validation has errors ? :  \(validation)")
        var array = [[String:Any]]()
        if !validation {
            if Singleton.fileManager.fileExists(atPath: (fileUrl?.path)!) {
                array = retrieveFromJsonFile(fileUrl: fileUrl!)
                array.append(event)
                saveToJsonFile(inputEvents: array, fileUrl: fileUrl!)
            }
            else {
                array.append(event)
                saveToJsonFile(inputEvents: array, fileUrl: fileUrl!)
            }
        }
    }
    
    func AddPlayerTelemetryForOffline(json: [String:Any],cid: String, rid: String, mimeType: String) {
        let event = preparePlayerEventForOffline(cid: cid, mimeType: mimeType, resId: rid, json: json)
        let validation = validateTelemetryEvents(event: event)
        print("player telemetry validation has errors ? :  \(validation)")
        var array = [[String:Any]]()
        if !validation {
            if Singleton.fileManager.fileExists(atPath: (fileUrl?.path)!) {
                array = retrieveFromJsonFile(fileUrl: fileUrl!)
                array.append(event)
                saveToJsonFile(inputEvents: array, fileUrl: fileUrl!)
            }
            else {
                array.append(event)
                saveToJsonFile(inputEvents: array, fileUrl: fileUrl!)
            }
        }
    }
    
    func AddDownloadTelemetry(rid: String, mimeType: String, contentType: String, status: String, mode : String) {
        let event = prepareDownloadEvent(mimeType: mimeType,rid: rid,contentType: contentType, status: status,mode : mode)
        let validation = validateTelemetryEvents(event: event)
        print("download telemetry validation has errors ? : \(validation)")
        if !validation {
            if Singleton.fileManager.fileExists(atPath: (fileUrl?.path)!) {
                var array = retrieveFromJsonFile(fileUrl: fileUrl!)
                array.append(event)
                saveToJsonFile(inputEvents: array, fileUrl: fileUrl!)
            } else {
                var array = [[String:Any]]()
                array.append(event)
                saveToJsonFile(inputEvents: array, fileUrl: fileUrl!)
            }
        }
        
    }
    
    func AddImpressionTelemetry(envType: String,type: String,pageID: String,id: String,url:String) {
        let event = prepareImpressionEvent(envType: envType, type: type, pageID: pageID, id: id, url: url)
        let validation = validateTelemetryEvents(event: event)
        print("impression telemetry validation has errors ? : \(validation)")
        if !validation {
            if Singleton.fileManager.fileExists(atPath: (fileUrl?.path)!) {
                var array = retrieveFromJsonFile(fileUrl: fileUrl!)
                array.append(event)
                saveToJsonFile(inputEvents: array, fileUrl: fileUrl!)
            } else {
                var array = [[String:Any]]()
                array.append(event)
                saveToJsonFile(inputEvents: array, fileUrl: fileUrl!)
            }
        }
    }
    
    func AddImpressionTelemetryForOffline(dataDictionary: [String:Any]) {
        let event = prepareImpressionEventForOffline(dataDictionary: dataDictionary)
        let validation = validateTelemetryEvents(event: event)
        print("impression telemetry validation has errors ? : \(validation)")
        if !validation {
            if Singleton.fileManager.fileExists(atPath: (fileUrl?.path)!) {
                var array = retrieveFromJsonFile(fileUrl: fileUrl!)
                array.append(event)
                saveToJsonFile(inputEvents: array, fileUrl: fileUrl!)
            } else {
                var array = [[String:Any]]()
                array.append(event)
                saveToJsonFile(inputEvents: array, fileUrl: fileUrl!)
            }
        }
    }
    
    //adding to the file
    func AddContinueLearningTelemetry(contextId: String, rid: String) {
        let parameters: [String: Any] = [
            "contextPathId" : contextId,
            "data" : "",
            "percentComplete" : 0,
            "resourceId" : rid
        ]
        if Singleton.fileManager.fileExists(atPath: (continueLearningFileUrl?.path)!) {
            var array = retrieveFromJsonFile(fileUrl: continueLearningFileUrl!)
            array.append(parameters)
            saveToJsonFile(inputEvents: array, fileUrl: continueLearningFileUrl!)
        } else {
            var array = [[String:Any]]()
            array.append(parameters)
            saveToJsonFile(inputEvents: array, fileUrl: continueLearningFileUrl!)
        }
    }
    
    func addContinueLearningTelemetryForOffline(continueLearningDictionary: [String:Any]) {
        let parameters: [String: Any] = [
            "contextPathId" : continueLearningDictionary["contextPathId"]!,
            "data" : continueLearningDictionary["data"]!,
            "percentComplete" : continueLearningDictionary["percentComplete"]!,
            "resourceId" : continueLearningDictionary["resourceId"]!
        ]
        if Singleton.fileManager.fileExists(atPath: (continueLearningFileUrl?.path)!) {
            var array = retrieveFromJsonFile(fileUrl: continueLearningFileUrl!)
            array.append(parameters)
            saveToJsonFile(inputEvents: array, fileUrl: continueLearningFileUrl!)
        } else {
            var array = [[String:Any]]()
            array.append(parameters)
            saveToJsonFile(inputEvents: array, fileUrl: continueLearningFileUrl!)
        }
    }
    
    //static let toCheckContinueLear
    
    static let toCheckImpData = ["pdata","pdata.id","pdata.ver", "uid", "devicedata","devicedata.UA","devicedata.screenResolution","devicedata.osVersion","devicedata.deviceName","devicedata.device", "bodhiuser","bodhiuser.email","bodhiuser.location","bodhiuser.unit", "edata","edata.eks","eks.type","eks.env","eks.name","eks.pageid","eks.id","eks.url","sid", "etags","etags.app","etags.partner","etags.dims", "mid","context","context.sid", "channel", "ets", "eid", "ver", "did", "mode", "bodhidata","bodhidata.pageinf", "bodhidata.lastpageinf","lastpageinf.pageid","lastpageinf.pagesection","lastpageinf.url","lastpageinf.pagedata", "pageinf.env","pageinf.type","pageinf.pageid","pageinf.id","pageinf.name","pageinf.url","pageinf.pagedata"]
    
    static let toCheckDownloadData = ["event.eid","event.ets","event.ver","event.mid","event.channel","event.uid","event.did","event.sid","event.resid","event.restype","event.contentType","event.bodhiuser","bodhiuser.email","bodhiuser.location","bodhiuser.unit", "devicedata","devicedata.UA","devicedata.screenResolution","devicedata.osVersion","devicedata.deviceName","devicedata.device","event.status","event.mode"]
    
    static let toCheckActivityData = ["eid","ver","uid","sid","mid","ets","courseid","restype","resid","progress","duration","bodhiuser","bodhiuser.email","bodhiuser.location","bodhiuser.unit", "playerdata", "mode", "devicedata","devicedata.UA","devicedata.screenResolution","devicedata.osVersion","devicedata.deviceName","devicedata.device"]
    static var stringTypeElements:[String] = []
    static var errorFound = false
    
    func validateTelemetryEvents(event: [String:Any]) -> Bool {
        
        var toCheck:[String] = []
        if(event["eid"] == nil) {
            Telemetry.errorFound = true
        } else {
            let eid = event["eid"] as! String
            Telemetry.stringTypeElements = []
            for (key,value) in event {
                
                if (key == "playerdata" && eid == "CP_ACTIVITY") {
                    continue
                } else {
                    _ = checkForNestedDictionary(key: key,value: value, toCheckChildrenData: &toCheckChildrenData, eventId: eid)
                }
            }
            if eid == "CP_IMPRESSION" {
                toCheck = Telemetry.toCheckImpData
                
                let extraElements = ["cdata","id","type"]
                toCheck += extraElements
                Telemetry.stringTypeElements += ["id","type"]
            }
            else if eid == "MB_DOWNLOAD" {
                toCheck = Telemetry.toCheckDownloadData
            } else if eid == "CP_ACTIVITY" {
                toCheck = Telemetry.toCheckActivityData
            } else {
                Telemetry.errorFound = true
                print("Wrong event id found")
            }
            
            for item in toCheck {
                if item.contains(".") {
                    let appendItem = item.split(separator: ".")[1]
                    toCheck.append(String(appendItem))
                    let index = toCheck.firstIndex(of: item)
                    toCheck.remove(at: index!)
                    
                }
            }
        }
        
        //adding playerdata manually as this is not to be checked
        if event["eid"] as! String == "CP_ACTIVITY" {
            Telemetry.stringTypeElements.append("playerdata")
        }
        
        
        var notFound : [String] = []
        
        print("Count of keys in parsed Json structure for \(event["eid"] as! String) is -> \(Telemetry.stringTypeElements.count) and Count of manual array  is -> ", toCheck.count)
        
        if Telemetry.stringTypeElements.count > toCheck.count {
            
            for element in Telemetry.stringTypeElements {
                if !toCheck.contains(element) {
                    notFound.append(element)
                }
            }
        }
            
        else if toCheck.count > Telemetry.stringTypeElements.count {
            
            for element in toCheck {
                if !Telemetry.stringTypeElements.contains(element){
                    notFound.append(element)
                }
            }
        }
            
        else{
            print("No errors found in the JSON structure")
        }
        
        if (!notFound.isEmpty) {
            print("Error for \(event["eid"] as! String), these key(s) are missing ->  \(notFound)")
        }
        
        if(!Telemetry.stringTypeElements.sorted().containsSameElements(as: toCheck.sorted())) {
            Telemetry.errorFound = true
        }
        return Telemetry.errorFound
    }
    
    
    func checkForNestedDictionary(key: String,value : Any, toCheckChildrenData :inout [String], eventId: String)  -> Int {
        
        toCheckChildrenData.append(key)
        
        if let childList = value as? Dictionary<String, Any> {
            
            for (key,value) in childList {
                
                _ = checkForNestedDictionary(key: key,value: value, toCheckChildrenData: &toCheckChildrenData, eventId: eventId)
                
                
            }
        }
        
        Telemetry.stringTypeElements = toCheckChildrenData
        return 0;
    }
    
    func preparePlayerEvent(json: [String:Any],cid: String, rid: String, mimeType: String) -> [String:Any] {
        //print(json)
        let event : [String:Any] = [
            "eid": "CP_ACTIVITY",
            "ver": "2.0",
            "uid": UserDetails.UID,
            "sid": Singleton.sessionID,
            "mid": "lex_\(DateUtil.getUnixTimeFromDate(input: Date()))",
            "ets": DateUtil.getUnixTimeFromDate(input: Date()),
            "courseid": cid,
            "restype": mimeType,
            "resid": rid,
            "progress": "null",
            "duration": "0",
            "bodhiuser": user,
            "playerdata": json,
            "mode": "offline",
            "devicedata": deviceInfo
        ]
        return event
    }
    
    func preparePlayerEventForOffline(cid: String,mimeType: String,resId: String,json: [String:Any]) -> [String:Any] {
        let event : [String:Any] = [
            "eid": "CP_ACTIVITY",
            "ver": "1.0",
            "uid": UserDetails.UID,
            "sid": Singleton.sessionID,
            "mid": "lex_\(DateUtil.getUnixTimeFromDate(input: Date()))",
            "ets": DateUtil.getUnixTimeFromDate(input: Date()),
            "courseid": cid,
            "restype": mimeType,
            "resid": resId,
            "progress": "null",
            "duration": "0",
            "bodhiuser": user,
            "playerdata": json,
            "mode": "offline",
            "devicedata": deviceInfo
        ]
        return event
    }
    
    func prepareDownloadEvent(mimeType: String, rid: String, contentType: String,status: String, mode : String) -> [String:Any] {
        //print(json)
        let user : [String:String] = [
            "email": UserDetails.email,
            "location": UserDetails.location,
            "unit": UserDetails.unit
        ]
        let event : [String:Any] = [
            "eid": "MB_DOWNLOAD",
            "ets": DateUtil.getUnixTimeFromDate(input: Date()),
            "ver": "2.0",
            "mid": "lex_\(DateUtil.getUnixTimeFromDate(input: Date()))",
            "channel" : "b00bc992ef25f1a9a8d63291e20efc8d",
            "uid": UserDetails.UID,
            "did": "null",
            "sid": Singleton.sessionID,
            "resid": rid,
            "restype": mimeType,
            "contentType": contentType,
            "bodhiuser": user,
            "devicedata": deviceInfo,
            "status": status,
            "mode" : mode
        ]
        return event
    }
    func prepareImpressionEvent(envType: String,type: String,pageID: String,id: String,url:String) -> [String:Any]{
        let eventBody : [String:Any] = [
            "eid": "CP_IMPRESSION",
            "ets": DateUtil.getUnixTimeFromDate(input: Date()) ,
            "ver": "2.0",
            "mid": "lex_\(DateUtil.getUnixTimeFromDate(input: Date()))",
            "channel": "",
            "sid": Singleton.sessionID,
            "uid": UserDetails.UID,
            "did": "",
            "pdata": [
                "id" : "lex.portal",
                "ver": "2.0"
            ],
            "cdata": [
                [
                    "id": "",
                    "type": ""
                ]
            ],
            "etags": [
                "app": [],
                "partner": [],
                "dims": []
            ],
            "context": [
                "sid": Singleton.sessionID
            ],
            "edata": [
                "eks": [
                    "env": envType,
                    "type": type,
                    "pageid": pageID,
                    "id": id,
                    "name": "",
                    "url": url
                ]
            ],
            "bodhiuser": user,
            "mode":"offline",
            "devicedata":deviceInfo,
            "bodhidata": [
                "pageinf": [
                    "env": envType,
                    "type": type,
                    "pageid": pageID,
                    "id": id,
                    "name": "",
                    "url": url,
                    "pagedata": []
                ],
                "lastpageinf": [
                    "pageid": "",
                    "pagesection": "",
                    "url": "",
                    "pagedata": []
                ]
            ]
        ]
        return eventBody
    }
    
    func prepareImpressionEventForOffline(dataDictionary: [String:Any] ) -> [String:Any] {
        
        var id = ""
        if dataDictionary["id"] != nil {
            id = dataDictionary["id"] as! String
        }
        
        let eventBody : [String:Any] = [
            "eid": "CP_IMPRESSION",
            "ets": dataDictionary["ets"] as! Int ,
            "ver": dataDictionary["ver"] as! Double,
            "mid": "lex_\(DateUtil.getUnixTimeFromDate(input: Date()))",
            "channel": "",
            "sid": Singleton.sessionID,
            "uid": UserDetails.UID,
            "did": "",
            "pdata": [
                "id" : dataDictionary["pdataId"],
                "ver": dataDictionary["ver"]
            ],
            "cdata": [
                [
                    "id": "",
                    "type": ""
                ]
            ],
            "etags": [
                "app": [],
                "partner": [],
                "dims": []
            ],
            "context": [
                "sid": Singleton.sessionID
            ],
            "edata": [
                "eks": [
                    "env": dataDictionary["env"],
                    "type": dataDictionary["type"],
                    "pageid": dataDictionary["pageId"],
                    "id": id,
                    "name": "",
                    "url": dataDictionary["url"]
                ]
            ],
            "bodhiuser": user,
            "mode":"offline",
            "devicedata":deviceInfo,
            "bodhidata": [
                "pageinf": [
                    "env": dataDictionary["env"],
                    "type": dataDictionary["type"],
                    "pageid": dataDictionary["pageId"],
                    "id": id,
                    "name": "",
                    "url": dataDictionary["url"],
                    "pagedata": []
                ],
                "lastpageinf": [
                    "pageid": "",
                    "pagesection": "",
                    "url": "",
                    "pagedata": []
                ]
            ]
        ]
        return eventBody
    }
    
    func saveToJsonFile(inputEvents: [[String:Any]], fileUrl: URL) {
        //print(inputEvents)
        do {
            let data = try JSONSerialization.data(withJSONObject: inputEvents, options: [])
            try data.write(to: fileUrl, options: [])
        } catch {
            //print(error)
        }
    }
    func retrieveFromJsonFile(fileUrl: URL) -> [[String:Any]] {
        do {
            let data = try Data(contentsOf: fileUrl, options: [])
            guard let eventsArray = try JSONSerialization.jsonObject(with: data, options: []) as? [[String: Any]] else { return [[String:Any]]()}
            //print(eventsArray)
            return eventsArray
        } catch {
            //print(error)
            return [[String:Any]]()
        }
    }
    
    
    func modifyJSONForQuiz(){
        
    }
    
    func uploadContinueLearningData() {
        print("uploading continue")
        var filePath : URL = (Singleton.fileManager.urls(for: .documentDirectory, in: .userDomainMask).first)!
        if(TestUtil.checkingTelemetry){
            filePath = (Singleton.fileManager.urls(for: .documentDirectory, in: .userDomainMask).first?.appendingPathComponent(TestUtil.continueLearningPath))!
        }else{
            filePath = (continueLearningFileUrl)!
        }
        if Singleton.fileManager.fileExists(atPath: filePath.path){
            let fileData = retrieveFromJsonFile(fileUrl: filePath)
            itemsCount = fileData.count
            for parameter in fileData {
                let continueUrlString = baseURL + "clientApi/v2/user/history/continue"
                print(parameter)
                
                let headers: HTTPHeaders = [ "authorization": "Bearer \(Singleton.accessToken)","Content-Type": "application/json"]
                Alamofire.request(continueUrlString, method: .post, parameters: parameter,encoding: JSONEncoding.default, headers: headers).responseString
                    {response in
                        switch response.result
                        {
                        case .success(_):
                            do{
                                self.count += 1
                                if(TestUtil.checkingTelemetry){
                                    TestUtil.telemetryCalls["continueLearningApi"] = true
                                    print(TestUtil.telemetryCalls)
                                }
                                else {
                                    if(self.count == self.itemsCount){
                                        try Singleton.fileManager.removeItem(at: self.continueLearningFileUrl!)
                                        self.count = 0
                                        self.itemsCount = 0
                                    }
                                }
                                break
                            }
                            catch{
                                break
                            }
                        case .failure(_):
                            break
                        }
                }
            }
        }
    }
    
    func uploadTelemetryData()
    {
        //print("---\(String(describing: fileUrl))")
        var filePath : URL = (Singleton.fileManager.urls(for: .documentDirectory, in: .userDomainMask).first)!
        if(TestUtil.checkingTelemetry){
            filePath = (Singleton.fileManager.urls(for: .documentDirectory, in: .userDomainMask).first?.appendingPathComponent(TestUtil.telemetryEventPath))!
        }else{
            filePath = (fileUrl)!
        }
        if Singleton.fileManager.fileExists(atPath: filePath.path){
            var dataToChange = retrieveFromJsonFile(fileUrl: filePath)
            //            print("Check data to change ",dataToChange.count)
            for i in 0...dataToChange.count-1{
                var changer = JSON(dataToChange[i])
                if(changer["playerdata"]["plugin"].stringValue == "quiz" && changer["playerdata"]["type"].stringValue == "done") {
                    
                    changer["playerdata"]["data"]["force"] = true
                    let tempId = changer["resid"].stringValue
                    changer["playerdata"]["data"]["isIdeal"] = false
                    changer["playerdata"]["data"]["lostFocus"] = false
                    changer["playerdata"]["data"]["courseId"] = JSON.null
                    changer["playerdata"]["data"]["identifier"] = JSON(tempId)
                    changer["playerdata"]["data"]["mimeType"] = JSON("application/quiz")
                    changer["playerdata"]["data"]["details"] = JSON(Singleton.quizApiResult[tempId] as Any)
                    
                    let quizJSON = JSON(Singleton.quizApiResult[tempId] as Any)
                    let result = quizJSON["result"].stringValue
                    let passPercentage = quizJSON["passPercent"].stringValue
                    
                    var isCompleted = false
                    if result > passPercentage {
                        isCompleted = true
                    }
                    //                    let isCompleted = result > passPercentage ? true : false
                    changer["playerdata"]["data"]["isCompleted"] = JSON(isCompleted)
                    
                    
                    //                    print("Changed JSON" , changer)
                    dataToChange[i]["playerdata"] = changer["playerdata"]
                    
                }
                //                print(changer)
            }
            //            print(JSON(dataToChange))
            let paramsBody : [String:Any] = [
                "requesterId": "DUMMY-REQUESTER",
                "did": "mobile",
                "key": "13405d54-85b4-341b-da2f-eb6b9e546fff",
                "msgid":"DUMMY-UUID"]
            let body:[String:Any] = [
                "id": "lex.telemetry",
                "ver": "2.0",
                "ts": DateUtil.getTimeStampString(inputDate: Date()),
                "params": paramsBody,
                "events":retrieveFromJsonFile(fileUrl: filePath)]
            
            
            let urlString = baseURL + "clientApi/telemetry/events"
            let headers: HTTPHeaders = [ "authorization": "Bearer \(Singleton.accessToken)","Content-Type": "application/json"]
            Alamofire.request(urlString, method: .post, parameters: body,encoding: JSONEncoding.default, headers: headers).responseString
                {response in
                    switch response.result
                    {
                    case .success(let data):
                        let status = JSON(data.data(using: .utf8)!)["params"]["status"].stringValue
                        if status.lowercased() == "successful"{
                            print("DATA TELEMETRY was successful and response data was \(data)")
                            do{
                                if(TestUtil.checkingTelemetry){
                                    TestUtil.telemetryCalls["telemetryEventsApi"] = true
                                    print(TestUtil.telemetryCalls)
                                }else{
                                    try Singleton.fileManager.removeItem(at: self.fileUrl!)
                                }
                                break
                            }
                            catch{
                                Singleton.tempCounter = "0"
                                //Error
                            }
                        }
                        break
                    case .failure(_):
                        //print("Error")
                        break
                        //print(error)
                    }
            }
        }
    }
}
//Class handles course progress data
class CourseProgress {
    let fileUrlForCourseProgress = Singleton.fileManager.urls(for: .documentDirectory, in: .userDomainMask).first?.appendingPathComponent("Data_CourseProgress.json")
    var itemsCount = 0
    var count = 0
    
    func processData(dataToSave: String) {
        if Singleton.fileManager.fileExists(atPath: (fileUrlForCourseProgress?.path)!) {
            var array = retrieveFromJsonFile(fileURL: fileUrlForCourseProgress!)
            array.append(dataToSave)
            saveToJsonFile(inputArray: array)
        } else{
            var array = [String]()
            array.append(dataToSave)
            saveToJsonFile(inputArray: array)
        }
        if NetworkReachabilityManager()!.isReachable {
            uploadData()
        }
    }
    
    func saveToJsonFile(inputArray: [String]) {
        do {
            let data = try JSONSerialization.data(withJSONObject: inputArray, options: [])
            try data.write(to: fileUrlForCourseProgress!, options: [])
        } catch {
            //print(error)
        }
    }
    func retrieveFromJsonFile(fileURL : URL) -> [String] {
        do {
            let data = try Data(contentsOf: fileURL, options: [])
            guard let cpArray = try JSONSerialization.jsonObject(with: data, options: []) as? [String] else { return [String]()}
            return cpArray
        } catch {
            return [String]()
        }
    }
    
    
    func uploadData(){
        var filePath : URL = (Singleton.fileManager.urls(for: .documentDirectory, in: .userDomainMask).first)!
        if(TestUtil.checkingTelemetry){
            filePath = (Singleton.fileManager.urls(for: .documentDirectory, in: .userDomainMask).first?.appendingPathComponent(TestUtil.courseProgressPath))!
        }else{
            filePath = (fileUrlForCourseProgress)!
        }
        if Singleton.fileManager.fileExists(atPath: filePath.path){
            let fileData = retrieveFromJsonFile(fileURL : filePath)
            itemsCount = fileData.count
            for item in fileData {
                let urlString = baseURL + "clientApi/user/history/" + item
                let headers: HTTPHeaders = [ "authorization": "Bearer \(Singleton.accessToken)","Content-Type": "application/json"]
                Alamofire.request(urlString, method: .post, parameters: [String:Any](),encoding: JSONEncoding.default, headers: headers).responseString
                    {response in
                        switch response.result
                        {
                        case .success(_):
                            do{
                                self.count += 1
                                if(TestUtil.checkingTelemetry){
                                    TestUtil.telemetryCalls["userHistoryApi"] = true
                                    print(TestUtil.telemetryCalls)
                                }else{
                                    if self.itemsCount == self.count {
                                        try Singleton.fileManager.removeItem(at: self.fileUrlForCourseProgress!)
                                        self.itemsCount = 0
                                        self.count = 0
                                    }
                                }
                                break
                            }
                            catch{
                                break
                            }
                        case .failure(_):
                            break
                        }
                }
            }
        }
    }
}
//Class handles quiz response data
class QuizResponse {
    let fileUrlForQuizResponse = Singleton.fileManager.urls(for: .documentDirectory, in: .userDomainMask).first?.appendingPathComponent("Data_QuizResponse.json")
    var quizCount = 0
    var count = 0
    var contentId = ""
    var quizId = ""
    
    func processData(dataToSave: [String:Any]) {
        if Singleton.fileManager.fileExists(atPath: (fileUrlForQuizResponse?.path)!) {
            var array = retrieveFromJsonFile(fileURL: fileUrlForQuizResponse!)
            array.append(dataToSave)
            saveToJsonFile(inputArray: array)
        } else {
            var array = [[String:Any]]()
            array.append(dataToSave)
            saveToJsonFile(inputArray: array)
        }
        if NetworkReachabilityManager()!.isReachable {
            uploadData()
            
        }
    }
    
    func saveToJsonFile(inputArray: [[String:Any]]) {
        do {
            let data = try JSONSerialization.data(withJSONObject: inputArray, options: [])
            try data.write(to: fileUrlForQuizResponse!, options: [])
        } catch {
            //print(error)
        }
    }
    
    func retrieveFromJsonFile(fileURL : URL) -> [[String:Any]] {
        do {
            let data = try Data(contentsOf: fileURL, options: [])
            guard let qrArray = try JSONSerialization.jsonObject(with: data, options: []) as? [[String: Any]] else { return [[String:Any]]()}
            return qrArray
        } catch {
            return [[String:Any]]()
        }
    }
    func uploadData(){
        do{
            var filePath : URL = (Singleton.fileManager.urls(for: .documentDirectory, in: .userDomainMask).first)!
                if(TestUtil.checkingTelemetry){
                    filePath = (Singleton.fileManager.urls(for: .documentDirectory, in: .userDomainMask).first?.appendingPathComponent(TestUtil.quizSubmissionPath))!
                }else{
                    filePath = (fileUrlForQuizResponse)!
                }
                if Singleton.fileManager.fileExists(atPath: filePath.path){
                    let fileData = retrieveFromJsonFile(fileURL : filePath)
                    print(fileData)
                   
                    if(Singleton.wid.count==0) || (Singleton.accessToken.count==0){
                       let userPreferences = CoreDataService.getAllUserPreferences(keyNames: [AppConstants.lastLoggedInKey]) as! [NSManagedObject]
                        if(userPreferences.count>0){
                            Singleton.wid = userPreferences[0].value(forKey: "wid") as! String
                             Singleton.accessToken = userPreferences[0].value(forKey: "accessToken") as! String
                            
                        }
                    }
                     let accessTokenForAPI = "Bearer \(Singleton.accessToken)"
                    let headers: HTTPHeaders = [ "Authorization": accessTokenForAPI,"Accept": "application/json", "rootorg": "Infosys", "org": "Infosys Ltd", "locale": "en", "wid": Singleton.wid]
                    let urlString = baseURL + "apis/protected/v8/user/evaluate/assessment/submit/v2"
                    print("No. of items : ",fileData.count)
                    quizCount = fileData.count
                    for item in fileData {
                        print("quiz ID ->",quizId)
                        let itemparameters = item["request"]
                        print(itemparameters)
                        Alamofire.request(urlString, method: .post, parameters: itemparameters as? Parameters,encoding: JSONEncoding.default, headers: headers).responseString
                            {response in
                                switch response.result
                                {
                                case .success(let data):
                                    print(data)
                                    if(data != "Access denied"){
                                        self.quizId = JSON(item)["request"]["identifier"].stringValue
                                        var response = JsonUtil.convertJsonFromJsonString(inputJson: data)!["result"]
                                        print(response)
                                        Singleton.quizApiResult[self.quizId] = data
                                        print("Check this -> ", Singleton.quizApiResult)
                                        let completed = JsonUtil.convertJsonFromJsonString(inputJson: data)!["result"]
                                        let passPercent = JsonUtil.convertJsonFromJsonString(inputJson: data)!["passPercent"]
                                        if completed >= passPercent {
                                            Singleton.isCompleted = true
                                        } else {
                                            Singleton.isCompleted = false
                                        }
                                        
                                        print("Success of telemetry quiz api call",Singleton.isCompleted)
                                        do  {
                                            self.count += 1
                                            if TestUtil.checkingTelemetry {
                                                TestUtil.telemetryCalls["quizSubmissionApi"] = true
                                                print(TestUtil.telemetryCalls)
                                            } else {
                                                if (self.count == self.quizCount){
                                                    try Singleton.fileManager.removeItem(at: self.fileUrlForQuizResponse!)
                                                    self.quizCount = 0
                                                    self.count = 0
                                                    if Connectivity.isConnectedToInternet() {
                                                        Telemetry().uploadTelemetryData()
                                                    }
                                                }
                                            }
                                            break
                                        }
                                        catch {
                                            break
                                        }
                                    }
                                    else {
                                         NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "showToast/\(AppConstants.submitDetailsError)/force"])
                                        
                                    }
                                    
                                    
                                case .failure(_):
                                    print("Failure of telemetry quiz api call")
                                    break
                                }
                        }
                    }
                }
            }
        catch{
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "showToast/\(AppConstants.submitDetailsError)/force"])
         
        return
        }
            
        }
        
}

extension Dictionary {
    
    mutating func merge(with dictionary: Dictionary) {
        dictionary.forEach { updateValue($1, forKey: $0) }
    }
    func merged(with dictionary: Dictionary) -> Dictionary {
        var dict = self
        dict.merge(with: dictionary)
        return dict
    }
}

extension Array where Element: Comparable {
    func containsSameElements(as other: [Element]) -> Bool {
        return self.count == other.count && self.sorted() == other.sorted()
    }
}

