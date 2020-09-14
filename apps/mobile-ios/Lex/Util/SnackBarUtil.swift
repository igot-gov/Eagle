//
//  SnackBarUtil.swift
//  Lex
//
//  Created by Shubham Singh on 8/10/18.
//  Copyright © 2018 Infosys. All rights reserved.
//

import UIKit

class SnackBarUtil {

    static let snackBarLabel = UILabel()

    static let snackbarHeight = 35
    static var downloadWebview = 155
    static let iphoneXHeight = 800
    static let downloadWebviewX = 182
    
    static func getSnackBarLabelFrame() -> CGRect {
        let xPos = CGFloat(0)
        let yPos = UIScreen.main.bounds.height-CGFloat(snackbarHeight)
        let snackBarLabelWidth = UIScreen.main.bounds.width
        let snackBarLabelHeight = CGFloat(snackbarHeight)
        return CGRect(x: xPos, y: yPos-snackBarLabelHeight, width: snackBarLabelWidth, height: snackBarLabelHeight)
    }
    
    static func getSnackBarLabelFrameForToc() -> CGRect {
        let xPos = CGFloat(0)
        let yPos = UIScreen.main.bounds.height-CGFloat(snackbarHeight)
        let snackBarLabelWidth = UIScreen.main.bounds.width
        let snackBarLabelHeight = CGFloat(snackbarHeight)
        return CGRect(x: xPos, y: yPos, width: snackBarLabelWidth, height: snackBarLabelHeight)
    }
    
    static func addSnackBarLabel(webview : UIView,message : String){
        let snackBarLabelFrame = getSnackBarLabelFrame()
        SnackBarUtil.snackBarLabel.text = message
        SnackBarUtil.snackBarLabel.textAlignment = .center
        SnackBarUtil.snackBarLabel.frame = snackBarLabelFrame
        SnackBarUtil.snackBarLabel.frame.origin.y = UIScreen.main.bounds.height
        SnackBarUtil.snackBarLabel.backgroundColor = UIColor.black.withAlphaComponent(0.8)
        
        SnackBarUtil.snackBarLabel.textColor = UIColor.white
        webview.insertSubview(snackBarLabel, aboveSubview: webview)
        UIView.animate(withDuration: 0.5, delay: 0.1, options: .curveEaseOut, animations: {
            // Hiding the label when the animation is complete
            SnackBarUtil.snackBarLabel.frame.origin.y = UIScreen.main.bounds.height-CGFloat(self.snackbarHeight)
        }, completion: {(isCompleted) in
            // Removing the toast message from the view controller once the animation is complete
//            print("Completed")
        })
    }
    
    static func createSnackBarForDownloads(webview : UIView,message : String){
        let snackBarLabelFrame = getSnackBarLabelFrame()
        SnackBarUtil.snackBarLabel.text = message
        SnackBarUtil.snackBarLabel.textAlignment = .center
        SnackBarUtil.snackBarLabel.frame = snackBarLabelFrame
        SnackBarUtil.snackBarLabel.frame.origin.y = UIScreen.main.bounds.height
        SnackBarUtil.snackBarLabel.backgroundColor = UIColor.black.withAlphaComponent(0.8)
//        print("check it ", UIScreen.main.bounds.height)
        SnackBarUtil.snackBarLabel.textColor = UIColor.white
        webview.insertSubview(snackBarLabel, aboveSubview: webview)
        UIView.animate(withDuration: 0.5, delay: 0.1, options: .curveEaseOut, animations: {
            // Hiding the label when the animation is complete
            if(UIScreen.main.bounds.height > CGFloat(self.iphoneXHeight)){
                self.downloadWebview = downloadWebviewX
            }
            SnackBarUtil.snackBarLabel.frame.origin.y = UIScreen.main.bounds.height-CGFloat(self.downloadWebview)
        }, completion: {(isCompleted) in
            // Removing the toast message from the view controller once the animation is complete
            //            print("Completed")
        })
    }
    
    static func createSnackBarForOffline(webview : UIView,message : String){
        let snackBarLabelFrame = getSnackBarLabelFrame()
        SnackBarUtil.snackBarLabel.text = message
        SnackBarUtil.snackBarLabel.textAlignment = .center
        SnackBarUtil.snackBarLabel.frame = snackBarLabelFrame
        SnackBarUtil.snackBarLabel.frame.origin.y = UIScreen.main.bounds.height
        SnackBarUtil.snackBarLabel.backgroundColor = UIColor.black.withAlphaComponent(0.8)
        //      print("check it ", UIScreen.main.bounds.height)
        SnackBarUtil.snackBarLabel.textColor = UIColor.white
        webview.insertSubview(snackBarLabel, aboveSubview: webview)
        UIView.animate(withDuration: 0.5, delay: 0.1, options: .curveEaseOut, animations: {
            // Hiding the label when the animation is complete
            if(UIScreen.main.bounds.height > CGFloat(self.iphoneXHeight)){
                self.downloadWebview = downloadWebviewX
            }
            SnackBarUtil.snackBarLabel.frame.origin.y = UIScreen.main.bounds.height-CGFloat(90)
        }, completion: {(isCompleted) in
            // Removing the toast message from the view controller once the animation is complete
            //            print("Completed")
        })
    }
    static func removeSnackBarLabel() {
        snackBarLabel.removeFromSuperview()
    }
}
