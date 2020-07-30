//  LiveStreamController.swift
//  Lex
//  Created by Shubham Singh on 1/31/19.
//  Copyright © 2019 Infosys. All rights reserved.

import AVFoundation
import HaishinKit
import Photos
import UIKit
import VideoToolbox

class ExampleRecorderDelegate: DefaultAVRecorderDelegate {
    override func didFinishWriting(_ recorder: AVRecorder) {
        guard let writer: AVAssetWriter = recorder.writer else { return }
        PHPhotoLibrary.shared().performChanges({() -> Void in
            PHAssetChangeRequest.creationRequestForAssetFromVideo(atFileURL: writer.outputURL)
        }, completionHandler: { _, error -> Void in
            do {
                try FileManager.default.removeItem(at: writer.outputURL)
            } catch {
                print(error)
            }
        })
    }
}

class LiveStreamViewController: UIViewController{
    
    var rtmpConnection = RTMPConnection()
    var rtmpStream: RTMPStream!
    var sharedObject: RTMPSharedObject!
    var currentEffect: VisualEffect?
    var publish: Bool = true
    
    var currentPosition: AVCaptureDevice.Position = .back
    
    
    var timerLabel: UILabel = UILabel()
    var switchButton: UIButton = UIButton()
    var startButton: UIButton = UIButton()
    var microphoneButton: UIButton = UIButton()
    var timer : Timer!
    var counter = 1
    
    
    @IBOutlet weak var lfView: GLHKView!
    
    @IBOutlet weak var navigationBar: UINavigationBar!
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        
        let navigationBarTintColor = AppConstants.primaryTheme.replacingOccurrences(of: "#", with: "")
        navigationBar.barTintColor = UIColor.fromHex(rgbValue: UInt32(String(navigationBarTintColor), radix: 16)!, alpha: 1.0)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.deviceRotated), name: UIDevice.orientationDidChangeNotification, object: nil)
        
        rtmpStream = RTMPStream(connection: rtmpConnection)
        rtmpStream.syncOrientation = true
        
        rtmpStream.captureSettings = [
            "sessionPreset": AVCaptureSession.Preset.hd1280x720.rawValue,
            "continuousAutofocus": true,
            "continuousExposure": true
        ]
        
        rtmpStream.videoSettings = [
            "width": 1280,
            "height": 720
        ]
        
        rtmpStream.audioSettings = [
            "muted": false, // mute audio
            "bitrate": 32 * 1024,
            //            "sampleRate": sampleRate,
        ]
        
        
        lfView.addSubview(timerLabel)
        lfView.addSubview(switchButton)
        lfView.addSubview(startButton)
        timerLabel.isHidden = true
        timerLabel.textAlignment = .center
        
        startButton.setTitle("Stream", for: UIControl.State())
        
        self.setPostitions()
        
        //        cameraButton.addTarget(self, action: #selector(didTapCameraButton(_:)), for:.touchUpInside)
        startButton.addTarget(self, action: #selector(didTapStartLiveButton(_:)), for: .touchUpInside)
        //        zoomInButton.addTarget(self, action: #selector(didTapZoomInButton(_:)), for: .touchUpInside)
        //        zoomOutButton.addTarget(self, action: #selector(didTapZoomOutButton(_:)), for: .touchUpInside)
        //        microphoneButton.addTarget(self, action: #selector(didTapMicrophoneButton(_:)), for: .touchUpInside)
        //
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        rtmpStream.attachAudio(AVCaptureDevice.default(for: .audio)) { error in
        }
        rtmpStream.attachCamera(DeviceUtil.device(withPosition: currentPosition)) { error in
        }
        lfView?.attachStream(rtmpStream)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        rtmpStream.close()
        rtmpStream.dispose()
    }
    
    
    @IBAction func homePressed(_ sender: Any) {
        print("Home button pressed")
        _ = navigationController?.popViewController(animated: true)
    }
    
    
    func setPostitions(){
        
        let padding: CGFloat = 16.0
        //        let containerViewHeight = UIScreen.main.bounds.height
        
        let lfViewFrame = CGRect(x: 0, y: self.navigationBar.frame.origin.y + self.navigationBar.frame.height, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height - (self.navigationBar.frame.origin.y + self.navigationBar.frame.height))
        
        lfView.frame = lfViewFrame
        let timerLabelFrame = CGRect(x: 20, y: UIScreen.main.bounds.origin.y + padding, width: 90, height: 50)
        
        timerLabel.frame = timerLabelFrame
        timerLabel.text = "LIVE 00:01"
        timerLabel.font = UIFont.systemFont(ofSize: 15)
        timerLabel.textColor = UIColor.white
        timerLabel.backgroundColor = UIColor.red
        
        
        let switchButtonFrame = CGRect(x: UIScreen.main.bounds.width - 70, y: UIScreen.main.bounds.height - 80 - (self.navigationBar.frame.origin.y + self.navigationBar.frame.height), width: 44, height: 44)
        switchButton.frame = switchButtonFrame
        
        let microphoneButtonFrame = CGRect(x: UIScreen.main.bounds.width - 70, y: UIScreen.main.bounds.origin.y + padding, width: 44, height: 44)
        microphoneButton.frame = microphoneButtonFrame
        microphoneButton.setImage(UIImage(named: "microphoneSwitch"), for: UIControl.State())
        
        let startButtonFrame = CGRect(x: UIScreen.main.bounds.width/4, y: UIScreen.main.bounds.height - 80 - (self.navigationBar.frame.origin.y + self.navigationBar.frame.height), width: UIScreen.main.bounds.width/2, height: 44)
        
        startButton.frame = startButtonFrame
        startButton.layer.cornerRadius = 22
        startButton.setTitleColor(UIColor.black, for:UIControl.State())
        startButton.titleLabel!.font = UIFont.systemFont(ofSize: 16)
        startButton.backgroundColor = UIColor.brown
    }
    
    // device rotation function
    @objc func deviceRotated() {
        self.setPostitions()
    }
    
    @objc func didTapStartLiveButton(_ button: UIButton) -> Void {
        if (!publish) {
            print("Streaming stopped")
            startButton.setTitle("Stream", for: UIControl.State())
            UIApplication.shared.isIdleTimerDisabled = false
            rtmpConnection.close()
            timerLabel.text = "LIVE 00:00"
            timerLabel.textColor = UIColor.white
            timerLabel.backgroundColor = UIColor.red
            timer.invalidate()
            timer = nil
            counter = 0
            timerLabel.isHidden = true
            rtmpConnection.removeEventListener(Event.RTMP_STATUS, selector: #selector(rtmpStatusHandler), observer: self)
            
        } else {
            startButton.setTitle("Stop", for: UIControl.State())
            UIApplication.shared.isIdleTimerDisabled = true
            rtmpConnection.addEventListener(Event.RTMP_STATUS, selector: #selector(rtmpStatusHandler), observer: self)
            rtmpConnection.connect(Preference.defaultInstance.uri!)
            
            timerLabel.isHidden = false
            timer = Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true, block: { (Void)
                in
                self.counter += 1
                self.timerLabel.text! = "LIVE " + String(format: "%02d:%02d", (self.counter % 3600) / 60, (self.counter % 3600) % 60)
            })
            
            
            
        }
        publish = !publish
        
    }
    
    
    
    @IBAction func togglePause(_ sender: Any) {
        rtmpStream.togglePause()
    }
    
    
    @IBAction func rotateCamera(_ sender: UIButton) {
        let position: AVCaptureDevice.Position = currentPosition == .back ? .front : .back
        rtmpStream.attachCamera(DeviceUtil.device(withPosition: position)) { error in
            
        }
        currentPosition = position
    }
    
    @objc
    func rtmpStatusHandler(_ notification: Notification) {
        let e = Event.from(notification)
        if let data: ASObject = e.data as? ASObject, let code: String = data["code"] as? String {
            print("---------")
            print(code)
            print("=========")
            
            switch code {
            case RTMPConnection.Code.connectSuccess.rawValue:
                rtmpStream!.publish(Preference.defaultInstance.streamName!)
            // sharedObject!.connect(rtmpConnection)
            default:
                break
            }
        }
    }
    
    func tapScreen(_ gesture: UIGestureRecognizer) {
        if let gestureView = gesture.view, gesture.state == .ended {
            let touchPoint: CGPoint = gesture.location(in: gestureView)
            let pointOfInterest = CGPoint(x: touchPoint.x / gestureView.bounds.size.width, y: touchPoint.y / gestureView.bounds.size.height)
            print("pointOfInterest: \(pointOfInterest)")
            rtmpStream.setPointOfInterest(pointOfInterest, exposure: pointOfInterest)
        }
    }
    
    
}
