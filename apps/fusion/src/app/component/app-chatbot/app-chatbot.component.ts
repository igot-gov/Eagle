import { Component, OnInit, Input, OnChanges } from '@angular/core'
import { SafeResourceUrl, DomSanitizer } from '@angular/platform-browser'
import { ConfigurationsService } from '../../../../library/ws-widget/utils/src/public-api'
import { MobileAppsService } from '../../services/mobile-apps.service'

const HIDE_ON_ROUTES = ['viewer']

@Component({
  selector: 'ws-app-chatbot',
  templateUrl: './app-chatbot.component.html',
  styleUrls: ['./app-chatbot.component.scss'],
})
export class AppChatbotComponent implements OnInit, OnChanges {
  @Input() currentUrl!: string
  chatbotOpened = false
  chatbotUrl: SafeResourceUrl | null = null
  isRestricted = true
  isFirstTimeLoaded = false
  hideOnInvalidRoute = false

  constructor(
    private sanitizer: DomSanitizer,
    private configSvc: ConfigurationsService,
    private mobileAppsSvc: MobileAppsService,
  ) { }

  ngOnInit() {
    if (this.configSvc.instanceConfig) {
      this.chatbotUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
        this.configSvc.instanceConfig.chatBotUrl,
      )
    }
    if (this.configSvc.restrictedFeatures) {
      this.isRestricted = this.configSvc.restrictedFeatures.has('chat-bot')
    }
    if (!this.isRestricted && this.currentUrl) {
      this.checkIfVisible()
    }
  }
  ngOnChanges() {
    if (this.currentUrl && !this.isRestricted) {
      this.checkIfVisible()
    }
  }

  private checkIfVisible() {
    if (
      this.currentUrl &&
      this.currentUrl.length &&
      HIDE_ON_ROUTES.some(route => this.currentUrl.substring(1).startsWith(route))
    ) {
      this.hideOnInvalidRoute = true
      this.setAppVisibility(false)
      return
    }
    this.hideOnInvalidRoute = false
    this.setAppVisibility(true)
  }

  private setAppVisibility(isVisible: boolean) {
    if (this.mobileAppsSvc.isMobile) {
      this.hideOnInvalidRoute = true
      this.mobileAppsSvc.appChatbotVisibility(isVisible ? 'yes' : 'no')
    }
  }

  openChatbot() {
    if (!this.isFirstTimeLoaded) {
      this.isFirstTimeLoaded = true
    }
    this.chatbotOpened = true
  }
}
