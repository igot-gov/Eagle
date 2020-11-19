
import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core'
import { NSProfileDataV2 } from '../../models/profile-v2.model'
import { MatDialog } from '@angular/material/dialog'
import { ActivatedRoute } from '@angular/router'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'
/* tslint:disable */
import _ from 'lodash'

@Component({
  selector: 'ws-app-users-view',
  templateUrl: './users-view.component.html',
  styleUrls: ['./users-view.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 margin-top-l' },
  /* tslint:enable */
})
export class UsersViewComponent implements OnInit, AfterViewInit, OnDestroy {
  /* tslint:disable */
  Math: any
  /* tslint:enable */
  currentFilter = 'timestamp'
  discussionList!: any
  discussProfileData!: any
  portalProfile!: NSProfileDataV2.IProfile
  userDetails: any
  location!: string | null
  tabs: any
  tabsData: NSProfileDataV2.IProfileTab[]
  currentUser!: string | null
  connectionRequests!: any[]
  tabledata: any = []
  data: any = []

  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    // private discussService: DiscussService,
    private configSvc: ConfigurationsService,
    // private networkV2Service: NetworkV2Service,
    // private profileV2Svc: ProfileV2Service
  ) {
    this.Math = Math
    this.currentUser = this.configSvc.userProfile && this.configSvc.userProfile.userId
    this.tabsData = this.route.parent && this.route.parent.snapshot.data.pageData.data.tabs || []
    this.tabs = this.route.data.subscribe(data => {
      this.portalProfile = data.profile
        && data.profile.data
        && data.profile.data.length > 0
        && data.profile.data[0]
      this.decideAPICall()
    })
  }
  decideAPICall() {
  }
  ngOnDestroy() {
    if (this.tabs) {
      this.tabs.unsubscribe()
    }
  }
  ngOnInit() {
    // int left blank
    this.tabledata = {
      columns: [
        {
          displayName: 'Full Name',
          key: 'col1key',
          isList: false,
          prop: 'testprop1',
        }, {
          displayName: 'Email',
          key: 'col2key',
          isList: false,
          prop: 'testprop2',
        }, {
          displayName: 'Type',
          key: 'col3key',
          isList: false,
          prop: 'testprop3',
        },
      ],
      actions: ['act1', 'act2', 'act3'],
      needHash: false,
      needCheckBox: false,
      sortState: 'asc',
      sortColumn: 'col1key',
    }
    this.data = [{
      col1key: 'User 1',
      col2key: 'a@gmail.com',
      col3key: 'permanent',
    }, {
      col1key: 'User 1',
      col2key: 'a@gmail.com',
      col3key: 'permanent',
    }, {
      col1key: 'User 1',
      col2key: 'a@gmail.com',
      col3key: 'permanent',
    }, {
      col1key: 'User 1',
      col2key: 'a@gmail.com',
      col3key: 'permanent',
    }, {
      col1key: 'User 1',
      col2key: 'a@gmail.com',
      col3key: 'permanent',
    }, {
      col1key: 'User 1',
      col2key: 'a@gmail.com',
      col3key: 'permanent',
    }, {
      col1key: 'User 1',
      col2key: 'a@gmail.com',
      col3key: 'permanent',
    }, {
      col1key: 'User 1',
      col2key: 'a@gmail.com',
      col3key: 'permanent',
    }, {
      col1key: 'User 1',
      col2key: 'a@gmail.com',
      col3key: 'permanent',
    }, {
      col1key: 'User 1',
      col2key: 'a@gmail.com',
      col3key: 'permanent',
    }, {
      col1key: 'User 1',
      col2key: 'a@gmail.com',
      col3key: 'permanent',
    }, {
      col1key: 'User 1',
      col2key: 'a@gmail.com',
      col3key: 'permanent',
    }, {
      col1key: 'User 1',
      col2key: 'a@gmail.com',
      col3key: 'permanent',
    }, {
      col1key: 'User 1',
      col2key: 'a@gmail.com',
      col3key: 'permanent',
    }]
  }
  ngAfterViewInit() {
    // this.elementPosition = this.menuElement.nativeElement.parentElement.offsetTop
  }
  fetchUserDetails() {
    // if (wid) {
    //   this.discussService.fetchProfileInfo(wid).subscribe((response: any) => {
    //     if (response) {
    //       this.discussProfileData = response
    //       this.discussionList = _.uniqBy(_.filter(this.discussProfileData.posts, p => _.get(p, 'isMainPost') === true), 'tid') || []
    //     }
    //   })
    // }
  }
  fetchConnectionDetails() {
    // this.networkV2Service.fetchAllConnectionEstablishedById(wid).subscribe(
    //   (data: any) => {
    //     this.connectionRequests = data.result.data
    //   },
    //   (_err: any) => {
    //     // this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
    //   })
  }

  filter(key: string | 'timestamp' | 'best' | 'saved') {
    if (key) {
      this.currentFilter = key
      switch (key) {
        case 'timestamp':
          this.discussionList = _.uniqBy(_.filter(this.discussProfileData.posts, p => _.get(p, 'isMainPost') === true), 'tid')
          break
        case 'best':
          this.discussionList = _.uniqBy(this.discussProfileData.bestPosts, 'tid')
          break
        case 'saved':
          break
        default:
          this.discussionList = _.uniqBy(this.discussProfileData.latestPosts, 'tid')
          break
      }
    }
  }

}
