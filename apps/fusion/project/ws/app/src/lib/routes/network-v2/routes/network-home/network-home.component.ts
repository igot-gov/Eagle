import { Component, OnInit } from '@angular/core'
import { ActivatedRoute, Router } from '@angular/router'
import { NSNetworkDataV2 } from '../../models/network-v2.model'
import { NetworkV2Service } from '../../services/network-v2.service'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'

@Component({
  selector: 'ws-app-network-home',
  templateUrl: './network-home.component.html',
  styleUrls: ['./network-home.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 mt-6 ' },
  /* tslint:enable */
})
export class NetworkHomeComponent implements OnInit {
  tabsData: NSNetworkDataV2.IProfileTab[]
  recommendedUsers!: NSNetworkDataV2.IRecommendedUserResult
  connectionRequests!: any
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private networkV2Service: NetworkV2Service,
    private configSvc: ConfigurationsService,
  ) {
    this.tabsData = this.route.parent && this.route.parent.snapshot.data.pageData.data.tabs || []
    this.recommendedUsers = this.route.snapshot.data.recommendedUsers.data.result.data.
    find((item: any) => item.field === 'employmentDetails.departmentName').results
    this.connectionRequests = this.route.snapshot.data.connectionRequests.data.result.data
  }

  ngOnInit() {
  }

  goToMyMdo() {
    this.router.navigate(['/app/network-v2/my-mdo'])
  }

  goToConnectionRequests() {
    this.router.navigate(['/app/network-v2/connection-requests'])
  }

  connectionUpdate(event: any) {
    if (event === 'connection-updated') {
      this.networkV2Service.fetchAllReceivedConnectionRequests().subscribe(
        (data: any) => {
          this.connectionRequests = data.result.data
        },
        (_err: any) => {
          // this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
        })
    }
  }

  connectionUpdatePeopleCard(event: any) {
    if (event === 'connection-updated') {
      let usrDept = 'igot'
      if (this.configSvc.userProfile) {
        usrDept = this.configSvc.userProfile.departmentName || 'igot'
      }
      let req: NSNetworkDataV2.IRecommendedUserReq
      req = {
        size: 50,
        offset: 0,
        search: [
          {
            field: 'employmentDetails.departmentName',
            values: [usrDept],
          },
        ],
      }
      this.networkV2Service.fetchAllRecommendedUsers(req).subscribe(
        (data: any) => {
          this.recommendedUsers = data.result.data
            .find((item: any) => item.field === 'employmentDetails.departmentName').results
        },
        (_err: any) => {
          // this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
        })
    }
  }

}
