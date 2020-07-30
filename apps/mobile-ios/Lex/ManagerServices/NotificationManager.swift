//  NotificationManager.swift
//  Lex
//  Created by Shubham Singh on 3/21/18.
//  Copyright © 2018 Infosys. All rights reserved.

import Foundation

class NotificationManager: NSObject {
    static var isEventsListened = false
    static func listenToAllEvents() -> Void {
        NotificationCenter.default.addObserver(DownloadService.self, selector: #selector(DownloadService.receiveNotification(obj:)), name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil)
        isEventsListened = true
    }
}
