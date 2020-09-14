//  APIData.swift
//  Lex
//  Created by Shubham Singh on 11/27/18.
//  Copyright © 2018 Infosys. All rights reserved.

import Foundation
import SwiftyJSON

class APIData {
    init() {
        // Read all the rows from core data and initialize the cache
    }
    
    static let lastUpdatedTime: [String: Date] = [:]
    static let apiDataCache: [String:JSON] = [:]
    
    static func setData(key: String, value: JSON) {
        // First set it at the apiDataCache and return the acknowledgement.
        // Asncly, set it in the core data
    }
    
    static func getData(key: String, fromPersistance: Bool = false) {
        // Get the data from the cache if the value fromPersistance is false, else get it from Persistance Store (Core data)
        // If there is no value in the cache or the entry is new, get the data from the API and call set key
    }
    
    static func API(method: HTTPMethods = .GET, headers: [String: String] = [:], url: String) -> JSON? {
        return nil
        // JSON should be as follows
        /*
         {
         "url": "URL of the request",
         "headers": "JSON headers",
         "method": "HTTP Method"
         "apiResponse": "Response you got from the API from Server"
         "mobileInjectedFields": "Required for your processing"// Example: "STATUS_CODE": 200, 201, 301, 401, 403, 500, 502
         }
         
         {
            "url": "https://lex.infosysapps.com/content/heierarchy/lex_1234",
            "headers": {
                "authorization": "Bearer ........"
            },
            "method": "GET",
            "apiResponse": {....},
            mobileInjectedFields: {
                "responseReturnedTime": 122425346436354,
                "requestStartTime": 122323233,
                "statusCode": 200
            }
         }
         */
    }
    
    // HTTP Method names
    enum HTTPMethods: String {
        case GET, POST, PUT, DELETE, PATCH
        
        func name() -> String {
            return self.rawValue
        }
    }
}
