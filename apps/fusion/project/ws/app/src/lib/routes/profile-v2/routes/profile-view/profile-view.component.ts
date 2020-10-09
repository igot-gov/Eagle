
import { AfterViewInit, Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core'
import { NSProfileData } from '../../models/profile-v2.model'
import { MatDialog } from '@angular/material/dialog'
import { ActivatedRoute } from '@angular/router'
// import { ProfileV2Service } from '../../services/profile-v2.servive'
/* tslint:disable */
import _ from 'lodash'
import { DiscussService } from '../../../discuss/services/discuss.service'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'
/* tslint:enable */

@Component({
  selector: 'app-profile-view',
  templateUrl: './profile-view.component.html',
  styleUrls: ['./profile-view.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 margin-top-l' },
  /* tslint:enable */
})
export class ProfileViewComponent implements OnInit, AfterViewInit {
  @ViewChild('stickyMenu', { static: true }) menuElement!: ElementRef
  sticky = false
  elementPosition: any
  currentFilter = 'timestamp'
  discussionList!: any
  discussProfileData!: any
  portalProfile: NSProfileData.IPortalProfile
  userDetails: any
  location!: string | null
  tabs: any
  tabsData: any
  @HostListener('window:scroll', ['$event'])
  handleScroll() {
    const windowScroll = window.pageYOffset
    if (windowScroll >= this.elementPosition) {
      this.sticky = false
    } else {
      this.sticky = false
    }
  }

  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private discussService: DiscussService,
    private configSvc: ConfigurationsService,
    // private profileV2Svc: ProfileV2Service
  ) {
    this.tabs = this.route.data.subscribe(data => {
      if (data && data.pageData && data.pageData.data) {
        this.tabsData = data.pageData.data.tabs || []
      }
    })
    this.portalProfile = this.route.snapshot.data.profile.data[0]

  }
  ngOnInit() {
    if (this.portalProfile && this.portalProfile.wid) {
      this.fetchUserDetails(this.portalProfile.wid)
    } else {
      const me = this.configSvc.userProfile && this.configSvc.userProfile.userId || null
      if (me) {
        this.fetchUserDetails(me)
      }
    }
  }
  ngAfterViewInit() {
    this.elementPosition = this.menuElement.nativeElement.offsetTop
  }
  fetchUserDetails(wid: string) {
    if (wid) {
      this.discussService.fetchProfileInfo(wid).subscribe((response: any) => {
        if (response) {
          this.discussProfileData = response
          this.discussionList = _.uniqBy(this.discussProfileData.latestPosts, 'tid') || []
        }
      })
    }
  }
  filter(key: string | 'timestamp' | 'best' | 'saved') {
    if (key) {
      this.currentFilter = key
      switch (key) {
        case 'timestamp':
          this.discussionList = _.uniqBy(this.discussProfileData.latestPosts, 'tid')
          break
        case 'best':
          this.discussionList = _.uniqBy(this.discussProfileData.bestPosts, 'tid')
          break
        case 'saved':
          this.discussService.fetchSaved().subscribe((response: any) => {
            if (response) {
              this.discussionList = _.uniqBy(response.posts, 'tid')
            } else {
              this.discussionList = []
            }
          },
            // tslint:disable-next-line
            () => {
              this.discussionList = []
            })
          break
        default:
          this.discussionList = _.uniqBy(this.discussProfileData.latestPosts, 'tid')
          break
      }
    }
  }

}
