import { Component, OnInit } from '@angular/core'
import { SafeUrl, DomSanitizer } from '@angular/platform-browser'
import { ConfigurationsService } from '../../../../library/ws-widget/utils/src/public-api'

@Component({
  selector: 'ws-epoch-login-v2',
  templateUrl: './epoch-login-v2.component.html',
  styleUrls: ['./epoch-login-v2.component.scss'],
})
export class EpochLoginV2Component implements OnInit {
  appIcon: SafeUrl | null = null
  events: any
  constructor(private configSvc: ConfigurationsService, private domSanitizer: DomSanitizer) {}

  ngOnInit() {
    this.events = [
      {
        name: 'AGM',
        date: '27 June 2020',
        desc: 'Experience the Annual General Meeting virtually',
        thumbnail: '/assets/instances/Epoch/images/landing/agm.png',
        thumbnailTitle: '',
        bgColor: '#007cc3',
        link: '/InfosysAGM',
      },
      {
        name: 'Ingenious',
        date: 'Upcoming',
        desc: 'Discovering the most innovative management students in the country',
        thumbnail: '',
        thumbnailTitle: 'INGENIOUS',
        bgColor: '#2f9bfe',
        link: 'https://www.infosys.com/careers/ingenious.html',
      },
      {
        name: 'Aarohan',
        date: 'Upcoming',
        desc: 'Catalyzing social innovation in India with a platform that helps solutions to scale',
        thumbnail: '/assets/instances/Epoch/images/landing/aarohan.png',
        thumbnailTitle: '',
        bgColor: '#f7ee23',
        link: 'https://www.infosys.com/infosys-foundation/aarohan-social-innovation-awards.html',
      },
    ]
    if (this.configSvc.instanceConfig) {
      this.appIcon = this.domSanitizer.bypassSecurityTrustResourceUrl(
        this.configSvc.instanceConfig.logos.landingLogo,
      )
    }
  }
}
