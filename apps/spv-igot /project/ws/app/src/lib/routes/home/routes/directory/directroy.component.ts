
import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core'
import { NSProfileDataV2 } from '../../models/profile-v2.model'
import { MatDialog } from '@angular/material/dialog'
import { ActivatedRoute } from '@angular/router'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'
/* tslint:disable */
import _ from 'lodash'
import { DirectoryService } from './directory.services'

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
    private directoryService: DirectoryService,
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

    this.getAllDepartmentsAPI()
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
  }
  getAllDepartmentsAPI() {
    this.directoryService.getAllDepartments().subscribe(res => {
      this.data = res.map((dept: any) => {
        return {
          mdo: dept.deptName,
          type: dept.deptTypeInfo.deptType,
          user: dept.noOfUsers,
        }
      })
    })
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
          this.getAllDepartmentsAPI()
          break
        case 'cbp':
          this.getAllDepartmentsAPI()
          break
        case 'csp':
          this.getAllDepartmentsAPI()
          break
        case 'frac':
          this.getAllDepartmentsAPI()
          break
        case 'fraccr':
          this.getAllDepartmentsAPI()
          break
        default:
          this.getAllDepartmentsAPI()
          break
      }
    }
  }
}
