//
//  ChatBotTests.swift
//  LexTest
//
//  Created by Bodhi on 9/17/18.
//  Copyright © 2018 Infosys. All rights reserved.
//

import XCTest

class ChatBotTests: XCTestCase {
    
    static func getTimeStampString(inputDate: Date) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
        return dateFormatter.string(from: inputDate)
    }
    
    static let chatBotUrlParams = "mic=0&keyboard=0&speech=1" + "&ts=" + getTimeStampString(inputDate: Date.init())

    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
    func testExample() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
    }
    
    func testPerformanceExample() {
        // This is an example of a performance test case.
        self.measure {
            // Put the code you want to measure the time of here.
        }
    }
    

    
}
