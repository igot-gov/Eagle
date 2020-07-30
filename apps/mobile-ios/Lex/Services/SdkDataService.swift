//  SdkDataService.swift
//  Lex
//  Created by Ipshita Chatterjee / Shubham Singh on 11/27/18.
//  Copyright © 2018 Infosys. All rights reserved.

import Foundation
import Alamofire
import SwiftyJSON
import CoreData

class SdkDataService: NSObject {
    
    static func convertStringToBinary(input: String) -> Data {
        return input.data(using: .utf8, allowLossyConversion: false)!
    }
    
    static func convertBinaryToString(input: Data) -> String {
        return String(data: input, encoding: String.Encoding.utf8)!
    }
    
    
    
    static func saveSdkDataToCoreData(sdkResourceObject : SdkResource,type : String?, deleteExisting: Bool = true) -> Bool {
        
        var entityName = AppConstants.SdkResourceEntityName
        if type != nil {
            entityName = AppConstants.SdkResourceWithTypeEntityName
        }
        
        let existingResult = getCoreDataRowforSdkResource(key : sdkResourceObject.key,entityName: entityName) as! [NSManagedObject]
        
        if existingResult.isEmpty == false {
            if deleteExisting {
                let _ = deleteCoreDataWithKey(key: sdkResourceObject.key,entityName: entityName)
            }
        }
        // Getting the core data context to save the new entry
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.viewContext
        
        var entity = NSEntityDescription.entity(forEntityName: AppConstants.SdkResourceEntityName, in: context!)
        var newEntry = NSManagedObject(entity: entity!, insertInto: context)
        
        if type != nil {
            entity = NSEntityDescription.entity(forEntityName: AppConstants.SdkResourceWithTypeEntityName, in: context!)
            newEntry = NSManagedObject(entity: entity!, insertInto: context)
        }
        
        // Adding data values to the entries
        newEntry.setValue(sdkResourceObject.key, forKey: "key")
        newEntry.setValue(sdkResourceObject.value, forKey: "value")
        newEntry.setValue(sdkResourceObject.userEmail, forKey: "userEmail")
        newEntry.setValue(sdkResourceObject.dateInserted, forKey: "dateInserted")
        newEntry.setValue(sdkResourceObject.dateUpdated, forKey: "dateUpdated")
        newEntry.setValue(sdkResourceObject.tenantName, forKey: "tenantName")
        newEntry.setValue(sdkResourceObject.mobileAppTenant, forKey: "mobileAppTenant")
        
        if type != nil {
            newEntry.setValue(sdkResourceObject.type, forKey: "type")
        }
        
        
        var isDataSaved: Bool = false;
        
        // Saving the data into the core data now
        do {
            try context?.save(); appDelegate?.saveContext(); isDataSaved = true;
        } catch let error as NSError{
            print(error.localizedDescription)
            print("Failed saving Sdk Resource")
        }
        return isDataSaved
    }
    
    
    static func getCoreDataRowforSdkResource(key: String,entityName : String) -> Any? {
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.viewContext
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: entityName)
        request.predicate = NSPredicate(format: "key == %@", key)
        request.returnsObjectsAsFaults = false
        do {
            let result = try context?.fetch(request)
            return result
        } catch {
            print("Failed")
            return nil
        }
    }
    
    static func deleteCoreDataWithKey(key: String,entityName : String) -> Bool {
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.viewContext
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: entityName)
        request.predicate = NSPredicate(format: "key == %@", key)
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
    
    
}
