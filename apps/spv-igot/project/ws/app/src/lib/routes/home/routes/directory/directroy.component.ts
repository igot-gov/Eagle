
import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core'
import { NSProfileDataV2 } from '../../models/profile-v2.model'
import { MatDialog } from '@angular/material/dialog'
import { ActivatedRoute } from '@angular/router'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'
/* tslint:disable */
import _ from 'lodash'

@Component({
  selector: 'ws-app-directory',
  templateUrl: './directory.component.html',
  styleUrls: ['./directory.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 margin-top-l' },
  /* tslint:enable */
})
export class DirectoryViewComponent implements OnInit, AfterViewInit, OnDestroy {
  /* tslint:disable */
  Math: any
  /* tslint:enable */
  currentFilter = 'underreview'
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
    private configSvc: ConfigurationsService,
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
      actions: [{ name: 'Approve', label: 'Approve', icon: 'remove_red_eye', type: 'Approve' },
      { name: 'Reject', label: 'Reject', icon: 'remove_red_eye', type: 'Reject' }],
      columns: [
        { displayName: 'MDO', key: 'mdo' },
        { displayName: 'Type', key: 'type' },
        { displayName: 'Users', key: 'user' },
      ],
      needCheckBox: false,
      needHash: false,
      sortColumn: '',
      sortState: 'asc',
    }
    this.data = [{

      mdo: 'Ministry of Education',
      type: 'Ministry',
      user: '121',
    },
    {
      mdo: 'Coffee Board',
      type: 'Board',
      user: '225',
    },
    {

      mdo: 'Ministry of Civil Aviation',
      type: 'Ministry',
      user: '652',
    },
    {
      mdo: 'Department of Defence',
      type: 'Department',
      user: '231',
    },
    {
      mdo: 'Indian Council for Agricultural Research',
      type: 'Department',
      user: '231',
    },]
  }
  ngAfterViewInit() {
  }
  tEIDTableTableAction() {

  }
  fetchUserDetails() {
  }
  fetchConnectionDetails() {
  }

  filter(key: string | 'timestamp' | 'best' | 'saved') {
    if (key) {
      this.currentFilter = key
      switch (key) {
        case 'mdo':
          this.data = [{

            mdo: 'Ministry of Education',
            type: 'Ministry',
            user: '121',
          },
          {
            mdo: 'Coffee Board',
            type: 'Board',
            user: '225',
          },
          {

            mdo: 'Ministry of Civil Aviation',
            type: 'Ministry',
            user: '652',
          },
          {
            mdo: 'Department of Defence',
            type: 'Department',
            user: '231',
          },
          {
            mdo: 'Indian Council for Agricultural Research',
            type: 'Department',
            user: '231',
          },]
          break
        case 'cbp':
          this.data = [{

            mdo: 'Ministry of Education',
            type: 'Ministry',
            user: '121',
          },
          {
            mdo: 'Coffee Board',
            type: 'Board',
            user: '225',
          },
          {
            mdo: 'Indian Council for Agricultural Research',
            type: 'Department',
            user: '231',
          },]
          break
        case 'csp':
          this.data = [{

            mdo: 'Ministry of Education',
            type: 'Ministry',
            user: '121',
          },
          {
            mdo: 'Indian Council for Agricultural Research',
            type: 'Department',
            user: '231',
          },]
          break
        case 'frac':
          this.data = [
            {
              mdo: 'Indian Council for Agricultural Research',
              type: 'Department',
              user: '231',
            },]
          break
        case 'fraccr':
          this.data = [{

            mdo: 'Ministry of Education',
            type: 'Ministry',
            user: '121',
          }]
          break
        default:
          this.discussionList = _.uniqBy(this.discussProfileData.latestPosts, 'tid')
          break
      }
    }
  }

  // need to enhance

}
