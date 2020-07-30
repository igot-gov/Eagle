//
//  TestUtil.swift
//  Lex
//
//  Created by Ipshita Chatterjee / Shubham Singh on 10/18/18.
//  Copyright © 2018 Infosys. All rights reserved.
//

import Foundation
import SwiftyJSON

class TestUtil : NSObject {
    
    static var goOffline = false
    static var userLoggedInBefore = false
    static var isDownloadsAccessible = false
    static var gotoURL:String = ""
    static var isOpenedForOpenrap = false
    static var currentNetworkType = ""
    
    static var fileExists = false
    
    static var successDownloads:[String:Bool] = [
        "lex_4116684876543255600:module": false,
        "lex_15987705621528664000:course": false,
        "lex_auth_012505584815300608159:pdf": false,
        "lex_1050274409656454364936:video": false,
        "lex_auth_012602301141835776368:quiz": false,
        "lex_28267174781528120000:web-module": false
    ]
    static var checkingDownloads = false
    
    static var successDownloadsOpenrap:[String:Bool] = [
        "lex_28442257769122470000:course": false,
        "lex_2481421132412953076116:resource-video": false,
        "lex_14771372713162232000:resource-webModule": false
    ]
    static var checkingDownloadsOpenrap = false
    //static var checkingDownlodsCancel = true
    static var testCancelDict:[String : Any] = [
        "data" : [
            "id":"lex_2082874997657425000"
        ],
        "eventName":"DOWNLOAD_CANCEL"
    ]
    static var demoJson = String(data: try! JSONSerialization.data(withJSONObject: TestUtil.testCancelDict, options: []), encoding: .utf8)!
    
    
    static var checkingTelemetry = false
    
    
    static var telemetryUploadSuccess : [String:Bool] = [:]
    static var telemetryCalls : [String : Bool] = [
        "continueLearningApi" : false,
        "quizSubmissionApi" : false,
        "userHistoryApi" : false,
        "telemetryEventsApi" : false
    ]
    
    
    
    static var continueLearningPath = "continueLearningTest.json"
    static var quizSubmissionPath = "quizResponseTest.json"
    static var courseProgressPath = "dataCourseProgressTest.json"
    static var telemetryEventPath = "dataTelemetryTest.json"
}

