import { Component, Input, OnInit } from '@angular/core'
import { Router } from '@angular/router'
import { NsWidgetResolver, WidgetBaseComponent } from '@ws-widget/resolver'
import { CardNetWorkService } from './card-network.service'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'

@Component({
  selector: 'ws-widget-card-network',
  templateUrl: './card-network.component.html',
  styleUrls: ['./card-network.component.scss'],
})
export class CardNetworkComponent extends WidgetBaseComponent

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
  deptpeopleSpinner = false
  newPeopleSpinner = false

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
    this.router.navigate(['/app/person-profile', (user.userId || user.id)])
    // this.router.navigate(['/app/person-profile'], { queryParams: { emailId: } })

  }
  goToUserProfileForSearch(user: any) {
    this.router.navigate(['/app/person-profile', user.wid])
    // this.router.navigate(['/app/person-profile'], { queryParams: { emailId:  } })

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
    this.newPeopleSpinner = true

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
    this.newPeopleSpinner = false
  }
  getAllDepartmentUsers() {
    const departmentName = this.configurationsService.userProfile && this.configurationsService.userProfile.departmentName
    this.deptpeopleSpinner = true
    this.deptUserReq = {
      limit: 50,
      offset: 0,
      department: departmentName || '',
      intervalInDays: 7,
      type: 'deptUsers',
    }
    this.cardNetworkService.fetchLatestUserInfo(this.deptUserReq).subscribe(data => {
      if (Array.isArray(data.users) && data.users.length) {
        this.departmentUserArray = data.users
      } else {
        this.departmentUserArray = []
      }
      this.deptpeopleSpinner = true
    })

  }
  getSearchResult() {
    this.cardNetworkService.fetchSearchUserInfo(this.nameFilter.trim()).subscribe(data => {
      this.searchResultUserArray = data
      this.searchSpinner = false

    })

  }
}
