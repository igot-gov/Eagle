//  DownloadManager.swift
//  Lex
//  Created by Abhishek Gouvala/ Shubham Singh on 3/24/18.
//  Copyright © 2018 Infosys. All rights reserved.


import Foundation
import CoreData
import SSZipArchive
import CryptoSwift
import RNCryptor

/*
 This class will perform a background download and will take care of the percentage of the download and the number of times this has been failed and success reports. This class has a dependancy of having a core data entity named "DownloadPersistance". The status and the details of the background downloads will be saved in this core data entity in a background thread of core data module. Hence there is a delay in which the downloads appear to the user. The thread which updates the core data entity for the Lex resource is comparatively low priority thread and hence the notification will be sent upon the download success.
 
 Imporovements are needed to change all the completion handler to stay as a stand alone method, so that the code stays clutter free and brewity of it is maintained.
 */

class DownloadManager : NSObject, URLSessionDelegate, URLSessionDownloadDelegate {
    
    static var shared = DownloadManager()
    
    typealias ProgressHandler = (Float) -> ()
    
    var onProgress : ProgressHandler? {
        didSet {
            if onProgress != nil {
                let _ = activate()
            }
        }
    }
    
    // It is a best practice to have only one session identifier for an app as recommended by Apple. Though the download works on creating many sessions with same identifier, apple says that it does not guarantee the behaviour of the session. Hence we make sure that only one session exists for our app and initiate it in the constructor, making it a static variable.
    private static let config = URLSessionConfiguration.background(withIdentifier: "\(Bundle.main.bundleIdentifier!).background")
    private static var session: URLSession? = nil
    
    override private init() {
        super.init()
        addConfigAndSession()
    }
    
    private func addConfigAndSession() {
        DownloadManager.config.httpAdditionalHeaders = ["Authorization": "Bearer \(Singleton.accessToken)"]
        if (DownloadManager.session == nil) {
            DownloadManager.session = URLSession(configuration: DownloadManager.config, delegate: self, delegateQueue: OperationQueue())
        }
    }
    
    
    // This method will initiate the session if it does not exist and then returns the session. Since the session is a static var, the same session will be used until the class is in the memory
    func activate() -> URLSession {
        // Warning: If an URLSession still exists from a previous download, it doesn't create a new URLSession object but returns the existing one with the old delegate object attached!
        if (DownloadManager.session == nil) {
            addConfigAndSession()
        }
        return DownloadManager.session!
    }
    
    private func calculateProgress(session : URLSession, completionHandler : @escaping (Float) -> ()) {
        session.getTasksWithCompletionHandler { (tasks, uploads, downloads) in
            let progress = downloads.map({ (task) -> Float in
                if task.countOfBytesExpectedToReceive > 0 {
                    return Float(task.countOfBytesReceived) / Float(task.countOfBytesExpectedToReceive)
                } else {
                    return 0.0
                }
            })
            completionHandler(progress.reduce(0.0, +))
        }
    }
    
    func MD5(string: String) -> Data {
        let messageData = string.data(using:.utf16LittleEndian)!
        var digestData = Data(count: Int(CC_MD5_DIGEST_LENGTH))
        
        _ = digestData.withUnsafeMutableBytes {digestBytes in
            messageData.withUnsafeBytes {messageBytes in
                CC_MD5(messageBytes, CC_LONG(messageData.count), digestBytes)
            }
        }
        
        return digestData
    }
    
    //METHOD FOR DECRYPTING THE .LEX FILE
    private func decryptOpenrapFile(filePath: String, outputDestination: String) {
        //        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "showToast/\(AppConstants.contentDownloadError)/force"])
        
        let keyString: String = AppConstants.openrapDecryptionKey
        print("input path",filePath)
        print("input file",outputDestination)
        
        
        if FileManager.default.fileExists(atPath: filePath) {
            let Ids = filePath.split(separator: "/")
            let contentId = String(Ids[Ids.count-1]).replacingOccurrences(of: ".lex", with: "")
            
            print(" File exists ",contentId)
            let file: FileHandle? = FileHandle(forReadingAtPath: (filePath))
            
            if file != nil {
                // Read all the data
                let encryptedData = file?.readDataToEndOfFile()
                
                let passwordForFile = keyString
                
                var digestPassword: [UInt8] = []
                digestPassword = Array(MD5(string: passwordForFile))
                
                _ = [UInt8](repeating: 0, count: 16)
                
                let decrypted = try! AES(key: digestPassword, blockMode: ECB(), padding: .pkcs7).decrypt([UInt8](encryptedData!))
                let decryptedData = Data(decrypted)
                
                
                let stringData =  String(bytes: decryptedData.bytes, encoding: .ascii)
                
                
                let newFileUrl =  documentDirectory.appendingPathComponent("sample.zip")
                let newFileUrl1 =  documentDirectory.appendingPathComponent("sample1.zip")
                //writing
                do {
                    try decryptedData.write(to: newFileUrl, options: .atomic)
                    
                    try stringData?.write(to: newFileUrl1, atomically: true, encoding: .utf8)
                    
                    SSZipArchive.unzipFile(atPath: newFileUrl.path, toDestination: documentDirectory.path)
                    
                    print("Path is", documentDirectory.path)
                    print("downloaded")
                    DownloadService.updateArtifact(lexId: contentId, filePath: documentDirectory)
                    
                }
                catch let error as NSError {
                    print(error.localizedDescription)
                }
            }
            
        } else {
            print(" File does not exists")
        }
    }
    
    
    func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask, didWriteData bytesWritten: Int64, totalBytesWritten: Int64, totalBytesExpectedToWrite: Int64) {
        
        if totalBytesExpectedToWrite > 0 {
            //            if let onProgress = onProgress {
            //                calculateProgress(session: session, completionHandler: onProgress)
            //            }
            _ = Float(totalBytesWritten) / Float(totalBytesExpectedToWrite)
            //            print("\n-----------\nProgress \(downloadTask) \(progress)")
        }
    }
    static var count = 0
    func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask, didFinishDownloadingTo location: URL) {
        if let sessionId = session.configuration.identifier {
            
            //            print("\n\n*****************************URL downloaded: \(downloadTask.originalRequest?.url)")
            //            DownloadManager.count = DownloadManager.count + 1
            //            print("\n\n*****************************Items downloaded: \(DownloadManager.count)")
            
            // Getting the domain name to check if the content is downloaded from open rap
            var splittedUrl = downloadTask.originalRequest?.url?.absoluteString.components(separatedBy: "/")
            // 0 - "http | https" 1 - ":" 2 - domain name
            //print("Domain name", String(describing: splittedUrl![1]))
            let domainName = splittedUrl![1]
            var lexId = splittedUrl![(splittedUrl?.count)!-3].replacingOccurrences(of: ".lex", with: "")
            if (!lexId.hasPrefix("lex")) {
                var counter = 0
                for url in splittedUrl! {
                    if(url.contains("%252F")){
                        splittedUrl![counter] = url.replacingOccurrences(of: "%252F", with: "/")
                    }
                    if(url.contains("%2F")){
                        splittedUrl![counter] = url.replacingOccurrences(of: "%2F", with: "/")
                    }
                    
                    counter += 1
                }
                print("Check the spliited Url here", splittedUrl)
                var split = splittedUrl![splittedUrl!.count - 1].components(separatedBy: "/")
                print("The new split", split)
                for words in split {
                    print("The words", words)
                    if (words.hasPrefix("lex") && !words.hasSuffix("main")) {
                        lexId = words
                    }
                }
            }
            print("lexId is", lexId)
           
            
            // Will move the file to a different location and update the core data here
            let documentsDirectoryURL =  FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
            
            if domainName == "captive.openrap.com" {
                // Destination where openrap downloaded resource will be saved
                let openRapDestinationURL = documentsDirectoryURL.appendingPathComponent(lexId + ".lex")
                
                // To let the method know that it needs to follow the download process of open rap
                
                // Save the file to documents directory
                
                // Removing the file if already exists
                if FileManager.default.fileExists(atPath: openRapDestinationURL.path) {
                    try? FileManager.default.removeItem(at: openRapDestinationURL)
                }
                
                // Now copy the file from the download task's location to documents directory
                do {
                    
                    try? FileManager.default.moveItem(at: location, to: openRapDestinationURL)
                    
                }
                
                self.decryptOpenrapFile(filePath: openRapDestinationURL.path, outputDestination: documentDirectory.appendingPathComponent(lexId + ".zip").absoluteString)
                
            } else{
                // Retreving the file name, so that can save the file to a different location
                var fileName = downloadTask.originalRequest?.url?.lastPathComponent ?? ""
                if(fileName.contains("%2F")){
                    let furtherFileName = fileName.components(separatedBy: "%2F")
                    fileName = furtherFileName.last!
                    if(furtherFileName.last!.contains("?type=")){
                        
                        let splitfileName = furtherFileName.last!.components(separatedBy: "?type=")
                        fileName = splitfileName.first!
                    }
                    
                }
                
                let urlSplitted = downloadTask.originalRequest?.url?.absoluteString.split(separator: "/")
                let contentId = AppConstants.downloadLexId
                
                
                // Will move the file to a different location and update the core data here
                let documentsDirectoryURL =  FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
                
                // Creating the destination url
                // Place where the thumbail or the artifact will be stored
                let destinationUrl = documentsDirectoryURL.appendingPathComponent(contentId + "/\(fileName.trimmingCharacters(in: .whitespacesAndNewlines))")
                //            let destinationUrl = contentId + "/\(fileName.trimmingCharacters(in: .whitespacesAndNewlines))"
                
                // Moving the data of the downloaded file to the destination where it should be.
                // The security restrictions of the API deletes the file once the control goes out of the block.
                // Since we are using a sync method in this block (i.e. DownloadManager.fetch()), once the control enters the fetch method, the file at the temporary location will be lost. Hence we are saving the file outside this block. Never move this piece of code out of this place
                //            print("Temporary File saved at location: \(location)")
                // Checking if the folder where the file will sit exists or not. We are already performing this check, but there are some errors in the content where the content thumbnail id is not same ad the artifact id, hence we will check again if the directory exists or not
                
                if !FileManager.default.fileExists(atPath: documentsDirectoryURL.appendingPathComponent(contentId).path)
                {
                    //                print("Content directory for \(contentId) does not exist. Will create it now")
                    do {
                        try FileManager.default.createDirectory(atPath: documentsDirectoryURL.appendingPathComponent(contentId).path, withIntermediateDirectories: false, attributes: nil)
                    } catch let error as NSError {
                        print(error.localizedDescription);
                    }
                } /*else {
                 //                print("Content directory exists for \(contentId). Will strore the files here")
                 }*/
                if FileManager.default.fileExists(atPath: destinationUrl.path) {
                    try? FileManager.default.removeItem(at: destinationUrl)
                }
                
                try? FileManager.default.moveItem(at: location, to: destinationUrl)
                //print("File moved to documents folder at path: \(destinationUrl.path)")
                
                DownloadManager.fetch(sessionId: sessionId, taskId: downloadTask.taskIdentifier, finished: { (downloadedObj) in
                    if (downloadedObj != nil) {
                        let contentIdFromFetch = downloadedObj?.contentId ?? ""
                        
                        if contentId.lowercased() != contentIdFromFetch.lowercased() {
                            //                        print("Content id is different, for thumbnail or artifact url, item needs to be moved...")
                            
                            let newDestinationUrl = destinationUrl.path.replacingOccurrences(of: contentId, with: contentIdFromFetch)
                            let newDestinationUrlPath = URL(string: "file://" + newDestinationUrl)
                            
                            do {
                                try FileManager.default.copyItem(at: destinationUrl, to: newDestinationUrlPath!)
                                //                            print("File moved from wrong path \(destinationUrl.path) folder at path: \(newDestinationUrl)")
                                //                            print("Successfully moved to: \(String(describing: newDestinationUrl))")
                            } catch _ {
                                /*
                                 print(error.localizedDescription)
                                 print("Error moving to: \(String(describing: newDestinationUrl))")
                                 */
                            }
                            
                        }
                        let downloadedUrl = downloadTask.originalRequest?.url
                        let needsUnzipping = downloadedObj?.needsUnzipping ?? false
                        
                        var offlineArtifactUrl = ""
                        var offlineThumbnailUrl = ""
                         var errorOccured = false
                        
                        var zipFileId = ""
                        
                        if needsUnzipping && !((downloadedUrl?.absoluteString.lowercased().hasSuffix("artifacts"))!) && (((downloadedUrl?.absoluteString.lowercased().contains(".zip"))!)){
                            // The destination url has a zipped file in it. Now we have to unzip it to the same directory
                            // Unzip
                            
                            zipFileId = String(destinationUrl.path.split(separator: "/").last!).replacingOccurrences(of: ".zip", with: "")
                            
                            SSZipArchive.unzipFile(atPath: destinationUrl.path, toDestination: documentsDirectoryURL.appendingPathComponent(contentIdFromFetch).path)
                            SSZipArchive.unzipFile(atPath: destinationUrl.path, toDestination: documentsDirectoryURL.appendingPathComponent(contentIdFromFetch).path, progressHandler: nil) { (data, result, error) in
                                if let _ = error {
                                    print("error:",error!)
                                    errorOccured = true
                                    
                                }
                            }
                            
                            //removing the zip file after extraction
                            do {
                                try FileManager.default.removeItem(at: destinationUrl)
                            } catch {
                                print("Some error occured while deleting the zip file")
                            }
                            
                            // Checking if the manifest.json, OR quiz.json OR assessment.json file exists for this entry after the unzipping is done
                            do {
                                var directoryContents = try FileManager.default.contentsOfDirectory(at: documentsDirectoryURL.appendingPathComponent(contentIdFromFetch), includingPropertiesForKeys: nil, options: [])
                                // print(directoryContents)
                                
                                let checkPath = documentsDirectoryURL.appendingPathComponent(contentIdFromFetch).appendingPathComponent(zipFileId)
                                
                                
                                if FileManager.default.fileExists(atPath: checkPath.path) {
                                    print("folder")
                                    
                                    let folderContents = try FileManager.default.contentsOfDirectory(at: checkPath, includingPropertiesForKeys: nil, options: [])
                                    
                                    for items in folderContents {
                                        
                                        let lastPathComponent = items.lastPathComponent
                                        
                                        do  {
                                            try FileManager.default.moveItem(at: items, to: documentsDirectoryURL.appendingPathComponent(contentIdFromFetch).appendingPathComponent(lastPathComponent))
                                            print("File moved")
                                        } catch let error as NSError {
                                            print("Ooops! Something went wrong: \(error)")
                                        }
                                    }
                                    
                                    do {
                                        try FileManager.default.removeItem(at: checkPath)
                                    } catch {
                                        print("Some error occured while deleting the folder")
                                    }
                                    
                                    
                                }
                                
                                //reinitializing the directory content if there was any file movement in case of extra folder
                                directoryContents = try FileManager.default.contentsOfDirectory(at: documentsDirectoryURL.appendingPathComponent(contentIdFromFetch), includingPropertiesForKeys: nil, options: [])
                                
                                
                                // if you want to filter the directory contents you can do like this:
                                var jsonFiles = directoryContents.filter{ $0.pathExtension == "json" }
                                //print("json files: ",jsonFiles)
                                let htmlFiles = directoryContents.filter{ $0.pathExtension == "html"}
                                let subdirs = directoryContents.filter{ $0.hasDirectoryPath }
                                var jsonSubDir : [String] = []
                                if(subdirs.count>0){
                                    for i in subdirs{
                                       let subDirectoryContents = try FileManager.default.contentsOfDirectory(at: i, includingPropertiesForKeys: nil, options: [])
                                        let subDirJsonFiles = subDirectoryContents.filter{ $0.pathExtension == "json" }
                                        if(subDirJsonFiles.count>0){
                                            for j in subDirJsonFiles{
                                               jsonSubDir.append(i.absoluteString)
                                                jsonFiles.append(j)
                                            }
                                        }
                                        
                                    }

                                    
                                    
                                }

                                // assessment.json is for assessment, quiz.json is for quuiz and manifest.json is for html/webmodule
                                var isJsonFileUnzipped = false
                                var targetFileName = ""
                                for jsonFile in jsonFiles {
                                    if jsonFile.absoluteString.contains("manifest.json") {
                                        isJsonFileUnzipped = true
                                        targetFileName = "manifest.json"
                                        break
                                    }
                                    if jsonFile.absoluteString.contains("quiz.json") {
                                        isJsonFileUnzipped = true
                                        targetFileName = "quiz.json"
                                        break
                                    }
                                    if jsonFile.absoluteString.contains("assessment.json") {
                                        isJsonFileUnzipped = true
                                        targetFileName = "assessment.json"
                                        break
                                    }
                                }
                                for htmlFile in htmlFiles{
                                    if (htmlFile.absoluteString.contains(".html") && jsonFiles.count == 0) {
                                        isJsonFileUnzipped = true
                                        targetFileName = "index.html"
                                    }
                                }
                                
                                if isJsonFileUnzipped {
                                    if(jsonSubDir.count>0){
                                        for i in jsonSubDir{
                                            print(i)
                                            let k = URL(string:i)!.lastPathComponent
                                            
                                            let checkPath = URL(string: i + "/\(targetFileName)")
                                            if FileManager.default.fileExists(atPath: checkPath!.path){
                                                offlineArtifactUrl = "\(contentIdFromFetch)/\(k)/\(targetFileName)"
                                            }
                                            
                                        }
                                        
                                    }
                                    else{
                                        offlineArtifactUrl = "\(contentIdFromFetch)/\(targetFileName)"
                                    }
                                    
                                }

                            } catch _ {
                                //print(error.localizedDescription)
                            }
                        }
                        
                        // Only of the file name or the content id is not empty, we will go ahead
                        if fileName.count>0 && contentIdFromFetch.count>0 {
                            
                            // Checking if the content directory for this resourse, exists. If not, creating it
                            if !FileManager.default.fileExists(atPath: documentsDirectoryURL.appendingPathComponent(contentIdFromFetch).path)
                            {
                                do {
                                    try FileManager.default.createDirectory(atPath: documentsDirectoryURL.appendingPathComponent(contentIdFromFetch).path, withIntermediateDirectories: false, attributes: nil)
                                } catch _ {
                                    //print(error.localizedDescription);
                                }
                            } /*else {
                             // print("Content directory exists for \(contentId). Will strore the files here")
                             }*/
                            
                            if ((downloadTask.originalRequest?.url?.absoluteString.lowercased().contains("artifacts"))!) {
                               let newDestinationUrl = destinationUrl.path.replacingOccurrences(of: contentId, with: contentIdFromFetch)

                                var appIconDownload = URL(string:AppConstants.downloadThumbnailUrl)?.lastPathComponent.lowercased()
                                if(appIconDownload == nil ){
                                    let splittedUrl = AppConstants.downloadThumbnailUrl.components(separatedBy: "/")
                                    print(splittedUrl)
                                    appIconDownload = splittedUrl.last?.lowercased()
                                }
                                var appIconUrl = downloadTask.originalRequest!.url!.lastPathComponent.lowercased()
                                print(appIconDownload)
                                print(appIconUrl)
                                print(AppConstants.downloadThumbnailUrl)
                                appIconUrl = appIconUrl.replacingOccurrences(of: "%20", with: " ")
                                appIconDownload = appIconDownload!.replacingOccurrences(of: "%20", with: " ")
                                print(appIconDownload)
                                print(appIconUrl)
                                if(appIconDownload == appIconUrl){
                                        offlineThumbnailUrl = newDestinationUrl
                                    }
                                else{
                                    offlineArtifactUrl = newDestinationUrl
                                    }
                                
                            } else if (downloadTask.originalRequest?.url?.absoluteString.lowercased().hasSuffix("assets"))! {
                                if !needsUnzipping {
                                    offlineArtifactUrl = destinationUrl.path
                                } /*else {
                                 print("The artifact is already downloaded and manifest for the same is stored at: \(offlineArtifactUrl)")
                                 }*/
                            } else if (downloadTask.originalRequest?.url?.absoluteString.lowercased().hasSuffix(".json"))! {
                                offlineArtifactUrl = destinationUrl.path
                             
                            }
                            if (errorOccured) {
                                let newDestinationUrl = destinationUrl.path.replacingOccurrences(of: contentId, with: contentIdFromFetch)

                                
                               var appIconDownload = URL(string:AppConstants.downloadThumbnailUrl)?.lastPathComponent.lowercased()
                                var appIconUrl = downloadTask.originalRequest!.url!.lastPathComponent.lowercased()
                                appIconUrl = appIconUrl.replacingOccurrences(of: "%20", with: " ")
                                appIconDownload = appIconDownload!.replacingOccurrences(of: "%20", with: " ")
                                print(appIconDownload)
                                print(appIconUrl)
                                if(appIconDownload == appIconUrl){
                                        offlineThumbnailUrl = newDestinationUrl
                                    }
                                else{
                                    offlineArtifactUrl = newDestinationUrl
                                    }
                                //offlineArtifactUrl  = destinationUrl.path
                                print("Error occure", errorOccured, "for", destinationUrl)
                            }
                            
                            
                            let background = DispatchQueue(label:"LockingQueue")
                            
                            offlineThumbnailUrl = offlineThumbnailUrl.replacingOccurrences(of: documentsDirectoryURL.path + "/", with: "")
                            offlineArtifactUrl = offlineArtifactUrl.replacingOccurrences(of: documentsDirectoryURL.path + "/", with: "")
                            
                            print("The last thumbnail", offlineThumbnailUrl, "last artifacte", offlineArtifactUrl)
                            
                            background.sync {
                                DownloadManager.updateDownloadedStatusFor(identifier: contentIdFromFetch, offlineThumbnailUrl: offlineThumbnailUrl, offlineArtifactUrl: offlineArtifactUrl, finished: {
                                    (saved) in
                                    
                                    if (saved) {
                                        // Delete the core data entry for the task id and the session id once the download is successfull
                                        DownloadManager.deleteDownloadPersistanceRowWith(sessionId: sessionId, taskId: downloadTask.taskIdentifier, finished: {
                                            if ($0==true) {
                                                //                                        print("\n******Removed the entry from the core data of downloaded resource \(downloadTask.taskIdentifier) sessionId: \(sessionId) ******\n")
                                            }
                                        })
                                    } /*else {
                                     print("Failed saving")
                                     }*/
                                })
                            }
                        }
                        // Will update the core data that the download has finished
                        //                    print("---------------\nFile location is: \(String(describing: fileName).description)/\(contentId)\n---------------")
                    }
                })
            }
            
        }
    }
    
    func urlSession(_ session: URLSession, task: URLSessionTask, didCompleteWithError error: Error?) {
        if error != nil {
            //print("Error while downloading: session: \(String(describing: session.configuration.identifier)) task: \(task.taskIdentifier)")
            //print("Could not download: \(String(describing: task.originalRequest?.url?.absoluteString))")
            //print(error.debugDescription)
            
            //print("Making the status as error when there is a download in the artifact ot the thumbnail")
            
            let urlSplitted = task.originalRequest?.url?.absoluteString.split(separator: "/")
            
            if urlSplitted != nil {
                let contentId = String(describing: urlSplitted![(urlSplitted?.count)!-3])
                DownloadManager.updateDownloadedStatusFor(identifier: contentId, offlineThumbnailUrl: "", offlineArtifactUrl: "", isError: true, finished: {
                    (saved) in
                    
                    if (saved) {
                        // Delete the core data entry for the task id and the session id once the download is successfull
                        DownloadManager.deleteDownloadPersistanceRowWith(sessionId: session.configuration.identifier!, taskId: task.taskIdentifier, finished: {
                            if ($0==true) {
                                //                            print("\n******Removed the entry from the core data of downloaded resource \(task.taskIdentifier) sessionId: \(session.configuration.identifier!) ******\n")
                            }
                        })
                    }
                })
            }
        }
    }
    
    class DownloadPersistanceModel: NSObject {
        var contentId: String = ""
        var sessionId: String = ""
        var taskId: Int = 0
        var downloadUrl: String = ""
        var fileType: String = ""
        var needsUnzipping: Bool = false
        
        init(url: String, contentId: String, sessionId: String, taskId: Int, fileType: String, needsUnzipping: Bool) {
            self.downloadUrl = url
            self.contentId = contentId
            self.sessionId = sessionId
            self.taskId = taskId
            self.fileType = fileType
            self.needsUnzipping = needsUnzipping
        }
        
        func save() {
            
            // Getting the core data context to save the new entry
            let appDelegate = AppDelegate.appDelegate
            let context = appDelegate?.persistentContainer.newBackgroundContext()
            
            let privateContext = NSManagedObjectContext(concurrencyType: .privateQueueConcurrencyType)
            privateContext.persistentStoreCoordinator = context?.persistentStoreCoordinator
            privateContext.perform {
                // Code in here is now running "in the background" and can safely
                // do anything in privateContext.
                // This is where you will create your entities and save them.
                let entity = NSEntityDescription.entity(forEntityName: "DownloadPersistance", in: privateContext)
                let newEntry = NSManagedObject(entity: entity!, insertInto: privateContext)
                
                // Adding the data now
                newEntry.setValue(self.contentId, forKey: "contentId")
                newEntry.setValue(self.sessionId, forKey: "sessionId")
                newEntry.setValue(self.taskId, forKey: "taskId")
                newEntry.setValue(self.downloadUrl, forKey: "downloadUrl")
                newEntry.setValue(self.fileType, forKey: "fileType")
                newEntry.setValue(self.needsUnzipping, forKey: "needsUnzipping")
                
                do {
                    try privateContext.save()
                    try context?.save()
                    appDelegate?.saveContext()
                } catch _ {
                    //print(error.localizedDescription)
                }
            }
        }
    }
    
    static func fetch(sessionId: String, taskId: Int, finished: @escaping (DownloadPersistanceModel?) -> ()) {
        // Getting the core data context to save the new entry
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.newBackgroundContext()
        
        let privateContext = NSManagedObjectContext(concurrencyType: .privateQueueConcurrencyType)
        privateContext.persistentStoreCoordinator = context?.persistentStoreCoordinator
        privateContext.perform {
            // Code in here is now running "in the background" and can safely
            // do anything in privateContext.
            // This is where you will create your entities and save them.
            
            // Adding the data now
            let request = NSFetchRequest<NSFetchRequestResult>(entityName: "DownloadPersistance")
            request.predicate = NSPredicate(format: "sessionId == %@ AND taskId == %d", sessionId, Int32(taskId))
            request.returnsObjectsAsFaults = false
            do {
                let result = try privateContext.fetch(request)
                
                let results = result as! [NSManagedObject]
                
                if (results.count>0) {
                    let data = (results)[0]
                    
                    let contentId = data.value(forKeyPath: "contentId") as! String
                    let sessionId = data.value(forKey: "sessionId") as! String
                    let taskId = data.value(forKey: "taskId") as! Int
                    let downloadUrl = data.value(forKey: "downloadUrl") as! String
                    let fileType = data.value(forKeyPath: "fileType") as! String
                    let needsUnzipping = data.value(forKey: "needsUnzipping") as! Bool
                    
                    finished(DownloadManager.DownloadPersistanceModel(url: downloadUrl, contentId: contentId, sessionId: sessionId, taskId: taskId, fileType: fileType, needsUnzipping: needsUnzipping))
                } else {
                    finished(nil)
                }
            } catch {
                //print("Failed")
                finished(nil)
            }
        }
        // return DownloadPersistanceModel(url: "Test", downloadPath: "Test", contentId: "Test", sessionId: "Test", taskId: 0, fileType: "") //savedDic[taskId]!
    }
    
    static func deleteDownloadPersistanceRowWith(sessionId: String, taskId: Int, finished: @escaping (Bool) -> ()) {
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.newBackgroundContext()
        
        let privateContext = NSManagedObjectContext(concurrencyType: .privateQueueConcurrencyType)
        privateContext.persistentStoreCoordinator = context?.persistentStoreCoordinator
        privateContext.perform {
            // Adding the data now
            let request = NSFetchRequest<NSFetchRequestResult>(entityName: "DownloadPersistance")
            request.predicate = NSPredicate(format: "taskId == %d AND sessionId == %@", taskId, sessionId)
            request.returnsObjectsAsFaults = false
            
            do {
                let result = try privateContext.fetch(request)
                let rows = result as! [NSManagedObject]
                
                if (rows.count>0) {
                    for row in rows {
                        privateContext.delete(row)
                    }
                    try privateContext.save();
                    try context?.save()
                    appDelegate?.saveContext()
                }
                finished(true)
            } catch _ {
                //print(error.localizedDescription)
            }
            finished(false)
        }
    }
    static func updateDownloadedStatusFor(identifier: String, offlineThumbnailUrl: String, offlineArtifactUrl: String, isError: Bool = false, finished: @escaping (Bool) -> ()) {
        let appDelegate = AppDelegate.appDelegate
        let context = appDelegate?.persistentContainer.newBackgroundContext()
        
        let background = DispatchQueue(label:"LockingQueue")
        
        background.sync {
            let privateContext = NSManagedObjectContext(concurrencyType: .mainQueueConcurrencyType)
            //            let privateContext = NSManagedObjectContext(concurrencyType: .privateQueueConcurrencyType)
            privateContext.persistentStoreCoordinator = context?.persistentStoreCoordinator
            privateContext.perform {
                // This will happen in the background
                // Updating the data now
                
                let uuid = UserDetails.UID
                
                let request = NSFetchRequest<NSFetchRequestResult>(entityName: AppConstants.lexResourceV2EntityName)
                request.predicate = NSPredicate(format: "content_id == %@ AND userUuid == %@", identifier,uuid)
                request.returnsObjectsAsFaults = false
                do {
                    let result = try privateContext.fetch(request)
                    
                    let rows = result as! [NSManagedObject]
                    
                    if rows.count>0 {
                        let rowToUpdate = rows[0]
                        
                        let identifier:String = rowToUpdate.value(forKeyPath: "content_id") as! String
                        let contentType: String = rowToUpdate.value(forKeyPath: "content_type") as! String
                        let json: Data = rowToUpdate.value(forKeyPath: "json") as! Data
                        let dateOne: Date = rowToUpdate.value(forKeyPath: "dateOne") as! Date
                        let dateTwo: Date = rowToUpdate.value(forKeyPath: "dateTwo") as! Date
                        let modifiedDate: Date = Date()
                        let expiryDate: Date = DateUtil.addDaysToDate(inputDate: Date(), noOfDays: AppConstants.contentExpiryInDays)
                        let requestedDate: Date = rowToUpdate.value(forKeyPath: "requested_date") as! Date
                        let downloadAttempts: Int = 0
                        let integerOne: Int = 0
                        let integerTwo: Int = 0
                        let percentComplete: Int = 0
                        let requestedByUser: Bool = rowToUpdate.value(forKey: "requestedByUser") as! Bool
                        let status: String = rowToUpdate.value(forKeyPath: "status") as! String
                        var stringOne: String = rowToUpdate.value(forKeyPath: "stringOne") as! String
                        var stringTwo: String = rowToUpdate.value(forKeyPath: "stringTwo") as! String
                        
                        if offlineThumbnailUrl.count>0 {
                            stringOne = offlineThumbnailUrl
                        }
                        if offlineArtifactUrl.count>0 {
                            stringTwo = offlineArtifactUrl
                        }
                        // Creating the core data model here
                        let coreDataObj = CoreDataService.CoreDataModel(identifier: identifier, contentType: contentType, artifactUrl: stringTwo, thumbnailUrl: stringOne, json: json, requestedByUser: requestedByUser, status: status, percentComplete: percentComplete)
                        coreDataObj.dateOne = dateOne
                        coreDataObj.dateTwo = dateTwo
                        coreDataObj.modifiedDate = modifiedDate
                        coreDataObj.expiryDate = expiryDate
                        coreDataObj.requestedDate = requestedDate
                        coreDataObj.downloadAttempts = downloadAttempts
                        coreDataObj.integerOne = integerOne
                        coreDataObj.integerTwo = integerTwo
                        coreDataObj.requestedByUser = requestedByUser
                        coreDataObj.percentComplete = percentComplete
                        coreDataObj.email = uuid
                        
                        // If error, updating the sattus as error
                        if isError {
                            if (coreDataObj.status != "CANCELLED"){
                                coreDataObj.status = "FAILED"
                            }
                        } else {
                            coreDataObj.status = (stringOne.count>0 && !stringOne.hasPrefix("http") &&  !stringOne.hasPrefix("https") && stringTwo.count>0 && !stringTwo.hasPrefix("http") && !stringTwo.hasPrefix("https")) ? "DOWNLOADED" : status
                        }
                        
                        coreDataObj.stringOne = stringOne
                        coreDataObj.stringTwo = stringTwo
                        
                        /*if stringOne.count>0 && stringTwo.count>0 && contentType.lowercased()=="resource"{
                         print("Downloaded: \(identifier)")
                         }
                         */
                        if (coreDataObj.status.lowercased()=="downloaded") {
                            coreDataObj.percentComplete = 100
                            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "resourceDownloaded"), object: nil, userInfo: ["type": "resourceDownloaded"])
                        }
                        
                        // Deleting the existing data from the core data
                        privateContext.delete(rowToUpdate)
                        
                        try privateContext.save()
                        try context?.save()
                        appDelegate?.saveContext()
                        
                        // Saving the updated core data row
                        let entity = NSEntityDescription.entity(forEntityName: AppConstants.lexResourceV2EntityName, in: privateContext)
                        let newEntry = NSManagedObject(entity: entity!, insertInto: privateContext)
                        
                        // Adding data values to the entries
                        newEntry.setValue(coreDataObj.identifier, forKey: "content_id")
                        newEntry.setValue(coreDataObj.contentType, forKey: "content_type")
                        newEntry.setValue(coreDataObj.json, forKey: "json")
                        newEntry.setValue(coreDataObj.binaryOne, forKey: "binaryOne")
                        newEntry.setValue(coreDataObj.dateOne, forKey: "dateOne")
                        newEntry.setValue(coreDataObj.dateTwo, forKey: "dateTwo")
                        newEntry.setValue(coreDataObj.downloadAttempts, forKey: "download_attempt")
                        newEntry.setValue(coreDataObj.expiryDate, forKey: "expiry_date")
                        newEntry.setValue(coreDataObj.integerOne, forKey: "integerOne")
                        newEntry.setValue(coreDataObj.integerTwo, forKey: "integerTwo")
                        newEntry.setValue(coreDataObj.modifiedDate, forKey: "modified_date")
                        newEntry.setValue(coreDataObj.percentComplete, forKey: "percent_complete")
                        newEntry.setValue(coreDataObj.requestedDate, forKey: "requested_date")
                        newEntry.setValue(coreDataObj.requestedByUser, forKey: "requestedByUser")
                        newEntry.setValue(coreDataObj.status, forKey: "status")
                        newEntry.setValue(coreDataObj.stringOne, forKey: "stringOne")
                        newEntry.setValue(coreDataObj.stringTwo, forKey: "stringTwo")
                        newEntry.setValue(coreDataObj.telemetryData, forKey: "telemetry_data")
                        newEntry.setValue(coreDataObj.email, forKey: "userUuid")
                        
                        // Saving the data into the core data
                        do {
                            try privateContext.save();
                            
                            try context?.save()
                            appDelegate?.saveContext()
                            
                            // Data in the core data is updated now. Now we can call the completion handler
                            finished(true)
                        } catch _ {
                            //print(error.localizedDescription)
                            //print("Failed saving")
                            finished(false)
                        }
                    }
                } catch _ {
                    //print(error.localizedFailureReason!)
                    //print(error.localizedDescription)
                    //print("Failed")
                    finished(false)
                }
            }
        }
    }
}


