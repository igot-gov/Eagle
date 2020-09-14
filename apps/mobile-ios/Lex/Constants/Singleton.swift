//
//  Singleton.swift
//  Lex
//  Created by Stalin M/ Abhishek Gouvala/ Shubham Singh on 12/03/18.
//  Copyright © 2018 Infy. All rights reserved.

import Foundation
import SwiftyJSON
import UserNotifications

class Singleton : NSObject {
    
    static let appConfigConstants = AppConstants(.PROD)
    static var tempCounter : String = "" // Some bogus counter
    static var accessToken : String = ""
    static var wid: String = ""
    static var sessionID : String = ""
    static var universalLinkClicked: Bool = false
    static var universalLink: String = ""
    static var downloadedCoursesArray = [DownloadObject]()
    static var downloadedModulesArray = [DownloadObject]()
    static var downloadedResourcesArray = [String:JSON]()
    static var fileManager = FileManager.default
    static var lastToast: Date? = nil
    static var sendUserForOpenRap: Bool = false
    
    static var alert = UIAlertController(title: nil, message: "", preferredStyle: .alert)
    static var didComeFromForeground = false
    static var isLexHotspotDev = false
    static let isHomesNetworkNotifierStarted = false
    static var isOffline = false
    static var hasUserLoggedOut = false
    static var isCompleted = false
    static var snackBarShown = false
    
    static var newOfflineBeta: Bool = true
    static var quizApiResult:[String : Any] = [:]
}

class UserDetails {
    static var email : String = ""
    static var location : String = ""
    static var unit : String = ""
    static var UID : String = ""
    
    static func setUserDetails(tokenString : String) {
        let decodedString = JWTDecoder.decodePayload(tokenstr: tokenString)
        if decodedString != "" {
            do {
                let userDictionary = try JSONSerialization.jsonObject(with: decodedString.data(using: .utf8)!, options: []) as? [String:Any]
                email = userDictionary!["email"] as! String
                UID = userDictionary!["sub"] as! String
                print(UID)
                //print(userName)
            } catch _ {}
        }
    }
}

class DownloadObject {
    var content : String = ""
    var meta : MetaObject = MetaObject()
}

class MetaObject {
    var status : String = ""
    var progress : Int = 0
    var downloadInitOn : String = ""
    var downloadFinishedOn : String = ""
    var expires : String = ""
}

class JWTDecoder {
    static func decodePayload(tokenstr: String) -> String {
        //splitting JWT to extract payload
        let array = tokenstr.components(separatedBy: ".")
        if array.count > 2 {
            var base64Str = array[1]
            if base64Str.count % 4 != 0 {
                let padlen = 4 - base64Str.count % 4
                base64Str += String(repeating: "=", count: padlen)
            }
            
            if let data = Data(base64Encoded: base64Str, options: []),
                let str = String(data: data, encoding: String.Encoding.utf8) {
                return str
            }
        }
        return ""
    }
}
class RegexUtility {
    //Regex method, return an array of string of matching items
    static func matches(for regex: String, in text: String) -> [String] {
        
        do {
            let regex = try NSRegularExpression(pattern: regex)
            let results = regex.matches(in: text,
                                        range: NSRange(text.startIndex..., in: text))
            return results.map {
                String(text[Range($0.range, in: text)!])
            }
        } catch _ {
            //print("invalid regex: \(error.localizedDescription)")
            return []
        }
    }
    
    static func replaceRegex(for regex: String,in text: String) -> String {
        let baseUrl: String = Singleton.appConfigConstants.appUrl
        
        do {
            let regex = try NSRegularExpression(pattern: regex)
            let range = NSMakeRange(0, text.count)
            let results = regex.stringByReplacingMatches(in: text, options: [], range: range, withTemplate: baseUrl )
            return results
        } catch _ {
            // print("invalid regex: \(error.localizedDescription)")
        }
        return ""
    }
    
}

public class LoadingOverlay {
    
    var overlayView : UIView!
    var activityIndicator : UIActivityIndicatorView!
    
    class var shared: LoadingOverlay {
        struct Static {
            static let instance: LoadingOverlay = LoadingOverlay()
        }
        return Static.instance
    }
    
    init() {
        self.overlayView = UIView()
        self.activityIndicator = UIActivityIndicatorView()
        
        overlayView.frame = CGRect(x: 0, y: 0, width: 80, height: 80)
        overlayView.backgroundColor = .clear
        overlayView.clipsToBounds = true
        overlayView.layer.cornerRadius = 10
        overlayView.layer.zPosition = 1
        
        activityIndicator.frame = CGRect(x: 0, y: 0, width: 40, height: 40)
        activityIndicator.style = .whiteLarge
        activityIndicator.center = CGPoint(x: overlayView.bounds.width / 2, y: overlayView.bounds.height / 2)
        activityIndicator.style = .whiteLarge
        activityIndicator.color = .gray
        overlayView.addSubview(activityIndicator)
    }
    
    public func showOverlay(view: UIView) {
        overlayView.center = view.center
        view.addSubview(overlayView)
        activityIndicator.startAnimating()
    }
    
    public func hideOverlayView() {
        activityIndicator.stopAnimating()
        overlayView.removeFromSuperview()
    }
}
class PlayerFunctions {
    
    static let fileManager = Singleton.fileManager
    
    static func copyTocPlayer(){
        let oldPlayerPath = Bundle.main.bundleURL.appendingPathComponent("Players/ToC")
        let filePath =  documentDirectory.appendingPathComponent("ToC_Player")
        if !fileManager.fileExists(atPath: filePath.path) {
            do {
                try fileManager.copyItem(at: oldPlayerPath, to: filePath)
            } catch _ {
                Singleton.tempCounter = "0"
            }
        } else {
            do {
                try fileManager.removeItem(at: filePath)
                try fileManager.copyItem(at: oldPlayerPath, to: filePath)
            } catch _ {
                Singleton.tempCounter = "0"
            }
        }
    }
    static func copyMediaPlayer() {
        let oldPlayerPath = Bundle.main.bundleURL.appendingPathComponent("Players/Media")
        let filePath =  documentDirectory.appendingPathComponent("Media_Player")
        if !fileManager.fileExists(atPath: filePath.path) {
            do {
                try fileManager.copyItem(at: oldPlayerPath, to: filePath)
            } catch _ {
                Singleton.tempCounter = "0"
            }
        } else {
            do {
                try fileManager.removeItem(at: filePath)
                try fileManager.copyItem(at: oldPlayerPath, to: filePath)
            } catch _ {
                Singleton.tempCounter = "0"
            }
        }
    }
    
    static func copyMobileApps() {
        let oldPlayerPath = Bundle.main.bundleURL.appendingPathComponent("Players/mobile-apps")
        let filePath =  documentDirectory.appendingPathComponent("mobile_apps")
        if !fileManager.fileExists(atPath: filePath.path) {
            do {
                try fileManager.copyItem(at: oldPlayerPath, to: filePath)
            } catch _ {
                Singleton.tempCounter = "0"
            }
        } else {
            do {
                try fileManager.removeItem(at: filePath)
                try fileManager.copyItem(at: oldPlayerPath, to: filePath)
            } catch _ {
                Singleton.tempCounter = "0"
            }
        }
    }
    
}
