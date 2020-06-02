import { Component, Input, OnInit } from '@angular/core'
import { INodeData } from '../../models/career-development-path.model'
// import { ConfigurationsService } from '@ws-widget/utils'

@Component({
  selector: 'ws-app-cdp-card',
  templateUrl: './cdp-card.component.html',
  styleUrls: ['./cdp-card.component.scss'],
})
export class CdpCardComponent implements OnInit {
  @Input() data: INodeData = {} as INodeData
  defaultThumbnail = ''
  @Input() showCompletion = false
  progress = Math.round(Math.random() * 100)
  // fetchImage = false
  constructor() { }

  ngOnInit() {
    // const instanceConfig = this.configSvc.instanceConfig
    // if (instanceConfig) {
    //   this.defaultThumbnail = instanceConfig.logos.defaultContent
    //   // //console.log('instanceConfig logos >', instanceConfig.logos.defaultContent)
    // }
    // this.fetchImage = true
  }
}
