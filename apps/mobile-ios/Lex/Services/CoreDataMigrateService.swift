//  CoreDataMigrateService.swift
//  Lex
//  Created by prakash.chakraborty/ Abhishek Gouvala/ Shubham Singh on 11/28/18.
//  Copyright © 2018 Infosys. All rights reserved.

import Foundation
import CoreData

class CoreDataMigrateService: NSObject {
    
    static func reloadMigrationData() {
        
        
        let lexResourceEntityRows = CoreDataService.getAllRows(entity: AppConstants.lexResourceEntityName) as! [NSManagedObject]
        if(lexResourceEntityRows.count > 0) {
            //Start migration of data
            for row in lexResourceEntityRows {
                let contentId = row.value(forKey: "content_id") as! String
                let contentType = row.value(forKey: "content_type") as! String
                let json = row.value(forKey: "json") as! Data
                let binaryOne = Data()
                let dateOne = row.value(forKey: "dateOne") as! Date
                let dateTwo = row.value(forKey: "dateTwo") as! Date
                let downloadAttempts = row.value(forKey: "download_attempt") as! Int
                let expiryDate = row.value(forKey: "expiry_date") as! Date
                let integerOne = row.value(forKey: "integerOne") as! Int
                let integerTwo = row.value(forKey: "integerTwo") as! Int
                let modifiedDate = row.value(forKey: "modified_date") as! Date
                let percentComplete = row.value(forKey: "percent_complete") as! Int
                let requestedDate = row.value(forKey: "requested_date") as! Date
                let requestedByUser = row.value(forKey: "requestedByUser") as! Bool
                let status = row.value(forKey: "status") as! String
                let stringOne = row.value(forKey: "stringOne") as! String
                let stringTwo = row.value(forKey: "stringTwo")as! String
                let telemetryData = Data()
                
                //Calling the init method for creating a coreData entry
                let userPreferences = CoreDataService.getAllUserPreferences(keyNames: [AppConstants.lastLoggedInKey]) as! [NSManagedObject]
                let uuid = userPreferences[0].value(forKey: "userUuid") as! String
                let V2Row = CoreDataService.getCoreDataRow(identifier: contentId, uuid : uuid) as! [NSManagedObject]
                if V2Row.count == 0 {
                    let model = CoreDataService.CoreDataModel.init(identifier: contentId, contentType: contentType, json: json, telemetryData: telemetryData, binaryOne: binaryOne, dateOne: dateOne, dateTwo: dateTwo, modifiedDate: modifiedDate, expiryDate: expiryDate, requestedDate: requestedDate, downloadAttempts: downloadAttempts, integerOne: integerOne, integerTwo: integerTwo, percentComplete: percentComplete, requestedByUser: requestedByUser, status: status, stringOne: stringOne, stringTwo: stringTwo, uuid: UserDetails.UID)
                    let result = CoreDataService.saveDataToCoreData(coreDataObj: model)
                    if result {
                        print("Core Data row migrated")
                    }
                }
                let deleted = CoreDataService.deleteCoreDataWith(identifier: contentId, uuid: uuid, entityName: AppConstants.lexResourceEntityName)
                if deleted {
                    print("record Deleted from v1")
                }
            }
        } else {
            print("No data exists in Lex Resource V1...")
        }
    }
    
    
    
}
