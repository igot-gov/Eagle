import { Component, Input, OnInit } from '@angular/core'
import { Router } from '@angular/router'
import { NsWidgetResolver, WidgetBaseComponent } from '@ws-widget/resolver'
import { CardNetWorkService } from './card-network.service'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'

@Component({
  selector: 'ws-widget-card-home-network',
  templateUrl: './card-network-home.component.html',
  styleUrls: ['./card-network-home.component.scss'],
})
export class CardNetworkHomeComponent extends WidgetBaseComponent

  implements OnInit, NsWidgetResolver.IWidgetData<any> {
  enableFeature = true
  enablePeopleSearch = true
  @Input() widgetData: any
  givenName: string | undefined
  userEmail: string | undefined
  keyTag: string[] = []
  newUserReq: any
  deptUserReq: any
  nameFilter = ''
  searchSpinner = false

  constructor(private router: Router, private cardNetworkService: CardNetWorkService, public configurationsService: ConfigurationsService) {
    super()
  }

  newUserArray = []
  departmentUserArray = []
  searchResultUserArray = []
  ngOnInit() {
    this.getAllActiveUsers()
    this.getAllDepartmentUsers()

  }

  getUserFullName(user: any) {
    if (user && user.personalDetails.firstname && user.personalDetails.surname) {
      return `${user.personalDetails.firstname.trim()} ${user.personalDetails.surname.trim()}`
    }
    return ''
  }
  getSearchProfileUserFullName(user: any) {
    if (user && user.first_name && user.last_name) {
      return `${user.first_name.trim()} ${user.last_name.trim()}`
    }
    return ''
  }
  goToUserProfile(user: any) {
    this.router.navigate(['/app/person-profile'], { queryParams: { emailId: user.personalDetails.primaryEmail } })
  }
  searchUser() {

    if (this.nameFilter.length === 0) {
      this.enableFeature = true
    } else {
      this.searchSpinner = true
      this.enableFeature = false
      this.getSearchResult()
    }

  }
  getAllActiveUsers() {

    this.newUserReq = {
      limit: 50,
      offset: 0,
      intervalInDays: 7,
      type: 'latestUsers',
    }
    this.cardNetworkService.fetchLatestUserInfo(this.newUserReq).subscribe(data => {
      this.newUserArray = data.users
      if (typeof this.newUserArray === 'undefined') {
        this.newUserArray = []
      }
    })

  }
  getAllDepartmentUsers() {
    this.deptUserReq = {
      limit: 50,
      offset: 0,
      department: this.configurationsService.activeOrg,
      intervalInDays: 7,
      type: 'deptUsers',
    }
    this.cardNetworkService.fetchLatestUserInfo(this.deptUserReq).subscribe(data => {
      this.departmentUserArray = data.users
    })

  }
  getSearchResult() {
    this.cardNetworkService.fetchSearchUserInfo(this.nameFilter.trim()).subscribe(data => {
      this.searchResultUserArray = data
      this.searchSpinner = false

    })

  }
}