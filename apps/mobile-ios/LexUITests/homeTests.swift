//
//  homeTests.swift
//  LexUITests
//
//  Created by LexUser on 11/7/18.
//  Copyright © 2018 Infosys. All rights reserved.
//

import XCTest

class homeTests: XCTestCase {

    override func setUp() {
        // Put setup code here. This method is called before the invocation of each test method in the class.

        // In UI tests it is usually best to stop immediately when a failure occurs

        // UI tests must launch the application that they test. Doing this in setup will make sure it happens for each test method.
        
        // In UI tests it’s important to set the initial state - such as interface orientation - required for your tests before they run. The setUp method is a good place to do this.
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func testGoalsLink() {
       //----should be connected to wifi
        
        let app = XCUIApplication()
        app.launch()
        app.webViews.staticTexts["Goals"].tap()
        app.webViews.staticTexts["Create a new goal"].tap()
        app.webViews.staticTexts["My Goals"].tap()
        
    }
    
    func testChatbotButton() {
        //----should be connected to wif
        let app = XCUIApplication()
        app.launch()
        sleep(2)
        app.buttons["chatbotButton"].tap()
        let textField = app.textFields["keyboardTextField"]
        textField.tap()
        textField.typeText("Hello there!")
        app.buttons["sendButton"].tap()
        app.buttons["micButton"].press(forDuration: 3)
        app.buttons["chatbotCloseButton"].tap()
        sleep(5)
    }
   
    func testHomeDownload(){
        let app = XCUIApplication()
        app.launch()
        sleep(10)
        app.buttons["Download"].firstMatch.tap()
        sleep(20)
    }


    func testHotspot(){
        //----start the hotspot before running this test
        let app = XCUIApplication()
        app.launch()
        XCUIApplication().alerts["Lex-Hotspot"].buttons["Stay here"].tap()
        app.buttons["openRapButton"].tap()
        app.webViews.staticTexts["Agile and Devops"].tap()
        //        Agile and Devops
        
    }
    
    func testNoNetworkAvailableAndDownloadsAccessible() {
        let app = XCUIApplication()
      
        app.launch()
        XCUIApplication().alerts["Lex-Hotspot"].buttons["Stay here"].tap()
        
        //----For simulators turn the wifi off to begin with
        //----For physical device uncomment the below code:
//        let settingsApp = XCUIApplication(bundleIdentifier: "com.apple.Preferences")
//        settingsApp.launch()
//
//        settingsApp.tables.cells["Airplane Mode"].tap()
        //app.activate()
        app.buttons["Yes"].tap()
        app.buttons["lex home"].tap()
        app.staticTexts["Network Disconnected"].tap()
        app.buttons["Okay"].tap()
    }
    

}
