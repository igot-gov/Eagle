//
//  AudioUtil.swift
//  Lex
//
//  Created by Abhishek Gouvala on 4/24/18.
//  Copyright © 2018 Infosys. All rights reserved.
//
import AVFoundation
class AudioUtil {
    enum SoundType {
        case Notification, Alert
    }
    
    static let notificationValue = 1307
    static let alertValue = 1307
    
    static func playNotificationAudio(ofType: SoundType) {
        switch ofType {
        case SoundType.Notification:
            AudioServicesPlaySystemSound(1307)
        case SoundType.Alert:
            AudioServicesPlaySystemSound(1307)
        }
    }
}
