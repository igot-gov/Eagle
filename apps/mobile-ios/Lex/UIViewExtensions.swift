//
//  UIViewExtensions.swift
//  Lex
//
//  Created by Shubham Singh on 12/18/19.
//  Copyright © 2019 Infosys. All rights reserved.
//

import Foundation
import UIKit


extension UIView {
    func showBlurLoader(){
        let blurEffect = UIBlurEffect(style: UIBlurEffect.Style.dark)
        let blurEffectView = UIVisualEffectView(effect: blurEffect)
        blurEffectView.frame = self.bounds
        blurEffectView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        
        let activityIndicator = UIActivityIndicatorView(style: .whiteLarge)
        activityIndicator.frame = CGRect(x: 0, y: 0, width: 50, height: 50)
        activityIndicator.startAnimating()
        
        blurEffectView.contentView.addSubview(activityIndicator)
        activityIndicator.center = blurEffectView.contentView.center
        
        self.addSubview(blurEffectView)
    }
    
    func removeBluerLoader(){
        self.subviews.compactMap {  $0 as? UIVisualEffectView }.forEach {
            $0.removeFromSuperview()
        }
    }
}


extension UIViewController {
    // Func to extend the toast message to all views
    func showToast(message : String, force: Bool = false) {
        
        var labelExists = false
        for view in self.view.subviews {
            if type(of: view) == UILabel.self {
                labelExists = true
                break
            }
        }
        
        var shouldShowToast = false
        if (!labelExists) || force {
            if Singleton.lastToast==nil {
                Singleton.lastToast = Date()
                shouldShowToast = true
            } else if force || DateUtil.getMinutesDifference(date1: Date(), date2: Singleton.lastToast!)>0.5 {
                shouldShowToast = true
            }
        }
        
        if shouldShowToast {
            loadToast(message: message)
        }
    }
    
    func loadToast(message: String) {
        // Creating the toast label
        let toastLabel = UILabel()
        // Formatting the label to look like a toast
        toastLabel.alpha = 1.0
        toastLabel.backgroundColor = UIColor(rgb: 0x545454)
        toastLabel.textColor = UIColor.white // White text
        toastLabel.text = message
        toastLabel.textAlignment = .center
        
        toastLabel.layer.cornerRadius = 18 // border radius, usually half the height
        toastLabel.clipsToBounds  =  true
        
        toastLabel.sizeToFit()
        toastLabel.adjustsFontSizeToFitWidth = true
        //        toastLabel.lineBreakMode = NSLineBreakMode.byWordWrapping
        
        // Creating the rectangle where the text will sit
        var contentWidth = toastLabel.frame.width + 30 // 15 padding on both sides
        
        let labelHeight = 36
        var xPos = (self.view.frame.size.width - contentWidth)/2
        if contentWidth>self.view.frame.size.width {
            toastLabel.frame = self.view.frame
            
            toastLabel.layer.cornerRadius = CGFloat(labelHeight/2)
            xPos = 5
            contentWidth = toastLabel.frame.width - (xPos*2)
            toastLabel.text = "  " + message + "  "
        }
        
        //        print("XPOS: \(xPos)")
        let rect = CGRect(x: xPos, y: self.view.frame.size.height-70, width: contentWidth, height: CGFloat(labelHeight))
        
        // Adding the frame to the rect
        toastLabel.frame = rect
        
        // Adding the lable to the ViewController
        self.view.addSubview(toastLabel)
        
        // Adding the notification sound for toast message
        // Commenting this out, Can be used later
        // AudioUtil.playNotificationAudio(ofType: .Alert)
        
        // Adding the animation to go transparent after some time.
        UIView.animate(withDuration: 3, delay: 2, options: .curveEaseOut, animations: {
            // Hiding the label when the animation is complete
            toastLabel.alpha = 0.0
        }, completion: {(isCompleted) in
            // Removing the toast message from the view controller once the animation is complete
            toastLabel.removeFromSuperview()
            Singleton.lastToast = Date()
        })
    }
    
    func showAlert(title: String?, message: String?) {
        Singleton.alert.title = title
        Singleton.alert.message = message
        Singleton.alert.view.tintColor = UIColor.black
        let loadingIndicator: UIActivityIndicatorView = UIActivityIndicatorView(frame: CGRect(x: 10, y: 5, width: 50, height: 50)) as UIActivityIndicatorView
        loadingIndicator.hidesWhenStopped = true
        loadingIndicator.style = UIActivityIndicatorView.Style.gray
        loadingIndicator.startAnimating();
        
        Singleton.alert.view.addSubview(loadingIndicator)
        present(Singleton.alert, animated: true, completion: nil)
    }
    
    func hideAlert() {
        Singleton.alert.view.removeFromSuperview()
    }
}
