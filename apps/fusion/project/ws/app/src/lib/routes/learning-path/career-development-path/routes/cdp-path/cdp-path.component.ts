import { Component, OnInit } from '@angular/core'
import { INodeData, ICdpChild } from '../../models/career-development-path.model'
import { ActivatedRoute } from '@angular/router'
import { NsPage, ConfigurationsService } from '@ws-widget/utils'
import { salesData } from '../../../dynamic-network/utils/dynamic-network.data'
@Component({
  selector: 'ws-app-cdp-path',
  templateUrl: './cdp-path.component.html',
  styleUrls: ['./cdp-path.component.scss'],
})
export class CdpPathComponent implements OnInit {
  grandChildObj: INodeData[] = salesData['Value Selling']
  childName = ''
  child = ''
  area = ''
  titleArray = {
    childArray: [],
    progress: [85, 65, 75, 88, 68],
  }
  carrierData: ICdpChild = {} as ICdpChild
  show = false
  noContent = false
  pageNavbar: Partial<NsPage.INavBackground> = this.configSvc.pageNavBar
  constructor(private activatedRoute: ActivatedRoute, private configSvc: ConfigurationsService) {}

  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      this.childName = decodeURIComponent(params.child.trim())
      this.child = params.cdPath.trim()
      this.area = params.cdArea.trim()
      this.activatedRoute.snapshot.data.pageData.data.third[this.area][this.child][0].children.map(
        (cur: ICdpChild) => {
          if (cur.title === this.childName) {
            this.carrierData = cur
          }
        },
      )
    })
  }
}
