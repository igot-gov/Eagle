//  NetworkFunctions.swift
//  Lex
//  Created by Abhishek Gouvala on 7/12/18.
//  Copyright © 2018 Infosys. All rights reserved.

class NetworkFunctions {
    static let hostPrefixWhitelist : [String] = ["lex","yammer","microsoftonline","login.", "iap","www.interaction-design.org"]
    static func whiteListChecker(hostToCheck: String) -> Bool {
        for item in hostPrefixWhitelist {
            if hostToCheck.contains(item) {
                return true
            }
        }
        return false
    }
}
