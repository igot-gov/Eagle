//  CoreDataService.swift
//  Lex
//  Created by Abhishek Gouvala / Shubham Singh on 3/19/18.
//  Copyright © 2018 Infosys. All rights reserved.

import Foundation
import Alamofire
import SwiftyJSON
import CoreData

class CoreDataService: NSObject {
    static func convertStringToBinary(input: String) -> Data {
        return input.data(using: .utf8, allowLossyConversion: false)!
    }
    static func convertBinaryToString(input: Data) -> String {
        return String(data: input, encoding: String.Encoding.utf8)!
    }
    static let entityName = "LexResource"
    static let userPreferenceEntityName = "UserPreferences"
    static let downloadPersistanceEntityName = "DownloadPersistance"
    
    class CoreDataModel {
        var identifier:String
        var contentType: String
        var json: Data
        var telemetryData: Data = Data()
        var binaryOne: Data = Data()
        var dateOne: Date = Date()
        var dateTwo: Date = Date()
        var modifiedDate: Date = Date()
        var expiryDate: Date = DateUtil.addDaysToDate(inputDate: Date(), noOfDays: AppConstants.contentExpiryInDays)
        var requestedDate: Date = Date()
        var downloadAttempts: Int = 0
        var integerOne: Int = 0
        var integerTwo: Int = 0
        var percentComplete: Int = 0
        var requestedByUser: Bool = false
        var status: String = "INITIATED"
        
        var stringOne: String = ""
        var stringTwo: String = ""
        var email : String = UserDetails.UID
        
        init(identifier: String, contentType: String, artifactUrl: String, thumbnailUrl: String, json: Data, requestedByUser: Bool, status: String, percentComplete: Int) {
            self.identifier = identifier
            self.contentType = contentType
            self.json = json
            self.requestedByUser = requestedByUser
            self.status = status
            self.stringOne = thumbnailUrl
            self.stringTwo = artifactUrl
            self.percentComplete = percentComplete
        }
        init(identifier : String,contentType : String,json : Data, telemetryData : Data, binaryOne : Data, dateOne : Date, dateTwo : Date, modifiedDate : Date, expiryDate : Date, requestedDate : Date, downloadAttempts : Int, integerOne : Int, integerTwo : Int , percentComplete : Int, requestedByUser : Bool, status : String, stringOne : String , stringTwo : String, uuid : String){
            self.identifier = identifier
            self.contentType = contentType
            self.json = json
            self.telemetryData = telemetryData
            self.binaryOne = binaryOne
            self.dateOne = dateOne
            self.dateTwo = dateTwo
            self.modifiedDate = modifiedDate
            self.expiryDate = expiryDate
            self.requestedDate = requestedDate
            self.downloadAttempts = downloadAttempts
            self.integerOne = integerOne
            self.integerTwo = integerTwo
            self.percentComplete = percentComplete
            self.requestedByUser = requestedByUser
            self.status = status
            self.stringOne = stringOne
            self.stringTwo = stringTwo
            self.email = uuid
        }
        
    }
    
    // Creating the core data object with content data
    static func createCoreDataFromContentModel(contentObj: ContentModel) -> CoreDataModel {
        return CoreDataModel(identifier: contentObj.identifier, contentType: contentObj.contentType, artifactUrl: contentObj.artifactURL ?? "", thumbnailUrl: contentObj.thumbnailURL, json: convertStringToBinary(input: contentObj.resourceJSON), requestedByUser: contentObj.userInitiated, status: contentObj.status, percentComplete: contentObj.percentComplete )
    }
    
    static func saveDataToCoreData(coreDataObj: CoreDataModel, deleteExisting: Bool = true) -> Bool {
        let existingResult = getCoreDataRow(identifier: coreDataObj.identifier, uuid: UserDetails.UID) as! [NSManagedObject]
        
        if existingResult.isEmpty == false {
            //print("Entries already exist for this identifier")
            let userPreferences = CoreDataService.getAllUserPreferences(keyNames: [AppConstants.lastLoggedInKey]) as! [NSManagedObject]
            let uuid = userPreferences[0].value(forKey: "userUuid") as! String
            if deleteExisting {
                let _ = deleteCoreDataWith(identifier: coreDataObj.identifier, uuid: uuid)
                /*
                 let deleted = deleteCoreDataWith(identifier: coreDataObj.identifier)
                 if deleted {
                 print("Deleted older entried of the identifier: \(coreDataObj.identifier)")
                 } else {
                 print("Error while deleting the older entries with identifier: \(coreDataObj.identifier)")
                 }
                 */
            }
        }
        
        // Getting the core data context to save the new entry
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.viewContext
        
        
        let entity = NSEntityDescription.entity(forEntityName: AppConstants.lexResourceV2EntityName, in: context!)
        let newEntry = NSManagedObject(entity: entity!, insertInto: context)
        
        // Adding data values to the entries
        newEntry.setValue(coreDataObj.identifier, forKey: "content_id")
        newEntry.setValue(coreDataObj.contentType, forKey: "content_type")
        newEntry.setValue(coreDataObj.json, forKey: "json")
        newEntry.setValue(coreDataObj.binaryOne, forKey: "binaryOne")
        newEntry.setValue(coreDataObj.dateOne, forKey: "dateOne")
        newEntry.setValue(coreDataObj.dateTwo, forKey: "dateTwo")
        newEntry.setValue(coreDataObj.downloadAttempts, forKey: "download_attempt")
        newEntry.setValue(coreDataObj.expiryDate, forKey: "expiry_date")
        newEntry.setValue(coreDataObj.integerOne, forKey: "integerOne")
        newEntry.setValue(coreDataObj.integerTwo, forKey: "integerTwo")
        newEntry.setValue(coreDataObj.modifiedDate, forKey: "modified_date")
        newEntry.setValue(coreDataObj.percentComplete, forKey: "percent_complete")
        newEntry.setValue(coreDataObj.requestedDate, forKey: "requested_date")
        newEntry.setValue(coreDataObj.requestedByUser, forKey: "requestedByUser")
        newEntry.setValue(coreDataObj.status, forKey: "status")
        newEntry.setValue(coreDataObj.stringOne, forKey: "stringOne")
        newEntry.setValue(coreDataObj.stringTwo, forKey: "stringTwo")
        newEntry.setValue(coreDataObj.telemetryData, forKey: "telemetry_data")
        newEntry.setValue(coreDataObj.email, forKey: "userUuid")
        
        var isDataSaved: Bool = false;
        
        // Saving the data into the core data
        do {
            try context?.save(); appDelegate?.saveContext(); isDataSaved = true;
        } catch let error as NSError{
            print(error.localizedDescription)
            print("Failed saving")
        }
        return isDataSaved
    }
    
    static func getAllRows(entity : String) -> Any? {
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.viewContext
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: entity)
        request.returnsObjectsAsFaults = false
        do {
            let result = try context?.fetch(request)
            return result
        } catch {
            print("Failed")
            return nil
        }
    }
    
    static func getAllExpiredRows() -> Any? {
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.viewContext
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: AppConstants.lexResourceV2EntityName)
        request.predicate = NSPredicate(format: "expiry_date <= %@", Date() as NSDate)
        request.returnsObjectsAsFaults = false
        do {
            let result = try context?.fetch(request)
            return result
        } catch {
            print("Failed")
            return nil
        }
    }
    
    static func getCoreDataRow(identifier: String, uuid : String) -> Any? {
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.viewContext
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: AppConstants.lexResourceV2EntityName)
        request.predicate = NSPredicate(format: "content_id == %@ AND userUuid == %@", identifier,uuid)
        request.returnsObjectsAsFaults = false
        do {
            let result = try context?.fetch(request)
            return result
        } catch {
            print("Failed")
            return nil
        }
    }
    
    
    static func deleteCoreDataWith(identifier: String,uuid : String, entityName: String =  AppConstants.lexResourceV2EntityName) -> Bool {
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.viewContext
        var request = NSFetchRequest<NSFetchRequestResult>(entityName: AppConstants.lexResourceV2EntityName)
        request.predicate = NSPredicate(format: "content_id == %@ AND userUuid == %@", identifier,uuid)
        if(entityName == AppConstants.lexResourceEntityName) {
            request = NSFetchRequest<NSFetchRequestResult>(entityName: AppConstants.lexResourceEntityName)
            request.predicate = NSPredicate(format: "content_id == %@", identifier)
            
        }
        request.returnsObjectsAsFaults = false
        do {
            let result = try context?.fetch(request) ?? []
            if result.count>0 {
                for data in result as! [NSManagedObject] {
                    context?.delete(data)
                    try context?.save();
                    appDelegate?.saveContext();
                }
            }
            return true
        } catch {
            print("Failed deleting the rows from the core data")
            return false
        }
    }
    
    static func deleteCoreDataForDownloadPersistance(identifier: String) -> Bool {
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.viewContext
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: CoreDataService.downloadPersistanceEntityName)
        request.predicate = NSPredicate(format: "contentId == %@", identifier)
        request.returnsObjectsAsFaults = false
        do {
            let result = try context?.fetch(request) ?? []
            if result.count>0 {
                for data in result as! [NSManagedObject] {
                    context?.delete(data)
                    try context?.save();
                    appDelegate?.saveContext();
                }
            }
            return true
        } catch {
            print("Failed deleting the rows from the core data")
            return false
        }
    }
    
    static func deleteUserPreferences(keyName: String) -> Bool {
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.viewContext
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: CoreDataService.userPreferenceEntityName)
        request.predicate = NSPredicate(format: "key == %@", keyName)
        request.returnsObjectsAsFaults = false
        do {
            let result = try context?.fetch(request) ?? []
            if result.count>0 {
                for data in result as! [NSManagedObject] {
                    context?.delete(data)
                    try context?.save();
                    appDelegate?.saveContext();
                }
            }
            return true
        } catch {
            print("Failed deleting the rows from the core data")
            return false
        }
    }
    
    static func updateRowInCoreData(withIdentifier: String, newRow coreDataObj: CoreDataModel) -> Bool {
        let rows = getCoreDataRow(identifier: withIdentifier, uuid: UserDetails.UID) as! [NSManagedObject]
        
        if rows.count>0 {
            let rowToUpdate = rows[0]
            
            rowToUpdate.setValue(coreDataObj.identifier, forKey: "content_id")
            rowToUpdate.setValue(coreDataObj.contentType, forKey: "content_type")
            rowToUpdate.setValue(coreDataObj.json, forKey: "json")
            rowToUpdate.setValue(coreDataObj.binaryOne, forKey: "binaryOne")
            rowToUpdate.setValue(coreDataObj.dateOne, forKey: "dateOne")
            rowToUpdate.setValue(coreDataObj.dateTwo, forKey: "dateTwo")
            rowToUpdate.setValue(coreDataObj.downloadAttempts, forKey: "download_attempt")
            rowToUpdate.setValue(coreDataObj.expiryDate, forKey: "expiry_date")
            rowToUpdate.setValue(coreDataObj.integerOne, forKey: "integerOne")
            rowToUpdate.setValue(coreDataObj.integerTwo, forKey: "integerTwo")
            rowToUpdate.setValue(coreDataObj.modifiedDate, forKey: "modified_date")
            rowToUpdate.setValue(coreDataObj.percentComplete, forKey: "percent_complete")
            rowToUpdate.setValue(coreDataObj.requestedDate, forKey: "requested_date")
            rowToUpdate.setValue(coreDataObj.requestedByUser, forKey: "requestedByUser")
            rowToUpdate.setValue(coreDataObj.status, forKey: "status")
            rowToUpdate.setValue(coreDataObj.stringOne, forKey: "stringOne")
            rowToUpdate.setValue(coreDataObj.stringTwo, forKey: "stringTwo")
            rowToUpdate.setValue(coreDataObj.telemetryData, forKey: "telemetry_data")
            rowToUpdate.setValue(coreDataObj.email, forKey: "userUuid")
            
            let userPreferences = CoreDataService.getAllUserPreferences(keyNames: [AppConstants.lastLoggedInKey]) as! [NSManagedObject]
            let userUid = userPreferences[0].value(forKey: "userUuid") as! String
            let isRowDeleted = deleteCoreDataWith(identifier: coreDataObj.identifier, uuid: userUid)
            if isRowDeleted {
                return saveDataToCoreData(coreDataObj: coreDataObj)
            }
            
        }
        return false
    }
    
    static func updateDownloadedStatusFor(identifier: String, offlineThumbnailUrl: String, offlineArtifactUrl: String) -> Bool {
        let rows = getCoreDataRow(identifier: identifier, uuid: UserDetails.UID) as! [NSManagedObject]
        
        if rows.count>0 {
            let rowToUpdate = rows[0]
            
            let identifier:String = rowToUpdate.value(forKeyPath: "content_id") as! String
            let contentType: String = rowToUpdate.value(forKeyPath: "content_type") as! String
            let json: Data = rowToUpdate.value(forKeyPath: "json") as! Data
            let dateOne: Date = rowToUpdate.value(forKeyPath: "dateOne") as! Date
            let dateTwo: Date = rowToUpdate.value(forKeyPath: "dateTwo") as! Date
            let modifiedDate: Date = Date()
            let expiryDate: Date = rowToUpdate.value(forKeyPath: "expiry_date") as! Date
            let requestedDate: Date = rowToUpdate.value(forKeyPath: "requested_date") as! Date
            let downloadAttempts: Int = 0
            let integerOne: Int = 0
            let integerTwo: Int = 0
            let percentComplete: Int = 0
            let requestedByUser: Bool = rowToUpdate.value(forKey: "requestedByUser") as! Bool
            let status: String = rowToUpdate.value(forKeyPath: "status") as! String
            var stringOne: String = rowToUpdate.value(forKeyPath: "stringOne") as! String
            var stringTwo: String = rowToUpdate.value(forKeyPath: "stringTwo") as! String
            
            if offlineThumbnailUrl.count>0 {
                stringOne = offlineThumbnailUrl
            }
            if offlineArtifactUrl.count>0 {
                stringTwo = offlineArtifactUrl
            }
            // Creating the core data model here
            let coreDataObj = CoreDataModel(identifier: identifier, contentType: contentType, artifactUrl: stringTwo, thumbnailUrl: stringOne, json: json, requestedByUser: requestedByUser, status: status, percentComplete: percentComplete)
            coreDataObj.dateOne = dateOne
            coreDataObj.dateTwo = dateTwo
            coreDataObj.modifiedDate = modifiedDate
            coreDataObj.expiryDate = expiryDate
            coreDataObj.requestedDate = requestedDate
            coreDataObj.downloadAttempts = downloadAttempts
            coreDataObj.integerOne = integerOne
            coreDataObj.integerTwo = integerTwo
            coreDataObj.requestedByUser = requestedByUser
            coreDataObj.percentComplete = percentComplete
            coreDataObj.status = (stringOne.count>0 && !stringOne.hasPrefix("http") &&  !stringOne.hasPrefix("https") && stringTwo.count>0 && !stringTwo.hasPrefix("http") && !stringTwo.hasPrefix("https")) ? "DOWNLOADED" : status
            coreDataObj.stringOne = stringOne
            coreDataObj.stringTwo = stringTwo
            
            if (coreDataObj.status.lowercased()=="downloaded") {
                coreDataObj.percentComplete = 100
            }
            let userPreferences = CoreDataService.getAllUserPreferences(keyNames: [AppConstants.lastLoggedInKey]) as! [NSManagedObject]
            let uuid = userPreferences[0].value(forKey: "userUuid") as! String
            let isRowDeleted = deleteCoreDataWith(identifier: identifier, uuid: uuid)
            if isRowDeleted {
                return saveDataToCoreData(coreDataObj: coreDataObj)
            }
        }
        return false
    }
    
    static func getDownloadedResources(includeUserInitiated: Bool = false) -> Any? {
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.viewContext
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: AppConstants.lexResourceV2EntityName)
        //        let statusPredicate = NSPredicate(format: "status == %@", "DOWNLOADED")
        let contentTypePredicate = NSPredicate(format: "content_type ==%@", "Resource")
        
        // Sort predicate
        let sectionSortDescriptor = NSSortDescriptor(key: "modified_date", ascending: false)
        let sortDescriptors = [sectionSortDescriptor]
        
        var predicateArr: [NSPredicate] = []
        predicateArr.append(contentTypePredicate);
        
        if includeUserInitiated {
            predicateArr.append(NSPredicate(format: "requestedByUser==true"))
        } else {
            predicateArr.append(NSPredicate(format: "requestedByUser==false"))
        }
        let andPredicate = NSCompoundPredicate(type: NSCompoundPredicate.LogicalType.and, subpredicates: predicateArr)
        
        // let contentTypePredicate = NSPredicate(format: "content_type ==%@", "Resource")
        //        let andPredicate = NSCompoundPredicate(type: NSCompoundPredicate.LogicalType.and, subpredicates: [statusPredicate, contentTypePredicate])
        
        request.predicate = andPredicate
        request.sortDescriptors = sortDescriptors
        request.returnsObjectsAsFaults = false
        
        do {
            let result = try context?.fetch(request)
            return result
        } catch {
            print("Failed")
            return nil
        }
    }
    
    static func getDownloadedResource(withIdentifier: String) -> Any? {
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.viewContext
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: AppConstants.lexResourceV2EntityName)
        
        let contentIdPredicate = NSPredicate(format: "content_id == %@", withIdentifier)
        //        let statusPredicate = NSPredicate(format: "status == %@", "DOWNLOADED")
        //        let contentTypePredicate = NSPredicate(format: "content_type == %@", "Resource")
        
        let andPredicate = NSCompoundPredicate(type: NSCompoundPredicate.LogicalType.and, subpredicates: [contentIdPredicate])
        
        request.predicate = andPredicate
        
        request.returnsObjectsAsFaults = false
        
        do {
            let result = try context?.fetch(request)
            return result
        } catch {
            print("Failed")
            return nil
        }
    }
    
    static func getOldestResourceExpiry() -> Date? {
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.viewContext
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: AppConstants.lexResourceV2EntityName)
        
        let statusPredicate = NSPredicate(format: "status == %@", "DOWNLOADED")
        let expiryDateSortDescriptor = NSSortDescriptor(key: "expiry_date", ascending: true)
        let andPredicate = NSCompoundPredicate(type: NSCompoundPredicate.LogicalType.and, subpredicates: [statusPredicate])
        
        request.predicate = andPredicate
        request.sortDescriptors = [expiryDateSortDescriptor]
        
        request.fetchLimit = 1
        request.propertiesToFetch = ["expiry_date"]
        
        request.returnsObjectsAsFaults = false
        
        do {
            let results = try context?.fetch(request)
            
            let allRows = results as! [NSManagedObject]
            
            if (allRows.count==1) {
                let row = allRows[0]
                return row.value(forKey: "expiry_date") as? Date
            }
        } catch {
            print("Failed")
        }
        return nil
    }
    
    static func getCoreDataContent(withIdentifiers: [String]) -> Any? {
        if withIdentifiers.count<1 {
            return nil
        }
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.viewContext
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: AppConstants.lexResourceV2EntityName)
        
        var predicateArr: [NSPredicate] = []
        for id in withIdentifiers {
            predicateArr.append(NSPredicate(format: "content_id == %@", id))
        }
        let orPredicate = NSCompoundPredicate(type: NSCompoundPredicate.LogicalType.or, subpredicates: predicateArr)
        
        request.predicate = orPredicate
        
        request.returnsObjectsAsFaults = false
        
        do {
            let result = try context?.fetch(request)
            return result
        } catch {
            print("Failed")
            return nil
        }
    }
    static func getDownloadedResource(withIdentifiers: [String]) -> Any? {
        if withIdentifiers.count<1 {
            return nil
        }
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.viewContext
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: AppConstants.lexResourceV2EntityName)
        
        //        var resourcePredicate = NSPredicate(format: "content_type == %@", "Resource")
        
        var predicateArr: [NSPredicate] = []
        for id in withIdentifiers {
            predicateArr.append(NSPredicate(format: "content_id == %@ AND content_type == %@", id, "Resource"))
        }
        let andPredicate = NSCompoundPredicate(type: NSCompoundPredicate.LogicalType.or, subpredicates: predicateArr)
        
        request.predicate = andPredicate
        
        request.returnsObjectsAsFaults = false
        
        do {
            let result = try context?.fetch(request)
            return result
        } catch {
            print("Failed")
            return nil
        }
    }
    
    static func getNonLeafLevelDataForUpdate() -> Any? {
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.viewContext
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: AppConstants.lexResourceV2EntityName)
        request.predicate = NSPredicate(format: "requestedByUser == true")
        
        request.returnsObjectsAsFaults = false
        do {
            let result = try context?.fetch(request)
            return result
        } catch {
            print("Failed")
            return nil
        }
    }
    
    
    static func getNonLeafLevelData(contentType: String, includeUserInitiated: Bool = false) -> Any? {
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.viewContext
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: AppConstants.lexResourceV2EntityName)
        let contentTypePredicate = NSPredicate(format: "content_type == %@", contentType)
        
        var predicateArr: [NSPredicate] = []
        predicateArr.append(contentTypePredicate)
        
        if includeUserInitiated {
            predicateArr.append(NSPredicate(format: "requestedByUser == true"))
        } else {
            predicateArr.append(NSPredicate(format: "requestedByUser == false"))
        }
        
        let andPredicate = NSCompoundPredicate(type: NSCompoundPredicate.LogicalType.and, subpredicates: predicateArr)
        
        // let contentTypePredicate = NSPredicate(format: "content_type ==%@", "Resource")
        // let andPredicate = NSCompoundPredicate(type: NSCompoundPredicate.LogicalType.and, subpredicates: [statusPredicate, contentTypePredicate])
        
        request.predicate = andPredicate
        request.returnsObjectsAsFaults = false
        
        do {
            let result = try context?.fetch(request)
            return result
        } catch {
            print("Failed")
            return nil
        }
    }
    
    static func markCoreDataDeletedWith(identifier: String) -> Bool {
        let appDelegate = AppDelegate.appDelegate
        let rows = getCoreDataRow(identifier: identifier, uuid: UserDetails.UID) as! [NSManagedObject]
        
        if rows.count>0 {
            // Updated the artifact and download url and mark the status as deleted
            let rowToUpdate = rows[0]
            
            rowToUpdate.setValue("DELETED", forKey: "status")
            rowToUpdate.setValue("", forKey: "stringOne")
            rowToUpdate.setValue("", forKey: "stringTwo")
            rowToUpdate.setValue(0, forKey: "percent_complete")
            
            // Saving the updated data into the core data
            do {
                let context = AppDelegate.appDelegate.persistentContainer.viewContext
                try context.save();
                appDelegate?.saveContext()
                return true
            } catch let error as NSError {
                print(error.localizedDescription)
                print("Failed saving")
            }
        }
        return false
    }
    
    static func updateContentExpiry() -> Bool {
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.viewContext
        let rows = getAllRows(entity : AppConstants.lexResourceV2EntityName) as! [NSManagedObject]
        
        do {
            if rows.count>0 {
                // Update the rows here
                for row in rows {
                    row.setValue(DateUtil.addDaysToDate(inputDate: Date(), noOfDays: AppConstants.contentExpiryInDays), forKey: "expiry_date")
                }
            }
            
            try context?.save()
            appDelegate?.saveContext()
            return true
        } catch let error as NSError {
            print(error.localizedDescription)
        }
        return false
    }
    
    static func saveUserPreferences(entries: [String:String],uuid : String, wid:String,accessToken : String, update: Bool = false) -> Bool? {
        // Getting the core data context to save the new entry
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.viewContext
        
        let entity = NSEntityDescription.entity(forEntityName: CoreDataService.userPreferenceEntityName, in: context!)
        
        // Saving each preferences
        for entry in entries {
            if update {
                let request = NSFetchRequest<NSFetchRequestResult>(entityName: CoreDataService.userPreferenceEntityName)
                request.predicate = NSPredicate(format: "key == %@", entry.key)
                request.returnsObjectsAsFaults = false
                do {
                    var result = try context?.fetch(request)
                    
                    result = result as! [NSManagedObject]
                    
                    if result?.count==1 {
                        // Update the entry
                        let row = result![0] as! NSManagedObject
                        row.setValue(entry.value, forKey: "value")
                        row.setValue(uuid, forKey: "userUuid")
                        row.setValue(accessToken, forKey: "accessToken")
                        row.setValue(wid, forKey: "wid")
                    } else {
                        insertAsNewEntry(entity: entity!, context: context!, entry: entry,uuid: uuid)
                    }
                } catch {
                    print("Failed")
                    return nil
                }
            } else {
                insertAsNewEntry(entity: entity!, context: context!, entry: entry,uuid: uuid)
            }
        }
        
        // Updating the core data with these entries
        var isDataSaved: Bool = false;
        
        // Saving the data into the core data
        do {
            try context?.save(); appDelegate?.saveContext(); isDataSaved = true;
        } catch let error as NSError{
            print(error.localizedDescription)
            print("Failed saving")
        }
        return isDataSaved
    }
    
    private static func insertAsNewEntry(entity: NSEntityDescription, context: NSManagedObjectContext, entry: Dictionary<String, String>.Element,uuid : String) {
        let newEntry = NSManagedObject(entity: entity, insertInto: context)
        
        // Adding data values to the entries
        newEntry.setValue(entry.key, forKey: "key")
        newEntry.setValue(entry.value, forKey: "value")
        newEntry.setValue(uuid, forKey: "userUuid")
    }
    
    static func getAllUserPreferences(keyNames: [String]) -> Any? {
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.viewContext
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: CoreDataService.userPreferenceEntityName)
        
        var predicateArr: [NSPredicate] = []
        for keyName in keyNames {
            predicateArr.append(NSPredicate(format: "key == %@", keyName))
        }
        
        let orPredicate = NSCompoundPredicate(type: NSCompoundPredicate.LogicalType.or, subpredicates: predicateArr)
        
        request.predicate = orPredicate
        request.returnsObjectsAsFaults = false
        
        do {
            let result = try context?.fetch(request)
            return result
        } catch {
            print("Failed")
            return nil
        }
    }
}

