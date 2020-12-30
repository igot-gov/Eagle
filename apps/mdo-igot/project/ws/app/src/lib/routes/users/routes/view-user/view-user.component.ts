import { AfterViewInit, Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core'
import { ActivatedRoute, Router, Event, NavigationEnd } from '@angular/router'

@Component({
  selector: 'ws-app-view-user',
  templateUrl: './view-user.component.html',
  styleUrls: ['./view-user.component.scss'],
})
export class ViewUserComponent implements OnInit, AfterViewInit {
  tabsData!: any[]
  currentTab = 'personalInfo'
  sticky = false
  elementPosition: any
  basicInfo: any
  fullname = ''
  academicDetails: any
  professionalDetails: any
  employmentDetails: any
  skillDetails: any
  interests: any
  @ViewChild('stickyMenu', { static: true }) menuElement!: ElementRef

  @HostListener('window:scroll', ['$event'])
  handleScroll() {
    const windowScroll = window.pageYOffset
    if (windowScroll >= this.elementPosition) {
      this.sticky = true
    } else {
      this.sticky = false
    }
  }

  constructor(private activeRoute: ActivatedRoute, private router: Router) {
    this.router.events.subscribe((event: Event) => {
      if (event instanceof NavigationEnd) {
        const profileData = this.activeRoute.snapshot.data.profileData.data.result.UserProfile[0] || {}
        this.basicInfo = profileData.personalDetails
        this.academicDetails = profileData.academics
        this.professionalDetails = profileData.professionalDetails[0]
        this.employmentDetails = profileData.employmentDetails
        this.skillDetails = profileData.skills
        this.interests = profileData.interests
        this.fullname = `${this.basicInfo.firstname} ${this.basicInfo.surname}`
      }
    })
  }

  ngOnInit() {
    this.tabsData = [
      {
        name: 'Personal details',
        key: 'personalInfo',
        render: true,
        enabled: true,
      },
      {
        name: 'Academics',
        key: 'academics',
        render: true,
        enabled: true,
      },
      {
        name: 'Professional details',
        key: 'profdetails',
        render: true,
        enabled: true,
      },
      {
        name: 'Certification and skills',
        key: 'skills',
        render: true,
        enabled: true,
      }]
  }

  ngAfterViewInit() {
    this.elementPosition = this.menuElement.nativeElement.parentElement.offsetTop
  }

  onSideNavTabClick(id: string) {
    this.currentTab = id
    const el = document.getElementById(id)
    if (el != null) {
      el.scrollIntoView({ behavior: 'smooth', block: 'start', inline: 'start' })
    }
  }

}
