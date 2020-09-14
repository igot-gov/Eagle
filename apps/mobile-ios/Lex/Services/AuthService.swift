//  AuthService.swift
//  Lex
//  Created by Shubham Singh on 3/22/18.
//  Copyright © 2019 Infosys. All rights reserved.

import Foundation
import LocalAuthentication


class AuthService {
    
    enum AuthOptions {
        case NA, SUCCESS, FAIL, ERROR, NOT_ENROLLED
    }
    
    static func authWithBiometrics(finished: @escaping (AuthOptions) -> ()) {
        let localAuthenticationContext = LAContext()
        localAuthenticationContext.localizedFallbackTitle = "Use Passcode"
        let myLocalizedReasonString = "Authentication for Infosys Lex\n(More than 3 failures will ask for passcode)"
        
        var authError: NSError?
        if #available(iOS 8.0, macOS 10.12.1, *) {
            if (localAuthenticationContext.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &authError)) {
                localAuthenticationContext.evaluatePolicy(.deviceOwnerAuthentication, localizedReason: "Log in to access Lex") { (success, error) in
                    print("The result of auth", success, error)
                }
            }
        }
    }
}
