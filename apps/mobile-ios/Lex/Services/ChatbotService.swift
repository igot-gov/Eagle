//  ChatbotService.swift
//  Lex
//  Created by Shubham Singh on 4/24/18.
//  Copyright © 2018 Infosys. All rights reserved.

import UIKit
import WebKit

class ChatbotService: NSObject {
    
    //    static let webV = UIWebView()
    static let webView = WKWebView()
    static let closeButton = UIButton()
    static let micButton = UIButton()
    static let keyboardTextField = UITextField()
    static let sendButton = UIButton()
    static let listeningLabel = UILabel()
    
    static let chatbotButton = UIButton()
    static let width:CGFloat = 50
    static let height:CGFloat = 50
    static let padding:CGFloat = 10
    
    static var refAdded = false
    static var cacheDirectory = Singleton.fileManager.urls(for: FileManager.SearchPathDirectory.cachesDirectory, in: .userDomainMask).first!
    
    static let chatbotEndPoint = "chat-bot"
    static let chatBotUrlParams = "mic=0&keyboard=0&speech=1" + "&ts=" + String(describing: DateUtil.getUnixTimeFromDate())
    //    static let chatBotUrlParams = "mic=0&keyboard=0&speech=1" + "&ts=" + String(describing: DateUtil.getTimeStampString(inputDate: Date.init()))
    static let chatBotUrlStr = Singleton.appConfigConstants.appUrl + "/" + chatbotEndPoint + "?" + chatBotUrlParams
    
    
    static func loadChatbotWebView(xPos: CGFloat, yPos: CGFloat) {
        print("Chatbot url: ", chatBotUrlStr)
        
        // Adding this to the process pool
        WebViewService.addWebViewToProcessPool(webView: webView)
        
        if let chatbotURL = URL(string: chatBotUrlStr) {
            let chatbotUrlRequest = URLRequest(url: chatbotURL)
            print("Checking for chatboturl")
            print(cacheDirectory)
            print(WKWebsiteDataStore.allWebsiteDataTypes())
            let defaults = UserDefaults.standard
            
            let currentAppVersion = Bundle.main.object(forInfoDictionaryKey: "CFBundleVersion") as! String
//            let previousVersion = defaults.string(forKey: "appVersion")
//            if previousVersion == nil {
//                // first launch
//                defaults.set(currentAppVersion, forKey: "appVersion")
//                defaults.synchronize()
//            } else if previousVersion == currentAppVersion {
//                // same version
//            } else {
//                // other version
//                defaults.set(currentAppVersion, forKey: "appVersion")
//                defaults.synchronize()
//                removeCacheData()
//            }
            
            webView.load(chatbotUrlRequest)
            webView.frame  = getWebViewFrame()
            webView.scrollView.minimumZoomScale = 1.0
            webView.scrollView.maximumZoomScale = 5.0
            
            print("width ", UIScreen.main.bounds.width)
            print("height ", UIScreen.main.bounds.height)
            
            // Add the chatbot button
            let chatbotButtonFrame = CGRect(x: xPos - padding - width, y: yPos - width - (yPos/10), width: width, height: width)
            
            chatbotButton.frame = chatbotButtonFrame
            let commentsImg = UIImage.fontAwesomeIcon(name: .commentingO, textColor: UIColor.white, size: CGSize(width: padding*3, height: padding*3))
            chatbotButton.setImage(commentsImg, for: .normal)
            
            let themeColor = AppConstants.primaryTheme.replacingOccurrences(of: "#", with: "")
            
            
            
            chatbotButton.backgroundColor = UIColor.fromHex(rgbValue: UInt32(String(themeColor),radix: 16)!,alpha: 1.0)
            chatbotButton.layer.cornerRadius = 0.5 * chatbotButton.bounds.size.width
            chatbotButton.clipsToBounds = true
        }
        chatbotButton.accessibilityIdentifier = "chatbotButton"
    }
    
    static func getWebViewFrame() -> CGRect {
        return CGRect(x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height-ChatbotService.height-padding*2)
    }
    
    private static var speakerEnabled = true
    static func toggleSpeaker() {
        speakerEnabled = !speakerEnabled
    }
    static func isSpeakerEnabled() -> Bool {
        return speakerEnabled
    }
    
    static func removeCacheData() {
        let dataStore = WKWebsiteDataStore.default()
        
        do{
            let directoryContents = try FileManager.default.contentsOfDirectory(at: cacheDirectory.appendingPathComponent("com.infosys.lex"), includingPropertiesForKeys: nil, options: [])
            for items in directoryContents{
                try? FileManager.default.removeItem(at: items)
            }
            print("Check Directory")
            
            
            print(directoryContents)
        }catch let error as NSError {
            print(error)
        }
        
//
//        let webSiteFilter: Set = [WKWebsiteDataTypeServiceWorkerRegistrations,WKWebsiteDataTypeSessionStorage,WKWebsiteDataTypeOfflineWebApplicationCache,WKWebsiteDataTypeDiskCache]
//
//        dataStore.fetchDataRecords(ofTypes: WKWebsiteDataStore.allWebsiteDataTypes()) { records in
//            print("Check the type of records!")
//            print(records)
//            dataStore.removeData(ofTypes: webSiteFilter,
//                                 for: records.filter { $0.displayName.contains("infosysapps.com") },
//                                 completionHandler: {})
//        }
        // Delete any associated cookies
        if let cookies = HTTPCookieStorage.shared.cookies {
            for cookie in cookies {
                HTTPCookieStorage.shared.deleteCookie(cookie)
            }
        }
        
    }
}
