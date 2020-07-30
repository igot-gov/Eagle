//  ContentModel.swift
//  Lex
//  Created by Abhishek Gouvala / Shubham Singh on 3/19/18.
//  Copyright © 2018 Infosys. All rights reserved.

import Foundation
public class ContentModel: NSObject {
    var identifier: String
    var thumbnailURL: String
    var artifactURL: String?
    var contentType: String
    var resourceJSON: String
    var userInitiated: Bool
    var requestedDate: Date
    var expiryDate: Date
    var status: String
    var percentComplete: Int
    
    init(identifier: String, thumbnailURL: String, artifactURL: String, contentType: String, resourceJSON: String, userInitiated: Bool, requestedDate: Date, expiryDate: Date, status: String, percentComplete: Int) {
        self.identifier = identifier
        self.thumbnailURL = thumbnailURL
        self.artifactURL = artifactURL
        self.contentType = contentType
        self.resourceJSON = resourceJSON
        self.userInitiated = userInitiated
        self.requestedDate = requestedDate
        self.expiryDate = expiryDate
        self.status = status
        self.percentComplete = percentComplete
    }
    
    public override var description: String {
        return "{\n\tidentifier: \(identifier)\n\tthumbnailURL: \(thumbnailURL)\n\tartifactURL: \(String(describing: artifactURL))\n\tcontentType: \(contentType)\n\tresourceJSON: \(resourceJSON)}\n"
    }
}
