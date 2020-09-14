//  AppConstants.swift
//  Lex
//  Created by Abhishek Gouvala/ Shubham Singh on 4/14/18.
//  Copyright © 2018 Infosys. All rights reserved.


import Foundation
import UIKit
class AppConstants {
    // Setting the default app properties for dev
    var appUrl = "https://led1.infosysapps.com"
    var internalIp = ""
    var environment = env.DEV.name()
    var homeUrl = "https://lex.infosysapps.com/page/home"
    
    static let lastLoggedInKey = "lastLoggedIn"
    static let contentExpiryInDays = 30
    static let maxOfflineUseInDays = 7
    static let openrapDecryptionKey = "12403724379428733330"
   //static let openRapUrl = ""
    
    static let googleUrl = "https://google.com/"
    static let SdkResourceEntityName = "SdkResource"
    static let SdkResourceWithTypeEntityName = "SdkResourceWithType"
    
    static var primaryTheme: String = "#3f51b5"
    static var primaryName: String = "teal-theme"
    
    static var chatBotExternal = false
    static var downloadLexId = ""
    static var downloadThumbnailUrl = ""
    static var downloadArtifactUrl = ""
    static var isExternalview = false
    
    
    // Titles
    static let catalogTitleText = "CATALOG"
    static let brandTitleText = "BRAND"
    static let infyTVText = "INFYTV"
    static var isUserLoggedOut = false
    
    static let lexResourceEntityName = "LexResource"
    static let lexResourceV2EntityName = "LexResourceV2"
    
    // Message
    static let assessmentOnlyOfflineMsg = "Assessments can only be taken in online mode. Please connect to internet and try again."
    static let featureOnlyAvailableOnline = "Uh Oh, this feature requires internet. Would you like to go to Downloads instead?"
    static let inOfflineMode = "You are in offline mode. Please try again when internet is available."
    static let inOpenRapMode = "You are currently connected to Lex Hotspot, please connect to the Internet."
    static let lexOfflineDownloadsAccessCondition = "Lex allows access to offline downloaded content for a maximum \(AppConstants.maxOfflineUseInDays) days after your last login.\nPlease connect to the internet and login again."
    static let continueOnCellular = "Continue download on cellular data?"
    static let contentDownloadedGotoDownloads = "Content downloaded. To view it, switch to Downloads under Apps"
    static let contentExpiringSoon = "You have content which is going to be expired soon. Do you wish to extend the expiry of all downloaded content?"
    static let confirmDelete = "You are about to remove this resource. Do you wish to continue?"
    static let confirmDeleteBelongsToParent = "This resource is part of another downloaded content. Deleting this will delete it from all places. Do you wish to continue?"
    static let contentNotDownloadedYet = "Resource not downloaded yet"
    static let contentDownloadError = "Error while loading the content"
    static let fetchDetailsError = "Error while fetching the details of this content"
    static let submitDetailsError = "Error while submiting the details of this content.Please try again later"
    static let allOnlineContent = "This is all-online content"
    static let containsExternal = "Contains external content. Please download each resource individually"
    static let externalContentLoadError = "Error while loading this content. Please try again after sometime"
    static let externalContentOnlineCondition = "Internet connectivity is required to access this content. Please connect to the internet to access this resource."
    static let externalNavigationRestriction = "Navigation out of this resource is restricted"
    static let chatbotNotLoaded = "Our chatbot service is unable to load now. Please try again later."
    
    static let noConnection = "No Internet Connection!"
    static let slowConnection = "Seems like your connection is slow"
    static let openRapWifiSsid = "Lex-Hotspot"
    static let openRapLaunchPageUrl = ""
    static let openRapDownloaded = "Downloading finished go to downloads to view content"
    static let openRapLoadingErrorMessage = "It seems like Lex hot spot is unavailable right now. Please try again later"
    static let openRapCoreDataKeyName = "open-rap-key"
    static let offlinePlayerNavigationMessage = "Feature not available in downloads mode"
    static let alreadyDownloading = "This content is already being downloaded"
    static let openInBrowserText = "This artifact requires browser support. Do come back after completion."
    static let onlineAssesments = "Taking you online for this assessment"
    static let updateApp = "Your current app version is outdated, please update from the app store."
    static let updateAppUX = "Please update the app for a better user experience."
    static let loadingText = "Loading..."
    static let migratingDataText = "Please wait while we migrate your data..."
    static let apiCallFailed = "It seems that the server is down, please try again later."
    
    static let downloadNotAllowed = "You cannot download this content."
    
    
    // Colors
    static let lexBrandColor = UIColor(red:0.25, green:0.32, blue:0.71, alpha:1.0)
    
    // Environment
    enum env: String {
        case DEV, STAG, PROD
        
        func name() -> String {
            return self.rawValue
        }
    }
    
    // Downloads Environments
    enum downloadType: String {
        case OPEN_RAP, DEFAULT
        
        func name() -> String {
            return self.rawValue.lowercased()
        }
    }
    
    init(_ environment: env) {
        switch environment {
        case .DEV:
            //            self.appUrl = "https://uon.onwingspan.com"
            self.appUrl = "https://lex-dev.infosysapps.com"
            self.internalIp = ""
            self.environment = env.DEV.name()
            break;
        case .STAG:
            self.appUrl = "https://lex-staging.infosysapps.com"
            self.internalIp = ""
            self.environment = env.STAG.name()
        case .PROD:
            self.appUrl = "https://lex.infosysapps.com"
            self.internalIp = ""
            self.environment = env.PROD.name()
        }
    }
}
