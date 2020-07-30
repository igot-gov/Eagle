//  SdkModel.swift
//  Lex
//  Created by Abhishek Gouvala / Shubham Singh on 11/28/18.
//  Copyright © 2018 Infosys. All rights reserved.

import Foundation

public class SdkResource {
    
    var key:String
    var value: Data =  Data()
    var userEmail: String
    var dateInserted: Date = Date()
    var dateUpdated: Date = Date()
    var tenantName: String = ""
    var mobileAppTenant: String = ""
    var type : String = ""
    
    init(key : String,value : Data,userEmail : String,dateInserted : Date,dateUpdated : Date, tenantName : String, mobileAppTenant : String) {
        self.key = key
        self.value = value
        self.userEmail = userEmail
        self.dateInserted = dateInserted
        self.dateUpdated = dateUpdated
        self.tenantName = tenantName
        self.mobileAppTenant = mobileAppTenant
    }
    
    init(key : String,value : Data,userEmail : String,dateInserted : Date,dateUpdated : Date, tenantName : String, mobileAppTenant : String,type : String) {
        self.key = key
        self.value = value
        self.userEmail = userEmail
        self.dateInserted = dateInserted
        self.dateUpdated = dateUpdated
        self.tenantName = tenantName
        self.mobileAppTenant = mobileAppTenant
        self.type = type
    }
    
    
}
