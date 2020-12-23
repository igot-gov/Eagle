import { Component, OnInit, Input } from '@angular/core'
import { DomSanitizer } from '@angular/platform-browser'
import { ConfigurationsService } from '../../../../../utils/src/public-api'

@Component({
  selector: 'ws-widget-btn-facebook-share',
  templateUrl: './btn-facebook-share.component.html',
  styleUrls: ['./btn-facebook-share.component.scss'],
})
export class BtnFacebookShareComponent implements OnInit {
  @Input() url = location.href
  @Input() contentId: string | null = null
  @Input() shareType: string | null = null
  isSocialMediaFacebookShareEnabled = false
  userId: string | undefined
  constructor(private sanitizer: DomSanitizer, private configSvc: ConfigurationsService) {}

  ngOnInit() {

    if (this.configSvc.restrictedFeatures) {
      this.isSocialMediaFacebookShareEnabled = !this.configSvc.restrictedFeatures.has(
        'socialMediaFacebookShare',
      )
    }
    if (this.configSvc.userProfile) {
      this.userId = this.configSvc.userProfile.userId
    }
  }

  get sanitizeFbUrl() {
    const url = `https://${window.location.hostname}/share/${this.shareType}/${this.userId}/${this.contentId}`
      return this.sanitizer.bypassSecurityTrustResourceUrl(
      `https://www.facebook.com/plugins/share_button.php?href=${url}&layout=button&size=large`,
    )
  }
}
