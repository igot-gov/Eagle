import { Component, OnInit, Input } from '@angular/core'
import { DomSanitizer } from '@angular/platform-browser'
import { ConfigurationsService } from '../../../../../utils/src/public-api'

@Component({
  selector: 'ws-widget-btn-linkedin-share',
  templateUrl: './btn-linkedin-share.component.html',
  styleUrls: ['./btn-linkedin-share.component.scss'],
})
export class BtnLinkedinShareComponent implements OnInit {
  @Input() url = location.href
  isSocialMediaLinkedinShareEnabled = false
  constructor(private sanitizer: DomSanitizer, private configSvc: ConfigurationsService) {}

  ngOnInit() {
    if (this.configSvc.restrictedFeatures) {
      this.isSocialMediaLinkedinShareEnabled = !this.configSvc.restrictedFeatures.has(
        'socialMediaLinkedinShare',
      )
    }
  }

  get sanitizeFbUrl() {
    const urlarr = location.href.split('/')
    const contentId = urlarr[urlarr.length - 2]
    const url = `https://d136953gtttd92.cloudfront.net/share/content/${contentId}`
        return this.sanitizer.bypassSecurityTrustResourceUrl(
      `https://www.linkedin.com/shareArticle?mini=true&url=${url}&source=LinkedIn`,
    )
  }
}
