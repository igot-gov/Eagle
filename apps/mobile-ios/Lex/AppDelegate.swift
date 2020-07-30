//
//  AppDelegate.swift
//  Lex
//  Created by prakash.chakraborty/Shubham Singh on 3/12/18.
//  Copyright © 2018 Infosys. All rights reserved.

import UIKit
import CoreData
import UserNotifications

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    
    //timer for slow connection
    var loadingTimer : Timer!
    let loadingLabel = UILabel()
    var homeUrl = "https://lex.infosysapps.com/page/home"
    
    var minimumVersion : [Int:Int] = [5:3]
    var restrictRotation:UIInterfaceOrientationMask = .all
    
    var splashImageView:UIImageView?
    
    //Added by nagasai_govula
    static var appDelegate: AppDelegate!
    
    override init() {
        super.init()
        // Added later by nagasai_govula
        AppDelegate.appDelegate = self
        
        // Added to initialize the Singleton App constant depending on the environment
        // Remove this later to a synchronised block on the Singleton's construnctor
        let _ = Singleton()
        
        DownloadedDataService.deleteExpiredResourcesInBackground()
        PlayerFunctions.copyTocPlayer()
        PlayerFunctions.copyMediaPlayer()
        PlayerFunctions.copyMobileApps()
    }
    
    //method to show loading
    func showWaiting() {
        loadingLabel.frame = SnackBarUtil.getSnackBarLabelFrame()
        // This will align the label to the center

        loadingLabel.center.x = (window?.center.x)!
        // loadingLabel.backgroundColor = UIColor.white.withAlphaComponent(0.8)
        loadingLabel.backgroundColor = AppConstants.lexBrandColor.withAlphaComponent(0.8)
        loadingLabel.layer.borderColor = AppConstants.lexBrandColor.cgColor
        loadingLabel.layer.borderWidth = 1
        loadingLabel.textColor = UIColor.white
        loadingLabel.textAlignment = .center
        loadingLabel.font = UIFont.systemFont(ofSize: 15.0)
        loadingLabel.text = AppConstants.loadingText
        loadingLabel.alpha = 1.0
        loadingLabel.layer.cornerRadius = 10;
        loadingLabel.clipsToBounds  =  true
        self.splashImageView!.addSubview(loadingLabel)
        loadingLabel.startBlink()
    }
    
    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        print(url, "open url", url.query);
        let split = url.absoluteString.split(separator: "/")
        print("Splitter", split[1])
        
//        print(url, "open url", url.query);
//               let split = url.absoluteString.split(separator: "/")
//               print("Splitter", split[1])
//
//               if (url.absoluteString.contains("LexCustomScheme")){
//                   Singleton.universalLinkClicked = true
//                   Singleton.universalLink = Singleton.appConfigConstants.appUrl + "/training"

        
        if (url.absoluteString.contains("LexCustomScheme")){
                Singleton.universalLinkClicked = true
                Singleton.universalLink = Singleton.appConfigConstants.appUrl + "/training"
            
        } else if (url.absoluteString.lowercased().contains("InfyMeContent".lowercased())) {
            if var fetchedId = url.query {
                fetchedId = fetchedId.replacingOccurrences(of: "id=", with: "")
                print("The fetched ID", fetchedId)
                if(fetchedId == "home"){
                    Singleton.universalLinkClicked = true
                    Singleton.universalLink = homeUrl
                }
                else{
                    Singleton.universalLinkClicked = true
                    Singleton.universalLink = "\(Singleton.appConfigConstants.appUrl)/app/toc/\(fetchedId)/overview"
                    print("APp link", Singleton.universalLink)
                }
                
            }
        }
        print(url.absoluteString)
        return true
    }
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
//        let langCultureCode: String = "en"
//        let defaults = UserDefaults.standard
//        defaults.set([langCultureCode], forKey: "AppleLanguages")
//        defaults.synchronize()
        
        
        WiFiUtil.netConnectionCheck(){
            isConnected in
            if isConnected == true{
                print("connected to normal internet connection");
            }
            else {
                WiFiUtil.openRapSuccess() {
                    isConnected in
                    print("Lex-Hotspot Connection:",isConnected)
                    if isConnected == true {
                        let appDelegate = UIApplication.shared.delegate as! AppDelegate
                        appDelegate.stopAnimation()
                        WiFiUtil.result = true
                        Singleton.sendUserForOpenRap = true
                    }
                }
            }
            
        }
                
        // adding observer for device rotation
//        NotificationCenter.default.addObserver(self, selector: <#T##Selector#>, name: <#T##NSNotification.Name?#>, object: <#T##Any?#>)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.deviceRotated), name: UIDevice.orientationDidChangeNotification, object: nil)
        // Override point for customization after application launch.
        splashImageView = UIImageView()
        splashImageView?.loadGif(name: "Lex")
        splashImageView?.contentMode = .center
        splashImageView?.frame = CGRect(x: 0, y: 0, width: (window?.frame.width)!, height: (window?.frame.height)!)
        //splashImageView?.backgroundColor = UIColor.init(red: 63, green: 81, blue: 181, alpha: 0.9)
        splashImageView?.backgroundColor = UIColor(red:0.25, green:0.32, blue:0.71, alpha:1.0)
        window!.addSubview(splashImageView!)
        window!.makeKeyAndVisible()
        let uuid = NSUUID().uuidString
        Singleton.sessionID = uuid
        //Adding splash Image as UIWindow's subview.
        window!.bringSubviewToFront(window!.subviews[0])
        
        self.showWaiting()
        
        
        //timer shows slow network connection
        loadingTimer = Timer.scheduledTimer(withTimeInterval: 15, repeats: false, block: { (void) in
            print("Snackbar called for slow connection")
            SnackBarUtil.addSnackBarLabel(webview:self.splashImageView!,message: AppConstants.slowConnection)
        })
        
        return true
    }
    
    //for orientation changes in Loading Screen
    @objc func deviceRotated() {
        if UIDevice.current.orientation.isLandscape {
            // print("Portrait")
            loadingLabel.frame = SnackBarUtil.getSnackBarLabelFrame()
            splashImageView?.frame = CGRect(x: 0, y: 0, width: (window?.frame.width)!, height: (window?.frame.height)!)
        }
        
        if UIDevice.current.orientation.isPortrait {
            //            print("Landscape")
            loadingLabel.frame = SnackBarUtil.getSnackBarLabelFrame()
            splashImageView?.frame = CGRect(x: 0, y: 0, width: (window?.frame.width)!, height: (window?.frame.height)!)
        }
        
    }
    
    func stopAnimation() {
        if self.splashImageView != nil {
            self.splashImageView?.alpha = 0.0
            self.splashImageView!.removeFromSuperview()
        }
        self.window!.rootViewController!.view.transform = CGAffineTransform.identity
        self.window?.makeKeyAndVisible()
    }
    
    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
        Singleton.tempCounter = ""
    }
    
    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
        Singleton.tempCounter = ""
        print("BACKGROUND")
    }
    
    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
        Singleton.tempCounter = ""
        //        })
    }
    
    func application(_ application: UIApplication, supportedInterfaceOrientationsFor window: UIWindow?) -> UIInterfaceOrientationMask
    {
        return self.restrictRotation
    }
    
    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
        application.applicationIconBadgeNumber = 0
        
        // When app is coming to foreground. Setting a variable that the app came to foreground and the respective actions like auth/internet check... will be done using this variable
        
        // Sending the notification for the app to load check for the Notification again
        // Define identifier
        /*let notificationName = NSNotification.Name("LoggedInCheck")
         NotificationCenter.default.post(name: notificationName, object: nil, userInfo: ["type": ""])*/
        // Post notification
        Singleton.didComeFromForeground = true
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
        // Saves changes in the application's managed object context before the application terminates.
        self.saveContext()
    }
    
    
    func application(_ application: UIApplication, continue userActivity: NSUserActivity, restorationHandler: @escaping ([UIUserActivityRestoring]?) -> Void) -> Bool {
        print("Continue User Activity called: ")
        if userActivity.activityType == NSUserActivityTypeBrowsingWeb {
            let url = userActivity.webpageURL!
            Singleton.universalLinkClicked = true
            print(url.absoluteString)
            Singleton.universalLink = url.absoluteString
            //handle url and open whatever page you want to open.
        }
        return true
    }
    
    
    //Stops Third party Keyboard
    func application(application: UIApplication, shouldAllowExtensionPointIdentifier extensionPointIdentifier: String) -> Bool
    {
        if (extensionPointIdentifier == UIApplication.ExtensionPointIdentifier.keyboard.rawValue) {
            return false
        }
        return true
    }
    
    
    // MARK: - Core Data stack
    
    lazy var persistentContainer: NSPersistentContainer = {
        /*
         The persistent container for the application. This implementation
         creates and returns a container, having loaded the store for the
         application to it. This property is optional since there are legitimate
         error conditions that could cause the creation of the store to fail.
         */
        let container = NSPersistentContainer(name: "Lex")
        let storeBefore = container.persistentStoreCoordinator.persistentStores
        let dbPath: String = "myDb.sqlite"
        let options: [AnyHashable: Any] = [
                  NSPersistentStoreFileProtectionKey: FileProtectionType.completeUntilFirstUserAuthentication
              ]
         let newStoreUrl = documentDirectory.appendingPathComponent("myDb.sqlite")
         try! container.persistentStoreCoordinator.addPersistentStore(ofType: NSSQLiteStoreType, configurationName: nil, at: newStoreUrl, options: options)
        
        if (FileManager.default.fileExists(atPath: newStoreUrl.absoluteString)) {
            print("new store cretaed")
            let storeAfter = container.persistentStoreCoordinator.persistentStores
             let temp = container.persistentStoreCoordinator.metadata(for: storeAfter.first!)
        }
       
//
//        container.loadPersistentStores(completionHandler: { (storeDescription, error) in
//            if let error = error as NSError? {
//                // Replace this implementation with code to handle the error appropriately.
//                // fatalError() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
//
//                /*
//                 Typical reasons for an error here include:
//                 * The parent directory does not exist, cannot be created, or disallows writing.
//                 * The persistent store is not accessible, due to permissions or data protection when the device is locked.
//                 * The device is out of space.
//                 * The store could not be migrated to the current model version.
//                 Check the error message to determine what the actual problem was.
//                 */
//                fatalError("Unresolved error \(error), \(error.userInfo)")
//            }
//
//
//        })
      

//        if(!store.isEmpty){
//            let newStoreUrl = documentDirectory.appendingPathComponent("myDb1.sqlite")
//
//            if(try! container.persistentStoreCoordinator.migratePersistentStore(store.first!, to: newStoreUrl, options: options, withType: NSSQLiteStoreType) != nil){
//                try! container.persistentStoreCoordinator.remove(store.first!)
//            }
//
//        }
        let storeUrl = container.persistentStoreCoordinator.persistentStores
//        if((storeUrl) != nil){
//
//            //let store = URL(fileURLWithPath: (storeUrl?.relativePath.replacingOccurrences(of: "Lex.sqlite", with: ""))!)
//                        if(!store.isEmpty){
//                try! container.persistentStoreCoordinator.migratePersistentStore(store.first!, to: storeUrl!, options: options, withType: NSSQLiteStoreType)
////                  try! container.persistentStoreCoordinator.addPersistentStore(ofType: NSSQLiteStoreType, configurationName: nil, at: newStoreUrl, options: options)
//            }
//        }
        
        
       
//            // var appDir = NSSearchPathForDirectoriesInDomains(.LibraryDirectory, .UserDomainMask, true)
//        try! container.persistentStoreCoordinator.destroyPersistentStore(at: temp!, ofType: NSSQLiteStoreType, options: nil)
        
//              let storeUrl = documentDirectory.appendingPathComponent("myDb.sqlite")
//              let newURL = URL(fileURLWithPath: "myDb.sqlite")
//        let oldStore = container.persistentStoreCoordinator.persistentStore(for: temp!)
//
//              if((oldStore) != nil){
//
//              }
//

//
//        let temp = container.persistentStoreCoordinator.persistentStores
      
//
//        let url = try! documentDirectory.path.asURL()
//
//        let options: [AnyHashable: Any] = [
//            NSPersistentStoreFileProtectionKey: FileProtectionType.complete
//        ]
       
        return container
    }()
    
    
    // MARK: - Core Data Saving support
    
    func saveContext () {
        let context = persistentContainer.viewContext
//        try! persistentContainer.persistentStoreCoordinator.addPersistentStore(ofType: String, configurationName: <#T##String?#>, at: <#T##URL?#>, options: <#T##[AnyHashable : Any]?#>)
        if context.hasChanges { }
    }
    @discardableResult static func async<T>(_ block: @escaping () -> T) -> T {
        let queue = DispatchQueue.global()
        let group = DispatchGroup()
        var result: T?
        group.enter()
        queue.async(group: group) { result = block(); group.leave(); }
        group.wait()
        
        return result!
    }
    
    public static func getAppDelegate() -> AppDelegate {
        return appDelegate
    }
    
    //Added by nagasai_govula for Download Manager
    func application(_ application: UIApplication, handleEventsForBackgroundURLSession identifier: String, completionHandler: @escaping () -> Void) {
        //print("handleEventsForBackgroundURLSession: \(identifier)")
        completionHandler()
    }
}

