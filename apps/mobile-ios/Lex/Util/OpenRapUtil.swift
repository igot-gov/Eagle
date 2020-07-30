//
//  SnackBarUtil.swift
//  Lex
//
//  Created by Shubham Singh on 8/10/18.
//  Copyright © 2018 Infosys. All rights reserved.
//

import UIKit

class OpenRapUtil {
    
    static var hotspotButton: UIButton?
    
    static let width:CGFloat = 50
    static let height:CGFloat = 50
    static let padding:CGFloat = 10
    static let iconPadding:CGFloat = 10
    
    static func createHotSpotButton() {
        
        if OpenRapUtil.hotspotButton == nil {
            hotspotButton = UIButton()
            let hotspotButtonFrame = CGRect(x: UIScreen.main.bounds.width - padding - width, y: UIScreen.main.bounds.height - width - (padding*15), width: width, height: width)
            
            hotspotButton?.frame = hotspotButtonFrame
            let hotspotImage = UIImage.fontAwesomeIcon(name: .wifi, textColor: UIColor.white, size: CGSize(width: iconPadding*3, height: iconPadding*3))
            hotspotButton?.setImage(hotspotImage, for: .normal)
            let tintColor = AppConstants.primaryTheme.replacingOccurrences(of: "#", with: "")
            hotspotButton?.backgroundColor = UIColor.fromHex(rgbValue: UInt32(String(tintColor), radix: 16)!, alpha: 1.0)
            hotspotButton?.layer.cornerRadius = 0.5 * (hotspotButton?.bounds.size.width)!
            hotspotButton?.clipsToBounds = true
        }    }
}
