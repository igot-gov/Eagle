//
//  tocTests.swift
//  LexUITests
//
//  Created by LexUser on 11/12/18.
//  Copyright © 2018 Infosys. All rights reserved.
//

import XCTest

class tocTests: XCTestCase {

    override func setUp() {
        // UI tests must launch the application that they test. Doing this in setup will make sure it happens for each test method.
//        let storyboard = UIStoryboard(name: "Main.storyboard", bundle: Bundle.main)
//        let controller = storyboard.instantiateViewController(withIdentifier: "segueHomeToToC")
//        UIApplication.shared.keyWindow?.rootViewController = controller

        // In UI tests it’s important to set the initial state - such as interface orientation - required for your tests before they run. The setUp method is a good place to do this.
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func testToc(){
        let app = XCUIApplication()
        app.launch()
        sleep(12)
        //----navigate to downloads manually here
        app.buttons["Courses"].tap()
        app.buttons["Modules"].tap()
        app.buttons["Resources"].tap()
        app.buttons["tocHam"].tap()
        app.staticTexts["About"].tap()
        
    }
}
