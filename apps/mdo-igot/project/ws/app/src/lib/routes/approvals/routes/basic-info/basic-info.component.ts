import { Component, OnInit } from '@angular/core'
import { ActivatedRoute, Router, NavigationEnd, Event } from '@angular/router'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */
import { ConfigurationsService } from '@ws-widget/utils'

@Component({
  selector: 'ws-app-basic-info',
  templateUrl: './basic-info.component.html',
  styleUrls: ['./basic-info.component.scss'],
})
export class BasicInfoComponent implements OnInit {
  basicInfo: any
  imagePath: any
  constructor(private activeRoute: ActivatedRoute, private router: Router, private configSvc: ConfigurationsService) {
    this.router.events.subscribe((event: Event) => {
      if (event instanceof NavigationEnd) {
        const profileData = this.activeRoute.snapshot.data.profileData.data.result.UserProfile[0] || {}
        this.configSvc.departName = _.get(this.activeRoute, 'snapshot.data.department.data.deptName')
        this.basicInfo = profileData.personalDetails
        this.imagePath = profileData.photo
      }
    })
  }

  ngOnInit() { }
  changeToGlobalSymbol($event: any) {
    $event.target.src = '/assets/images/profile/blank-profilePcture.png'
  }
}
