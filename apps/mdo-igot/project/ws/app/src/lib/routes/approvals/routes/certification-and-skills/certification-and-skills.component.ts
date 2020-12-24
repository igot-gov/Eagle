import { Component, OnInit } from '@angular/core'
import { ActivatedRoute, Router, NavigationEnd, Event } from '@angular/router'

@Component({
  selector: 'ws-app-certification-and-skills',
  templateUrl: './certification-and-skills.component.html',
  styleUrls: ['./certification-and-skills.component.scss'],
})
export class CertificationAndSkillsComponent implements OnInit {

  skillDetails: any
  interests: any

  constructor(private activeRoute: ActivatedRoute, private router: Router) {
    this.router.events.subscribe((event: Event) => {
      if (event instanceof NavigationEnd) {
        const profileData = this.activeRoute.snapshot.data.profileData.data.result.UserProfile[0] || {}
        this.skillDetails = profileData.skills
        this.interests = profileData.interests
      }
    })
  }

  ngOnInit() { }

}
