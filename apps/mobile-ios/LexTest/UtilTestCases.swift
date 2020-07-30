////
////  UtilTestCases.swift
////  LexTests
////
////  Created by LexUser on 10/6/18.
////  Copyright © 2018 Infosys. All rights reserved.
////
//
//import XCTest
//import Reachability
//import SwiftyJSON
//import AVFoundation
//
//@testable import Lex
//
//class UtilTestCases: XCTestCase {
//    
//    let dateStringExample1 = "25/01/2011"
//    var dateFromString:Date! = nil
//    let dateFormatter = DateFormatter()
//    
//    override func setUp() {
//        // Put setup code here. This method is called before the invocation of each test method in the class.
//        
//        dateFormatter.dateFormat = "dd/MM/yyyy"
//        dateFromString = dateFormatter.date(from: dateStringExample1)
//    }
//    
//    override func tearDown() {
//        // Put teardown code here. This method is called after the invocation of each test method in the class.
//    }
//    
//    //DATE UTIL TESTS
//    func testDateUtilGetUnixTime() {
//        // This is an example of a functional test case.
//        // Use XCTAssert and related functions to verify your tests produce the correct results.
//        
//        let result = DateUtil.getUnixTimeFromDate(input: dateFromString!)
//        
//        let toCheck = 1295893800000
//        XCTAssert(toCheck == result, "Date util - unixTimeConverter fails")
//        
//    }
//    
//    func testAddDaysToDate() {
//        let result = DateUtil.addDaysToDate(inputDate: dateFromString, noOfDays: 5)
//        dateFormatter.dateFormat = "dd/MM/yyyy"
//        let toCheckDate = dateFormatter.date(from: "30/01/2011")
//        
//        XCTAssert(result == toCheckDate , "Date util - addDaysToDate fails")
//    }
//    
//    func testGetMinutesDifference() {
//        
//        dateFormatter.dateFormat = "dd/MM/yyyy HH:mm"
//        let date1 = dateFormatter.date(from: "25/01/2011 12:00")
//        let date2 = dateFormatter.date(from: "25/01/2011 11:30")
//        
//        let result = DateUtil.getMinutesDifference(date1: date1!, date2: date2!)
//        
//        XCTAssert(result == 30 , "Date util - GetMinutesDifference fails")
//    }
//    
//    func testGetDateDifferenceInDays() {
//        dateFormatter.dateFormat = "dd/MM/yyyy HH:mm"
//        let date1 = dateFormatter.date(from: "26/01/2011 12:00")
//        let date2 = dateFormatter.date(from: "25/01/2011 11:30")
//        
//        let result = DateUtil.getDateDifferenceInDays(date1: date1!, date2: date2!)
//        
//        XCTAssert(result == 1.0208333333333333 , "Date util - GetDateDifferenceInDays fails")
//    }
//    
//    func testGetTimeStampString() {
//        let result = DateUtil.getTimeStampString(inputDate: Date.init())
//        let dateFormatter = DateFormatter()
//        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
//        let toCheck = dateFormatter.string(from: Date.init())
//        
//        XCTAssert(result == toCheck, "Date util - GetDateDifferenceInDays fails")
//    }
//    
//    //JSON Util
//    func testConvertJsonFromJsonString() {
//        let result = JsonUtil.convertJsonFromJsonString(inputJson: TestUtil.demoJson)
//        XCTAssert(result != nil, "Json util - ConvertJsonFromJsonString fails")
//    }
//    
//    //Network Util
//    
//    func testGetNetworkType(){
//        let result = NetworkUtil.getNetworkType()
//        print(result)
//        var toChk:NetworkUtil.NetworkType
//        let value = TestUtil.currentNetworkType
//        if(value == "Wifi"){
//            toChk = .Wifi
//        }else if(value == "Cellular"){
//            toChk = .Cellular
//        }else {
//            toChk = .None
//        }
//        XCTAssert(toChk == result, "Network util - GetNetworkType fails")
//    }
//}
//
//
//
//
////                if(TestUtil.checkingDownlodsCancel) {
////                    DownloadService.downloadArtifact(withId: "lex_2082874997657425000", downloadtype: AppConstants.downloadType.DEFAULT.name())
////                }
