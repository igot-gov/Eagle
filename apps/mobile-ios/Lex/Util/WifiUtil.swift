//
//  WiFiUtil.swift
//  Lex
//
//  Created by Shubham Singh on 8/16/18.
//  Copyright © 2018 Infosys. All rights reserved.
//

import SystemConfiguration.CaptiveNetwork
import Foundation
import Alamofire

class WiFiUtil {
    
    //method to get ssid of the WIFI to which the device is currently connected
    static func getWiFiSsid() -> String? {
        
        if Singleton.isLexHotspotDev{
            return AppConstants.openRapWifiSsid
        }
        var ssid: String?
        if let interfaces = CNCopySupportedInterfaces() as NSArray? {
            for interface in interfaces {
                if let interfaceInfo = CNCopyCurrentNetworkInfo(interface as! CFString) as NSDictionary? {
                    ssid = interfaceInfo[kCNNetworkInfoKeySSID as String] as? String
                    break
                }
            }
        }
        //        print("SSID: ", ssid)
        return ssid
    }
    static var result : Bool = false
    static func openRapSuccess(completionHandler: @escaping (_ isConnected: Bool) -> ()){
//        let urlString = AppConstants.openRapUrl
//        
//        Alamofire.request(urlString, method: .get).responseString
//            {response in
//                print("The response is",response)
//                
//                switch response.result
//                {
//                case .success(_):
//                    completionHandler(true)
//                case .failure(_):
//                    completionHandler(false)
//                }
//        }
    }
    
    static func netConnectionCheck(completionHandler: @escaping (_ isConnected: Bool) -> ()){
        let urlString = AppConstants.googleUrl
        
        Alamofire.request(urlString, method: .get).responseString
            {response in
                
                switch response.result
                {
                case .success(_):
                    completionHandler(true)
                case .failure(_):
                    completionHandler(false)
                }
        }
    }
}
