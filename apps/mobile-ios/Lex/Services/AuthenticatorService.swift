//  Authenticator.swift
//  Lex
//  Created by Shubham Singh on 4/24/18.
//  Copyright © 2018 Infosys. All rights reserved.

import LocalAuthentication
import UIKit

class AuthenticatorService {
    enum AuthOptions {
        case NA, SUCCESS, FAIL, ERROR, NOT_ENROLLED
    }
    static func authWithBiometrics(finished: @escaping (AuthOptions) -> ()) {
        let myContext = LAContext()
        myContext.localizedFallbackTitle = "Use Passcode"
        let myLocalizedReasonString = "Authentication for Infosys Lex\n(More than 3 failures will ask for passcode)"
        
        var authError: NSError?
        if #available(iOS 8.0, macOS 10.12.1, *) {
            if myContext.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &authError) {
                
                if myContext.biometryType == .touchID {
                    myContext.evaluatePolicy(.deviceOwnerAuthentication, localizedReason: myLocalizedReasonString) { success, evaluateError in
                        if success {
                            // User authenticated successfully, take appropriate action
                            finished(AuthOptions.SUCCESS)
                        } else {
                            // User did not authenticate successfully, look at error and take appropriate action
                            if evaluateError?.localizedDescription == "Canceled by user." {
                                // We will have to exit the app here, since the user has clicked on home when asked for Authentication. This is suspicious activity and the app will be closed
                                exit(0)
                            }
                            //                        finished(AuthOptions.FAIL)
                        }
                    }
                } else if myContext.biometryType == .faceID {
                    myContext.evaluatePolicy(.deviceOwnerAuthentication, localizedReason: myLocalizedReasonString) { success, evaluateError in
                        if success {
                            // User authenticated successfully, take appropriate action
                            finished(AuthOptions.SUCCESS)
                        } else {
                            // User did not authenticate successfully, look at error and take appropriate action
                            if evaluateError?.localizedDescription == "Canceled by user." {
                                // We will have to exit the app here, since the user has clicked on home when asked for Authentication. This is suspicious activity and the app will be closed
                                exit(0)
                            }
                            //                        finished(AuthOptions.FAIL)
                        }
                    }
                }
                
            } else {
                // Could not evaluate policy; look at authError and present an appropriate message to user
                print(authError ?? "")
                
                if authError?.code == -7 {
                    finished(AuthOptions.NOT_ENROLLED)
                } else {
                    finished(AuthOptions.ERROR)
                }
            }
        } else {
            // Fallback on earlier version
            finished(AuthOptions.NA)
        }
    }
}
