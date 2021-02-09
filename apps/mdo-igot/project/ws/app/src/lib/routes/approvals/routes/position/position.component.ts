import { Component, OnInit } from '@angular/core'
import { ActivatedRoute, Router, NavigationEnd, Event } from '@angular/router'

@Component({
  selector: 'ws-app-position',
  templateUrl: './position.component.html',
  styleUrls: ['./position.component.scss'],
})
export class PositionComponent implements OnInit {
  professionalDetails: any
  employmentDetails: any
  constructor(private activeRoute: ActivatedRoute, private router: Router) {
    this.router.events.subscribe((event: Event) => {
      if (event instanceof NavigationEnd) {
        const profileData = this.activeRoute.snapshot.data.profileData.data.result.UserProfile[0] || {}
        this.professionalDetails = profileData.professionalDetails[0]
        this.employmentDetails = profileData.employmentDetails
      }
    })
  }

  ngOnInit() { }
}
