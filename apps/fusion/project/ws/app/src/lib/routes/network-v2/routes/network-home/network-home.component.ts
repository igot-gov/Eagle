import { Component, OnInit } from '@angular/core'
import { ActivatedRoute, Router } from '@angular/router'
import { NSNetworkDataV2 } from '../../models/network-v2.model'

@Component({
  selector: 'ws-app-network-home',
  templateUrl: './network-home.component.html',
  styleUrls: ['./network-home.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1' },
  /* tslint:enable */
})
export class NetworkHomeComponent implements OnInit {
  tabsData: NSNetworkDataV2.IProfileTab[]
  recommendedUsers!: NSNetworkDataV2.IRecommendedUserResult
  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.tabsData = this.route.parent && this.route.parent.snapshot.data.pageData.data.tabs || []
    this.recommendedUsers = this.route.snapshot.data.recommendedUsers.data.result.data.
    find((item: any) => item.field === 'employmentDetails.departmentName').results
  }

  ngOnInit() {
  }

  goToRecommendedUsers() {
    this.router.navigate(['/app/network-v2/recommended'])
  }

  goToConnectionRequests() {
    this.router.navigate(['/app/network-v2/connection-requests'])
  }

}
