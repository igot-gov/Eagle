//  UserServices.swift
//  Lex
//  Created by Abhishek Gouvala / Shubham Singh on 5/17/18.
//  Copyright © 2018 Infosys. All rights reserved.

import Foundation
import CoreData

class UserServices {
    static func getLastLoggedIn() -> Date? {
        // Check when the user has last logged in. If the last logged in is more than 7 days, do not let the user go ahead
        let usersLastLoggedInRows = CoreDataService.getAllUserPreferences(keyNames: [AppConstants.lastLoggedInKey]) as! [NSManagedObject]
        
        // User has logged in before. Check if the logged in date is greater than 7 days
        if usersLastLoggedInRows.count > 0 {
            let lastLoggedInTimestampString = usersLastLoggedInRows[0].value(forKey: "value") as! String
            print(lastLoggedInTimestampString)
            
            let dateLastLoggedIn = Date(timeIntervalSince1970: Double(lastLoggedInTimestampString)!/1000 )
            
            return dateLastLoggedIn
        }
        return nil
    }
}
