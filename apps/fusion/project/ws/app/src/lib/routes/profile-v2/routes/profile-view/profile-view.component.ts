
import { AfterViewInit, Component, ElementRef, HostListener, OnDestroy, OnInit, ViewChild } from '@angular/core'
import { NSProfileDataV2 } from '../../models/profile-v2.model'
import { MatDialog } from '@angular/material/dialog'
import { ActivatedRoute } from '@angular/router'
import { DiscussService } from '../../../discuss/services/discuss.service'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'
// import { ProfileV2Service } from '../../services/profile-v2.servive'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */

@Component({
  selector: 'app-profile-view',
  templateUrl: './profile-view.component.html',
  styleUrls: ['./profile-view.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 margin-top-l' },
  /* tslint:enable */
})
export class ProfileViewComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('stickyMenu', { static: true }) menuElement!: ElementRef
  sticky = false
  elementPosition: any
  currentFilter = 'timestamp'
  discussionList!: any
  discussProfileData!: any
  portalProfile!: NSProfileDataV2.IProfile
  userDetails: any
  location!: string | null
  tabs: any
  tabsData: NSProfileDataV2.IProfileTab[]
  currentUser!: string | null
  @HostListener('window:scroll', ['$event'])
  handleScroll() {
    const windowScroll = window.pageYOffset
    if (windowScroll >= this.elementPosition) {
      this.sticky = true
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
    this.tabsData = this.route.parent && this.route.parent.snapshot.data.pageData.data.tabs || []
    this.tabs = this.route.data.subscribe(data => {
      this.portalProfile = data.profile
        && data.profile.data
        && data.profile.data.length > 0
        && data.profile.data[0]
    })
    this.currentUser = configSvc.userProfile && configSvc.userProfile.userId
  }
  ngOnDestroy() {
    if (this.tabs) {
      this.tabs.unsubscribe()
    }
  }
  ngOnInit() {
    // if (this.portalProfile && this.portalProfile.userId) {
    //   this.fetchUserDetails(this.portalProfile.userId)
    // } else {
    //   const me = this.configSvc.userProfile && this.configSvc.userProfile.userId || null
    //   if (me) {
    //     this.fetchUserDetails(me)
    //   }
    // }
  }
  ngAfterViewInit() {
    this.elementPosition = this.menuElement.nativeElement.parentElement.offsetTop
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
