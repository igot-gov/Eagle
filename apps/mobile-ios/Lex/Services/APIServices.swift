//  APIServices.swift
//  Lex
//  Created by Shubham Singh on 3/22/18.
//  Copyright © 2018 Infosys. All rights reserved.

import Foundation
import Alamofire
import SwiftyJSON

class APIServices: NSObject {
    
    private static var httpsBase = Singleton.appConfigConstants.appUrl
    private static let apiBase = "apis/protected/v8/content"
    private static let hierarchyEndpoint = "?hierarchyType=detail"
    private static let wTokenEndpoint = "apis/protected/v8/user/details/wtoken"
    
    public static func getHierarchy(contentId: String, finished: @escaping (JSON) -> () ){
        // This will make an API call and then get the data for hierarchy of an API
        let accessTokenForAPI = "Bearer \(Singleton.accessToken)"
        
        let headers: HTTPHeaders = [ "Authorization": accessTokenForAPI,"Accept": "application/json", "rootorg": "Infosys", "org": "Infosys Ltd", "locale": "en", "wid": Singleton.wid]
        Alamofire.request(
            "\(httpsBase)/\(apiBase)/\(contentId)/\(hierarchyEndpoint)",
            method: .post,
            parameters: nil,
            encoding: JSONEncoding.default,
            headers: headers
        ).responseJSON
            {
                response in
                print("The hierarchy response", response)
                switch response.result
                {
                case .success:
                    //print("Successfully got the hierarchy data for: \(contentId).")
                    if response.data != nil {
                        // Reading the response from the API call as a JSON object
                        let apiCallResponseJson = try? JSON(data: response.data!)
                        finished(apiCallResponseJson!)
                    }
                case .failure( _):
                    //print("Error while getting the hierarchy data for content id: \(contentId)")
                    //print(error.localizedDescription)
                    finished(JSON.null)
                }
        }
    }
    
    
    public static func getWToken(finished: @escaping (String?) -> ()) {
        let accessTokenForAPI = "bearer \(Singleton.accessToken)"
        let headers: HTTPHeaders = [ "Authorization": accessTokenForAPI,"Accept": "application/json", "rootorg": "Infosys", "org": "Infosys Ltd", "locale": "en"]
        print("\(Singleton.appConfigConstants.appUrl)/\(wTokenEndpoint)")
        Alamofire.request("\(Singleton.appConfigConstants.appUrl)/\(wTokenEndpoint)", method: .get, parameters: nil, encoding: JSONEncoding.default, headers: headers).responseString {
            response in
            switch response.result
            {
            case .success(let data):
                //print("Successfully got the hierarchy data for: \(contentId).")
                print("Api call success", data)
                if let convertedJson = JsonUtil.convertJsonFromJsonString(inputJson: data) {
                    let repData: JSON =  convertedJson
                    Singleton.wid = repData["user"]["wid"].stringValue
                    finished(Singleton.wid)
                }
                
            case .failure(let error as NSError):
                print("Error on calling wtoken API", error)
                finished("")
            }
        }
    }
    
    //Gets Trending Learning Items
    public static func fetchHomePageData(endpoint: String, finished: @escaping (Data) -> () ){
        let accessTokenForAPI = "Bearer \(Singleton.accessToken)"
        let headers: HTTPHeaders = [ "Authorization": accessTokenForAPI,"Accept": "application/json"]
        Alamofire.request(
            endpoint,
            method: .get,
            encoding: JSONEncoding.default,
            headers: headers
        ).responseJSON
            {
                response in
                
                switch response.result
                {
                case .success:
                    if response.data != nil {
                        // Reading the response from the API call as a JSON object
                        finished(response.data!)
                    }
                case .failure( _):
                    finished(Data())
                }
        }
    }
}
