import { Component, Input, OnInit } from '@angular/core'
import { Router } from '@angular/router'
import { NsWidgetResolver, WidgetBaseComponent } from '@ws-widget/resolver'
@Component({
  selector: 'ws-widget-card-hubs-list',
  templateUrl: './card-hubs-list.component.html',
  styleUrls: ['./card-hubs-list.component.scss'],
})
export class CardHubsListComponent extends WidgetBaseComponent

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

  constructor(private router: Router) {
    super()
  }

  hubsList = [{ path: '/app/search/learning', hubname: 'Learn', desc: 'Shape your Skills with hunderds-of online course', icon: 'test' },
  { path: '/app/discuss/home', hubname: 'Discuss', desc: 'Have a question? Have an Idea? discuss with your peers', icon: 'test' },
  { path: '/page/network', hubname: 'Network', desc: 'Connect with Interesting people, see what they are upto.', icon: 'test' },
  { path: '/page/learn', hubname: 'Carrier', desc: 'Explore the carrier option grow into the path you are', icon: 'test' },
  { path: '/app/notifications', hubname: 'News', desc: 'Keep you self update with the latest news from IGOT', icon: 'test' }]

  ngOnInit() {

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

    }

  }
}
