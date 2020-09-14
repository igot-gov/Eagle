//
//  NetworkUtil.swift
//  Lex
//
//  Created by Abhishek Gouvala on 4/18/18.
//  Copyright © 2018 Infosys. All rights reserved.
//

import Foundation
import Reachability

class NetworkUtil {
    enum NetworkType {
        case Cellular, Wifi, None
    }
    
    private static var networkType:NetworkType = .None
    
    public static func setNetworkType(type: NetworkType) {
        NetworkUtil.networkType = type
    }
    public static func getNetworkType() -> NetworkType {
        return networkType
    }
}
