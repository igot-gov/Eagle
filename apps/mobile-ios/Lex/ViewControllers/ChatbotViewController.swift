//  ChatbotViewController.swift
//  Lex
//  Created by Shubham Singh on 3/12/18.
//  Copyright © 2019 Infosys. All rights reserved.

import UIKit
import FontAwesome_swift
import AVFoundation
import Speech
import WebKit
import SwiftyJSON

class ChatbotViewController: UIViewController, WKNavigationDelegate, WKScriptMessageHandler, UITextFieldDelegate, SFSpeechRecognizerDelegate {
    // Variables for the Chat bot View
    let speechSynthesizer = AVSpeechSynthesizer()
    var chatBotLoadTimer: Timer!
    var currentExternalURL = ""
    var failCount = 0
    
    let tintColor = AppConstants.primaryTheme.replacingOccurrences(of: "#", with: "")
    
    let webView = ChatbotService.webView
    
    let closeButton = UIButton()
    let micButton = UIButton()
    let keyboardTextField = UITextField()
    let sendButton = UIButton()
    let speakerButton = UIButton()
    let listeningLabel = UILabel()
    let newUIView = UIView()
    var chatBotLoaded = false
    
    // Constants for all over views
    private static let padding = CGFloat(5)
    private static let lineHeight = 50
    private static var lexColor = UIColor(red:0.25, green:0.32, blue:0.71, alpha:1.0)
    private static let micButtonWidth = CGFloat(ChatbotViewController.lineHeight)
    private static let micButtonHeight = CGFloat(ChatbotViewController.lineHeight)
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        if(chatBotLoadTimer == nil){
            chatBotLoadTimer = Timer.scheduledTimer(withTimeInterval: 1, repeats: true, block: { (Void)
                in
                self.performChatbotLoadedCheck()
            })
        }
        let tintColor = AppConstants.primaryTheme.replacingOccurrences(of: "#", with: "")
        ChatbotViewController.lexColor = UIColor.fromHex(rgbValue: UInt32(String(tintColor), radix: 16)!, alpha: 1.0)
        
        
        listeningLabel.accessibilityIdentifier = "listeningLabel"
        closeButton.accessibilityIdentifier = "chatbotCloseButton"
        keyboardTextField.accessibilityIdentifier = "keyboardTextField"
        sendButton.accessibilityIdentifier = "sendButton"
        micButton.accessibilityIdentifier = "micButton"
        
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillChange(notification:)), name: UIResponder.keyboardWillChangeFrameNotification, object: nil)
        
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide),name: UIResponder.keyboardWillHideNotification, object: nil)
        
        //adding notification for device rotation
        NotificationCenter.default.addObserver(self, selector: #selector(self.deviceRotated), name: UIDevice.orientationDidChangeNotification, object: nil)
        
        // Native part of the web view
        newUIView.frame = getNativePartFrame()
        newUIView.backgroundColor = UIColor.clear
        newUIView.contentMode = .top
        webView.frame = getWebViewFrame()
        
        // Adding the close button and other functionalities for web view
        addCloseButton(webView: webView, closeButton: closeButton)
        
        // Adding the mic and send button for native frame
        addKeyboardTextField(uiView: newUIView, keyboardTextField: keyboardTextField)
        addSendButton(uiView: newUIView, sendButton: sendButton)
        addMicButton(uiView: newUIView, micButton: micButton)
        addSpeakerButton(uiView: webView, speakerButton: speakerButton)
        
        self.view.window?.windowLevel = UIWindow.Level.statusBar;
        
        // Adding the ref if not added.
        if ChatbotService.refAdded {
            self.webView.configuration.userContentController.removeScriptMessageHandler(forName: "chatRef")
            print("Chat Ref removed")
        }
        self.webView.configuration.userContentController.add(self, name: "chatRef")
        ChatbotService.refAdded = true
        
        // Adding the collection view
        self.navigationController?.navigationBar.setBackgroundImage(UIImage(), for: UIBarMetrics.default)
        self.navigationController?.navigationBar.shadowImage = UIImage()
        self.navigationController?.navigationBar.isTranslucent = true
        self.navigationController?.view.backgroundColor = UIColor.clear
        self.view.backgroundColor = UIColor.white
        self.view.addSubview(webView)
        self.view.addSubview(newUIView)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        AppConstants.chatBotExternal = false
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        NotificationCenter.default.removeObserver(self,name: UIResponder.keyboardWillChangeFrameNotification, object: nil)
        NotificationCenter.default.removeObserver(self,name: UIResponder.keyboardWillHideNotification, object: nil)
        NotificationCenter.default.removeObserver(self, name: UIDevice.orientationDidChangeNotification, object: nil)
        
        // Removing the buttons from the view
        closeButton.removeFromSuperview()
        keyboardTextField.removeFromSuperview()
        sendButton.removeFromSuperview()
        micButton.removeFromSuperview()
        speakerButton.removeFromSuperview()
    }
    
    
    
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        do {
            if message.name == "chatRef" {
                print(JSON(message.body))
                let messageData = String(describing: message.body).data(using: .utf8)
                let messageJSON =  try JSONSerialization.jsonObject(with: messageData!, options: [])
                let jsonData = JSON(messageJSON)
                let eventName = JSON(messageJSON)["eventName"].stringValue
                print(jsonData)
                
                if eventName == "chatBotResponse" && ChatbotService.isSpeakerEnabled() {
                    endService(mic: false, speech: true)
                    // Checking if the speech is from current view
                    if self.isViewLoaded && (self.view.window != nil) && !audioEngine.isRunning {
                        let data = jsonData["data"].stringValue
                        let rateRaw = jsonData["speed"]
                        var rate = Float(0.5)
                        if rateRaw != JSON.null {
                            rate = Float(rateRaw.intValue) / Float(2)
                        }
                        let utterance = AVSpeechUtterance(string: data.withoutHtml)
                        utterance.voice = AVSpeechSynthesisVoice(language: "en-US")
                        utterance.rate = rate
                        try self.audioSession.setCategory(AVAudioSession.Category.playback)
                        speechSynthesizer.speak(utterance)
                    }
                } else if eventName == "navigateFromChatBot" {
                    // Take back to the previous Web view and update the url
                    let action = jsonData["data"]["action"]
                    let value = jsonData["data"]["value"]
                    
                                       switch action {
                        
                    case "search":
                        print("From Search: ", value)
                        let valueJSON =  try JSONSerialization.jsonObject(with: value.stringValue.data(using: .utf8)!, options: [])
                        let valueJsonObj = JSON(valueJSON)
                        
                        // Taking to the search page, by making the query
                        let url = "app/search/learning?q=" + valueJsonObj["q"].stringValue + "&f=" + valueJsonObj["f"].stringValue
                        var dataDict: [String: String] = [:]
                        dataDict["key"] = "urlNav"
                        dataDict["value"] = url
                        
                        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "chatBot"), object: self, userInfo: dataDict)
                        //                        self.closeButton.sendActions(for: .touchUpInside)
                        self.exitThisView()
                        break
                        
                    case "viewer":
                        print("From Viewer: ", value)
                        var dataDict: [String: String] = [:]
                        dataDict["key"] = "urlNav"
                        dataDict["value"] = value.stringValue
                        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "chatBot"), object: self, userInfo: dataDict)
                        //                      self.closeButton.sendActions(for: .touchUpInside)
                        self.exitThisView()
                        break
                        
                    //new actions
                    case "goals":
                        var dataDict: [String: String] = [:]
                        dataDict["key"] = "goalsNav"
                        dataDict["value"] = "app/goals/me/all"
                        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "chatBot"), object: self, userInfo: dataDict)
                        self.exitThisView()
                        break
                        
                    case "home":
                        var dataDict: [String: String] = [:]
                        dataDict["key"] = "homeNav"
                        dataDict["value"] = "page/home"
                        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "chatBot"), object: self, userInfo: dataDict)
                        self.exitThisView()
                        break
                        
                    case "interest":
                        var dataDict: [String: String] = [:]
                        dataDict["key"] = "interestNav"
                        dataDict["value"] = "app/profile/interest"
                        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "chatBot"), object: self, userInfo: dataDict)
                        self.exitThisView()
                        break
                        
                    case "NA":
                        let urlPath = JSON(messageJSON)["data"]["value"].stringValue
                         let  path = JsonUtil.convertJsonFromJsonString(inputJson: urlPath)
                         self.currentExternalURL = path!["url"].stringValue
                         
                         var dataDict: [String: String] = [:]
                         dataDict["key"] = "externalNav"
                         dataDict["value"] = currentExternalURL
                         AppConstants.chatBotExternal = true
                         NotificationCenter.default.post(name: NSNotification.Name(rawValue: "chatBot"), object: self, userInfo: dataDict)
                         self.exitThisView()
                        break
                        
                    default:
                        print(value)
                        break
                    }
                } else if eventName == "APP_LOADED" {
                    print("apploaded called---")
                    
                    let accessToken = Singleton.accessToken
                    self.chatBotLoaded = true
                    let sendDataMethodName = "window.receiveToken(\"\(accessToken)\")"
                    self.webView.evaluateJavaScript(sendDataMethodName, completionHandler: nil)
                    removeChatBotTimers()
                }
                    
                else if eventName == "OPEN_EXTERNAL_URL" {
                    let urlPath = JSON(messageJSON)["data"]["value"].stringValue
                    var  path = JsonUtil.convertJsonFromJsonString(inputJson: urlPath)
                    self.currentExternalURL = path!["url"].stringValue
                    
                    var dataDict: [String: String] = [:]
                    dataDict["key"] = "externalNav"
                    dataDict["value"] = currentExternalURL
                    AppConstants.chatBotExternal = true
                    NotificationCenter.default.post(name: NSNotification.Name(rawValue: "chatBot"), object: self, userInfo: dataDict)
                    self.exitThisView()
                }
            }
        } catch let error as NSError {
            print(error)
            print("Exception while getting the data from the chat bot")
        }
    }
    
    // Functions for the chat bot
    
    func performChatbotLoadedCheck() {
        failCount += 1
        
        if failCount == 10 {
            showChatbotNotLoaded()
        }
        
        self.webView.evaluateJavaScript("window.mobileAppLoaded()", completionHandler: nil)
    }
    
    // Alert for chatbot not bing loaded
    func showChatbotNotLoaded() {
        let alertController = UIAlertController(title: "Error", message: AppConstants.chatbotNotLoaded, preferredStyle: .alert)
        
        // Going back action
        let okayAction = UIAlertAction(title: "Okay", style: .cancel) { (action) -> Void in
            self.exitThisView()
        }
        
        //        // Reporting an issue that the content did not load
        //        let reportAction = UIAlertAction(title: "Report", style: .default, handler: { (action) -> Void in
        //            self.exitThisView()
        //            DispatchQueue.main.async {
        //                NotificationCenter.default.post(name: NSNotification.Name(rawValue: "extPlayer"), object: self, userInfo: ["path": "feedback/bug"])
        //            }
        //        })
        
        // Reload action
        //        let reloadAction = UIAlertAction(title: "Reload", style: .default) { (action) -> Void in
        //            self.webView.load(NSURLRequest(url: NSURL(string: ChatbotService.chatBotUrlStr)! as URL) as URLRequest)
        //        }
        //
        // Add Actions
        //        alertController.addAction(reportAction)
        alertController.addAction(okayAction)
        //        alertController.addAction(reloadAction)
        alertController.preferredAction = okayAction
        self.present(alertController, animated: true, completion: nil)
    }
    
    // for removing the timer of the chat bot IOS_LOADED event
    func removeChatBotTimers(){
        if chatBotLoadTimer != nil && chatBotLoadTimer.isValid {
            chatBotLoadTimer.invalidate()
            print("Timer removed for chat bot (viewDidLoad)")
            chatBotLoadTimer = nil
        }
    }
    
    // Sending the native part frame and position
    func getNativePartFrame() ->CGRect {
        let paddingAddition = ChatbotService.padding * 2
        let frameHeight = ChatbotService.height + paddingAddition
        return CGRect(x: 0, y: UIScreen.main.bounds.height - frameHeight, width: UIScreen.main.bounds.width, height: frameHeight)
    }
    
    func getWebViewFrame() -> CGRect {
        let webVHeight =  UIScreen.main.bounds.height - (UIScreen.main.bounds.height-newUIView.frame.origin.y) + 10
        return CGRect(x: 0, y: 0, width: UIScreen.main.bounds.width, height: webVHeight)
    }
    
    
    func getCloseButtonFrame() -> CGRect {
        
        let closeButtonWidth = CGFloat(30)
        let closeButtonHeight = CGFloat(30)
        if(UIScreen.main.bounds.height > 800) {
            return CGRect(x: webView.frame.width-(closeButtonWidth)-ChatbotViewController.padding, y: closeButtonHeight + (ChatbotViewController.padding * 5), width: closeButtonWidth, height: closeButtonHeight)
        }
        else{
            return CGRect(x: webView.frame.width-(closeButtonWidth)-ChatbotViewController.padding, y: closeButtonHeight-ChatbotViewController.padding, width: closeButtonWidth, height: closeButtonHeight)
        }
    }
    
    func addCloseButton(webView: WKWebView, closeButton: UIButton) {
        // Adding the close button
        let closeButtonFrame = getCloseButtonFrame()
        closeButton.frame = closeButtonFrame
        
        // Adding the button on the close button
        let closeImage = UIImage.fontAwesomeIcon(name: .close, textColor: UIColor.white, size: CGSize(width: 20, height: 20))
        closeButton.setImage(closeImage, for: .normal)
        
        closeButton.backgroundColor = UIColor.fromHex(rgbValue: UInt32(String(tintColor), radix: 16)!, alpha: 1.0)
        closeButton.layer.cornerRadius = 0.5 * closeButton.bounds.size.width
        closeButton.clipsToBounds = true
        
        // Adding the border for the button
        closeButton.layer.borderColor = AppConstants.lexBrandColor.cgColor
        closeButton.layer.borderWidth = 1.0
        
        // Adding the function-action to the close button
        closeButton.addTarget(self, action: #selector(closeButtonAction), for: .touchUpInside)
        
        // Adding the button to the web view frame
        webView.insertSubview(closeButton, aboveSubview: self.view)
    }
    
    func getMicButtonFrame() -> CGRect {
        let micButtonWidth = CGFloat(ChatbotViewController.lineHeight)
        let micButtonHeight = CGFloat(ChatbotViewController.lineHeight)
        return  CGRect(x: micButtonWidth + (4 * ChatbotViewController.padding) + keyboardTextField.frame.width, y: ChatbotService.padding, width: micButtonWidth, height: micButtonHeight)
    }
    
    func addMicButton(uiView: UIView, micButton: UIButton) {
        // Adding the mic button
        let micButtonFrame = getMicButtonFrame()
        
        micButton.frame = micButtonFrame
        let micImage = UIImage.fontAwesomeIcon(name: .microphone, textColor: UIColor.white, size: CGSize(width: 30, height: 30))
        micButton.setImage(micImage, for: .normal)
        micButton.backgroundColor = ChatbotViewController.lexColor
        micButton.layer.cornerRadius = 0.5 * micButton.bounds.size.width
        micButton.clipsToBounds = true
        
        // Adding the actions
        micButton.addTarget(self, action: #selector(micButtonDownAction), for: .touchDown)
        micButton.addTarget(self, action: #selector(micButtonUpAction), for: [.touchUpInside, .touchDragOutside, .touchDragExit])
        micButton.addTarget(self, action: #selector(micButtonMultitap), for: [.touchDownRepeat])
        
        uiView.insertSubview(micButton, aboveSubview: uiView)
    }
    
    func getKeyboardTextFieldFrame() -> CGRect {
        let keyboardTextFieldWidth = webView.frame.width - ChatbotViewController.micButtonWidth*2 - 5*ChatbotViewController.padding
        let keyboardTextFieldHeight = CGFloat(ChatbotViewController.lineHeight)
        return CGRect(x: (2*ChatbotViewController.padding), y: ChatbotService.padding, width: keyboardTextFieldWidth, height: keyboardTextFieldHeight)
    }
    
    func addKeyboardTextField(uiView: UIView, keyboardTextField: UITextField) {
        // Adding the input box
        let keyboardTextFieldFrame = getKeyboardTextFieldFrame()
        
        keyboardTextField.attributedPlaceholder = NSAttributedString(string: "Type or hold the mic to chat with bot", attributes: [
            .foregroundColor: UIColor.lightGray,
            .font: UIFont.boldSystemFont(ofSize: 12.0)
        ])
        
        keyboardTextField.textAlignment = .right
        keyboardTextField.frame = keyboardTextFieldFrame
        keyboardTextField.rightView = UIView(frame: CGRect(x: keyboardTextField.frame.width-ChatbotService.padding, y: ChatbotService.padding, width: ChatbotService.padding, height: keyboardTextField.frame.height))
        keyboardTextField.rightViewMode = .always
        
        keyboardTextField.backgroundColor = UIColor.white
        keyboardTextField.layer.borderColor = UIColor.lightGray.cgColor
        keyboardTextField.layer.borderWidth = 1.0
        keyboardTextField.layer.cornerRadius = 10
        keyboardTextField.delegate = self
        
        uiView.insertSubview(keyboardTextField, aboveSubview: newUIView)
    }
    
    
    func getSendButtonFrame() -> CGRect {
        return CGRect(x: (3*ChatbotViewController.padding) + self.keyboardTextField.frame.width, y: ChatbotService.padding, width: ChatbotViewController.micButtonWidth, height: ChatbotViewController.micButtonHeight)
    }
    func addSendButton(uiView: UIView, sendButton: UIButton) {
        // Adding the send button
        sendButton.frame = getSendButtonFrame()
        
        sendButton.setTitleColor(.black, for: [.normal, .selected, .focused, .highlighted])
        let sendImage = UIImage.fontAwesomeIcon(name: .send, textColor: ChatbotViewController.lexColor, size: CGSize(width: 30, height: 30))
        sendButton.setImage(sendImage, for: .normal)
        
        // Adding the actions
        sendButton.addTarget(self, action: #selector(sendButtonAction), for: .touchDown)
        
        // Rounding the button
        sendButton.layer.cornerRadius = 0.5 * self.sendButton.bounds.size.width
        sendButton.clipsToBounds = true
        
        // Adding the border
        sendButton.layer.borderWidth = 1
        sendButton.layer.borderColor = AppConstants.lexBrandColor.cgColor
        
        uiView.insertSubview(sendButton, aboveSubview: uiView)
    }
    
    func getSpeakerFrame() -> CGRect {
        return CGRect(x: UIScreen.main.bounds.width - ChatbotService.width - ChatbotService.padding, y: UIScreen.main.bounds.height/2, width: ChatbotService.width, height: ChatbotService.height)
        //        return CGRect(x: 0, y: 0, width: ChatbotService.width, height: ChatbotService.height)
    }
    
    func addSpeakerButton(uiView: UIView, speakerButton: UIButton) {
        // Adding the frame
        speakerButton.frame = getSpeakerFrame()
        speakerButton.setImage(getSpeakerImage(), for: .normal)
        speakerButton.backgroundColor = UIColor.white.withAlphaComponent(0.5)
        
        // Adding the actions
        speakerButton.addTarget(self, action: #selector(speakerButtonAction), for: .touchUpInside)
        
        // Rounding the button
        speakerButton.layer.cornerRadius = 0.5 * speakerButton.bounds.size.width
        speakerButton.clipsToBounds = true
        
        // Adding the border
        speakerButton.layer.borderWidth = 1
        speakerButton.layer.borderColor = AppConstants.lexBrandColor.cgColor
        
        uiView.insertSubview(speakerButton, aboveSubview: uiView)
    }
    
    func showListening() {
        listeningLabel.frame = CGRect(x: self.view.frame.size.width-150-ChatbotService.padding, y: self.view.frame.size.height-100, width: 150, height: 35)
        // This will align the label to the center
        listeningLabel.center.x = self.view.center.x
        listeningLabel.backgroundColor = UIColor.white.withAlphaComponent(0.8)
        listeningLabel.layer.borderColor = UIColor.red.withAlphaComponent(0.5).cgColor
        listeningLabel.layer.borderWidth = 1
        listeningLabel.textColor = UIColor.black
        listeningLabel.textAlignment = .center
        listeningLabel.font = UIFont.systemFont(ofSize: 15.0)
        listeningLabel.text = "🎤 Listening.."
        listeningLabel.alpha = 1.0
        listeningLabel.layer.cornerRadius = 10;
        listeningLabel.clipsToBounds  =  true
        self.view.addSubview(listeningLabel)
        listeningLabel.startBlink()
    }
    
    func hideListening() {
        listeningLabel.removeFromSuperview()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        print("Received a memory warning")
        // Dispose of any resources that can be recreated.
    }
    
    func endService(mic: Bool = false, speech: Bool = false) {
        if speech {
            if speechSynthesizer.isSpeaking {
                speechSynthesizer.stopSpeaking(at: .immediate)
            }
        }
        if mic {
            if audioEngine.isRunning {
                audioEngine.stop()
                audioEngine.inputNode.removeTap(onBus: 0)
                recognitionRequest?.endAudio()
                print("Audio state running...")
            }
            if !audioEngine.isRunning {
                print("Audio receiving stopped...")
                hideListening()
            }
        }
    }
    func exitThisView() {
        endService(mic: true, speech: true)
        navigationController?.popViewController(animated: true)
        dismiss(animated: true, completion: nil)
    }
    
    @objc func closeButtonAction(sender: UIButton!) {
        print("Close button tapped")
        exitThisView()
    }
    @objc func micButtonDownAction(sender: UIButton!) {
        endService(mic: true, speech: true)
        // Removing existing text from the input box
        self.keyboardTextField.text = ""
        if !audioEngine.isRunning {
            startRecording()
        }
    }
    let autoSend = false
    @objc func micButtonUpAction(sender: UIButton!) {
        endService(mic: true)
        if autoSend {
            sendButton.sendActions(for: .touchDown)
        }
    }
    @objc func micButtonMultitap(sender: UIButton) {
        endService(mic: true)
    }
    @objc func sendButtonAction(sender: UIButton!) {
        if keyboardTextField.text != "" {
            // Removing the keyboard
            self.view.endEditing(true)
            // Running the javascript
            let sendDataMethodName = "window.speechRecognise(\"\(keyboardTextField.text!)\")"
            self.webView.evaluateJavaScript(sendDataMethodName, completionHandler: nil)
            
            keyboardTextField.text = ""
        }
    }
    
    @objc func speakerButtonAction(sender: UIButton!) {
        ChatbotService.toggleSpeaker()
        print("IS Speaker enabled:", ChatbotService.isSpeakerEnabled())
        endService(speech: true)
        speakerButton.setImage(getSpeakerImage(), for: .normal)
    }
    
    func getSpeakerImage() -> UIImage {
        let height = ChatbotService.height-ChatbotService.padding*2
        let width = ChatbotService.width-ChatbotService.padding*2
        var speakerImage = UIImage.fontAwesomeIcon(name: .volumeUp, textColor: ChatbotViewController.lexColor, size: CGSize(width: width, height: height))
        if !ChatbotService.isSpeakerEnabled() {
            speakerImage = UIImage.fontAwesomeIcon(name: .volumeOff, textColor: ChatbotViewController.lexColor.withAlphaComponent(0.3), size: CGSize(width: ChatbotService.width/1.5, height: ChatbotService.height/1.5))
        }
        return speakerImage
    }
    
    // For moving the view up when keyboad or any input form comes on the bottom of the screen.
    @objc func keyboardWillHide() {
        self.view.frame.origin.y = 0
    }
    
    @objc func keyboardWillChange(notification: NSNotification) {
        if let keyboardSize = (notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue {
            if keyboardTextField.isFirstResponder {
                self.view.frame.origin.y = -keyboardSize.height
            }
        }
    }
    
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        self.view.endEditing(true)
        self.sendButtonAction(sender: nil)
        return false
    }
    
    // Variables for speech recogniser
    private let speechRecognizer = SFSpeechRecognizer(locale: Locale.init(identifier: "en-US"))
    private var recognitionRequest: SFSpeechAudioBufferRecognitionRequest?
    private var recognitionTask: SFSpeechRecognitionTask?
    private let audioEngine = AVAudioEngine()
    
    let audioSession = AVAudioSession.sharedInstance()
    
    // method related to speech recogniser
    func startRecording() {
        SFSpeechRecognizer.requestAuthorization { authStatus in
            /* The callback may not be called on the main thread. Add an
             operation to the main queue to update the record button's state.
             */
            OperationQueue.main.addOperation {
                var canRecord = false
                switch authStatus {
                case .authorized:
                    print("Authorized")
                    canRecord = true
                case .denied:
                    print("Denied")
                case .restricted:
                    print("Restricted")
                case .notDetermined:
                    print("Not determined")
                }
                
                if canRecord {
                    self.startMicRecording()
                } else {
                    let alertController = UIAlertController(
                        title: "Permissions",
                        message: "In order to use this functionality, please enable \("Speech Recognition") on settings",
                        preferredStyle: .alert)
                    
                    let cancelAction = UIAlertAction(title: "Cancel", style: .cancel, handler: nil)
                    alertController.addAction(cancelAction)
                    
                    let openAction = UIAlertAction(title: "Open Settings", style: .default) { (action) in
                        if let url = URL(string:UIApplication.openSettingsURLString) {
                            UIApplication.shared.open(url, options: convertToUIApplicationOpenExternalURLOptionsKeyDictionary([:]), completionHandler: nil)
                        }
                    }
                    
                    alertController.addAction(openAction)
                    
                    self.present(alertController, animated: true, completion: nil)
                }
            }
        }
    }
    
    func startMicRecording() {
        endService(mic: true, speech: true)
        
        if recognitionTask != nil {
            recognitionTask?.cancel()
            recognitionTask = nil
        }
        
        do {
            try audioSession.setCategory(AVAudioSession.Category(rawValue: convertFromAVAudioSessionCategory(AVAudioSession.Category.record)))
            try audioSession.setMode(AVAudioSession.Mode.measurement)
            try audioSession.setActive(true, options: .notifyOthersOnDeactivation)
        } catch {
            print("audioSession properties weren't set because of an error.")
        }
        
        recognitionRequest = SFSpeechAudioBufferRecognitionRequest()
        
        let inputNode = audioEngine.inputNode
        
        guard let recognitionRequest = recognitionRequest else {
            fatalError("Unable to create an SFSpeechAudioBufferRecognitionRequest object")
        }
        
        recognitionRequest.shouldReportPartialResults = true
        
        recognitionTask = speechRecognizer?.recognitionTask(with: recognitionRequest, resultHandler: { (result, error) in
            
            var isFinal = false
            
            if result != nil {
                print("Text is: " + (result?.bestTranscription.formattedString)!)
                self.keyboardTextField.text = (result?.bestTranscription.formattedString)!
                isFinal = (result?.isFinal)!
            }
            
            if error != nil || isFinal {
                self.audioEngine.stop()
                inputNode.removeTap(onBus: 0)
                
                self.recognitionRequest = nil
                self.recognitionTask = nil
            }
        })
        
        let recordingFormat = inputNode.outputFormat(forBus: 0)
        inputNode.installTap(onBus: 0, bufferSize: 1024, format: recordingFormat) { (buffer, when) in
            self.recognitionRequest?.append(buffer)
        }
        
        audioEngine.prepare()
        
        do {
            try audioEngine.start()
            if audioEngine.isRunning {
                print("Audio engine is running....")
                showListening()
            }
        } catch {
            print("audioEngine couldn't start because of an error.")
        }
    }
    
    
    //device rotation
    @objc func deviceRotated() {
        DispatchQueue.main.async {
            self.webView.frame = ChatbotService.getWebViewFrame()
            let currentCloseButtonFrame = self.getCloseButtonFrame()
            if UIDevice.current.orientation.isLandscape {
                print(currentCloseButtonFrame.origin.y)
                self.closeButton.frame = CGRect(x: currentCloseButtonFrame.origin.x, y: currentCloseButtonFrame.origin.y-20, width: currentCloseButtonFrame.width, height:currentCloseButtonFrame.height)
            } else {
                self.closeButton.frame = currentCloseButtonFrame
            }
            
            self.newUIView.frame = self.getNativePartFrame()
            self.keyboardTextField.frame = self.getKeyboardTextFieldFrame()
            self.sendButton.frame = self.getSendButtonFrame()
            self.speakerButton.frame = self.getSpeakerFrame()
            self.micButton.frame = self.getMicButtonFrame()
        }
    }
}
extension UILabel {
    func startBlink() {
        UIView.animate(withDuration: 1.0,
                       delay:0.5,
                       options:[.allowUserInteraction, .curveEaseInOut, .autoreverse, .repeat],
                       animations: { self.alpha = 0 },
                       completion: nil)
    }
    
    func stopBlink() {
        layer.removeAllAnimations()
        alpha = 1
    }
}

extension String {
    public var withoutHtml: String {
        guard let data = self.data(using: .utf8) else {
            return self
        }
        
        let options: [NSAttributedString.DocumentReadingOptionKey: Any] = [
            .documentType: NSAttributedString.DocumentType.html,
            .characterEncoding: String.Encoding.utf8.rawValue
        ]
        
        guard let attributedString = try? NSAttributedString(data: data, options: options, documentAttributes: nil) else {
            return self
        }
        
        return attributedString.string
    }
}



// Helper function inserted by Swift 4.2 migrator.
fileprivate func convertFromAVAudioSessionCategory(_ input: AVAudioSession.Category) -> String {
    return input.rawValue
}

// Helper function inserted by Swift 4.2 migrator.
fileprivate func convertToUIApplicationOpenExternalURLOptionsKeyDictionary(_ input: [String: Any]) -> [UIApplication.OpenExternalURLOptionsKey: Any] {
    return Dictionary(uniqueKeysWithValues: input.map { key, value in (UIApplication.OpenExternalURLOptionsKey(rawValue: key), value)})
}
