//  AccessToken.swift
//  Lex
//  Created by Shubham Singh on 11/27/18.
//  Copyright © 2018 Infosys. All rights reserved.

import Foundation

class AccessTokenService {
    public static func getAccessToken(fromPersistance: Bool = false) -> String {
        return ""
        // If persistance, always get it from CoreData
        // Get the access token from the app data if available. Else get it from the CoreData and set it at app level as well
    }
    public static func setAccessToken(token: String, insertIntoPersistance: Bool = false) {
        // Set the access token on the core data, if force is enabled, else set it at app level only
    }
}
