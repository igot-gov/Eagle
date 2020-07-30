//
//  JsonUtil.swift
//  Lex
//
//  Created by Abhishek Gouvala on 3/20/18.
//  Copyright © 2018 Infosys. All rights reserved.
//

import Foundation
import SwiftyJSON

class JsonUtil: NSObject {
    static func convertJsonFromJsonString(inputJson: String) -> JSON? {
        do {
            return try JSON(data: inputJson.data(using: String.Encoding(rawValue: String.Encoding.utf8.rawValue))!)
        } catch let error as NSError
        {
            print(error)
        }
        return nil
    }
}
