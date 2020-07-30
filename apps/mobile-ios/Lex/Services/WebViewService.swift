//  WebViewService.swift
//  Lex
//  Created by Shubham Singh on 7/18/18.
//  Copyright © 2018 Infosys. All rights reserved.

import WebKit

class WebViewService {
    static let webViewProcessPool: WKProcessPool = WKProcessPool()
    
    static func addWebViewToProcessPool(webView: WKWebView!) {
        if webView != nil {
            if !Thread.isMainThread {
                DispatchQueue.main.async {
                    webView.configuration.processPool = WebViewService.webViewProcessPool
                }
            } else {
                webView.configuration.processPool = WebViewService.webViewProcessPool
            }
            
        }
    }
}
