//
//  DateUtil.swift
//  Lex
//
//  Created by Abhishek Gouvala on 3/24/18.
//  Copyright © 2018 Infosys. All rights reserved.
//

import Foundation

class DateUtil: NSObject {
    static func getUnixTimeFromDate(input: Date = Date()) -> Int {
        return Int(input.timeIntervalSince1970*1000)
    }
    
    static func addDaysToDate(inputDate: Date, noOfDays: Int) -> Date {
        return Calendar.current.date(byAdding: .day, value: noOfDays, to: inputDate)!
    }
    
    static func getMinutesDifference(date1: Date, date2: Date) -> Double {
        let difference = date1.timeIntervalSince(date2)
        return (difference / 60.0).truncatingRemainder(dividingBy: 60.0)
    }
    
    static func getDateDifferenceInDays(date1: Date, date2: Date) -> Double {
        let difference = date1.timeIntervalSince(date2)
        let differenceInDays = Double(difference/(60 * 60 * 24 ))
        print(differenceInDays)
        return differenceInDays
    }
    //Converts date to RFC 3339 String, used to send JSON
    static func getTimeStampString(inputDate: Date) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
        return dateFormatter.string(from: inputDate)
    }
}

