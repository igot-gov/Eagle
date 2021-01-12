import { Component, OnInit } from '@angular/core'
import { ActivatedRoute, Router, NavigationEnd, Event } from '@angular/router'

@Component({
  selector: 'ws-app-basic-info',
  templateUrl: './basic-info.component.html',
  styleUrls: ['./basic-info.component.scss'],
})
export class BasicInfoComponent implements OnInit {
  basicInfo: any
  constructor(private activeRoute: ActivatedRoute, private router: Router) {
    this.router.events.subscribe((event: Event) => {
      if (event instanceof NavigationEnd) {
        const profileData = this.activeRoute.snapshot.data.profileData.data.result.UserProfile[0] || {}
        this.basicInfo = profileData.personalDetails
      }
    })
  }

  ngOnInit() { }
}
