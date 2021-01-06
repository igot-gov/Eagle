
import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core'
import { NSProfileDataV2 } from '../../models/profile-v2.model'
import { MatDialog } from '@angular/material/dialog'
import { ActivatedRoute, Router } from '@angular/router'
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
  currentFilter = 'MDO'
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
  currentDepartment!: string
  data: any = []
  wholeData: any = []
  departmentHearders: any = []

  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private configSvc: ConfigurationsService,
    private directoryService: DirectoryService,
    private router: Router
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
  }
  getDepartmentHeader() {
    this.wholeData.forEach((head: { deptTypeInfo: { deptType: void } }) => {
      if (this.departmentHearders.indexOf(head.deptTypeInfo.deptType) === -1) {
        this.departmentHearders.push(head.deptTypeInfo.deptType)
      }
    })
    const index = this.departmentHearders.indexOf('SPV')
    if (index > -1) {
      this.departmentHearders.splice(index, 1)
    }
    this.getDepartDataByKey(this.departmentHearders[0])
    this.tabledata = {
      actions: [{ name: 'Edit', label: 'Edit info', icon: 'remove_red_eye', type: 'button' }],
      columns: [
        { displayName: this.currentFilter, key: 'mdo' },
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
      this.wholeData = res
      this.getDepartmentHeader()
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
  onRoleClick(role: any) {
    this.router.navigate([`/app/roles/${role.id}/users`])
  }
  filter(key: string | 'timestamp' | 'best' | 'saved') {
    this.getDepartDataByKey(key)
  }
  getDepartDataByKey(key: string) {
    if (key) {
      this.currentFilter = key
      this.currentDepartment = key
      const filteredData: any[] = []
      this.wholeData.map((dept: any) => {
        if (dept.deptTypeInfo.deptType === this.currentFilter) {
          filteredData.push(dept)
        }
      })
      this.data = filteredData.map((dept: any) => {
        console.log(dept)
        return {
          id: dept.id,
          mdo: dept.deptName,
          type: dept.deptTypeInfo.deptSubType,
          user: dept.noOfUsers,
          head: dept.headquarters,
          typeid: dept.deptTypeInfo.id,
        }
      })

    }
  }
  actionClick(clickedData: any) {
    this.router.navigate([`/app/roles/${this.currentFilter}/basicinfo`, { 'data': JSON.stringify(clickedData) }])

  }

}
