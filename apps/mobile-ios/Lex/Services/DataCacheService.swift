//  DataCacheService.swift
//  Lex
//  Created by Abhishek Gouvala / Shubham Singh on 4/18/18.
//  Copyright © 2018 Infosys. All rights reserved.

import Foundation
import SwiftyJSON

class DataCacheService {
    enum ContentType: String {
        case Course, Collection, Resource
        
        func name() -> String {
            return self.rawValue
        }
    }
    
    private static var courses: [[String:JSON]] = []
    private static var modules: [[String:JSON]] = []
    private static var resources: [[String:JSON]] = []
    private static var toc: [String:JSON] = [:]
    
    static var isUpdatingCourse = false
    static var isUpdatingModule = false
    static var isUpdatingResource = false
    
    static func getDownloadedContent(contentType: ContentType) -> [[String:JSON]] {
        switch contentType {
        case .Course:
            return DataCacheService.courses
        case .Collection:
            return DataCacheService.modules
        case .Resource:
            return DataCacheService.resources
        }
    }
    
    // This method will updated the toc for any resource, but we are right now, caching only the course level data
    static func getTocOfAResource(contentId: String) -> JSON? {
        if let val = DataCacheService.toc[contentId] {
            return val;
        } else {
            return nil
        }
    }
    
    // This method is mainly invoked when there is any downloaded content. The background thread will check what the data is and update the relative values in the core data and the same is sent back to the UI which shows the list
    static func updateContentInBackground() {
        // print("Came into the content in background")
        /*updateContentInBackground(contentType: .Resource)
         updateContentInBackground(contentType: .Collection)
         updateContentInBackground(contentType: .Course)
         */
        // print("OUT OF Came into the content in background")
    }
    
    static func updateContentInBackground(contentType: ContentType) {
        switch contentType {
        case .Course:
            DispatchQueue.global(qos: .background).async {
                isUpdatingCourse = true
                DataCacheService.courses = DownloadedDataService.getDownloads(type: .Course)
                isUpdatingCourse = false
            }
        case .Collection:
            DispatchQueue.global(qos: .background).async {
                isUpdatingModule = true
                DataCacheService.modules = DownloadedDataService.getDownloads(type: .Collection)
                isUpdatingModule = false
            }
        case .Resource:
            DispatchQueue.global(qos: .background).async {
                isUpdatingResource = true
                DataCacheService.resources = DownloadedDataService.getDownloads(type: .Resource)
                isUpdatingResource = false
            }
        }
    }
}
