
import { Component, OnDestroy, OnInit } from '@angular/core'
import { NSProfileDataV2 } from '../../models/profile-v2.model'
import { MatDialog } from '@angular/material/dialog'
import { ActivatedRoute, Router } from '@angular/router'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'
import { UsersService } from '../../../users/services/users.service'
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
export class UsersViewComponent implements OnInit, OnDestroy {
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
  usersData!: any

  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private router: Router,
    // private discussService: DiscussService,
    private configSvc: ConfigurationsService,
    // private networkV2Service: NetworkV2Service,
    // private profileV2Svc: ProfileV2Service
    private usersService: UsersService
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
    this.tabledata = {
      // actions: [{ name: 'Approve', label: 'Approve', icon: 'remove_red_eye', type: 'Approve' },
      // { name: 'Reject', label: 'Reject', icon: 'remove_red_eye', type: 'Reject' }],
      columns: [
        { displayName: 'Full Name', key: 'fullname' },
        { displayName: 'Email', key: 'email' },
        { displayName: 'Position', key: 'position' },
        { displayName: 'Role', key: 'role' },
      ],
      needCheckBox: false,
      needHash: false,
      sortColumn: '',
      sortState: 'asc',
    }

    this.getAllUsers()
  }

  filter(key: string | 'timestamp' | 'best' | 'saved') {
    const activeUsersData: any[] = []
    const blockedUsersData: any[] = []
    const inactiveUsersData: any[] = []
    if (this.usersData.active_users && this.usersData.active_users.length > 0) {
      this.usersData.active_users.forEach((user: any) => {
        activeUsersData.push({
          fullname: user ? `${user.firstName} ${user.lastName}` : null,
          email: user.emailId,
          role: user.roleInfo.roleName,
          userId: user.userId,
        })
      })
    }

    if (this.usersData.blocked_users && this.usersData.blocked_users.length > 0) {
      this.usersData.blocked_users.forEach((user: any) => {
        blockedUsersData.push({
          fullname: user ? `${user.firstName} ${user.lastName}` : null,
          email: user.emailId,
          role: user.roleInfo.roleName,
          userId: user.userId,
        })
      })
    }
    if (this.usersData.inActive_users && this.usersData.inActive_users.length > 0) {
      this.usersData.inActive_users.forEach((user: any) => {
        inactiveUsersData.push({
          fullname: user ? `${user.firstName} ${user.lastName}` : null,
          email: user.emailId,
          role: user.roleInfo.roleName,
          userId: user.userId,
        })
      })
    }

    if (key) {
      this.currentFilter = key
      switch (key) {
        case 'active':
          this.data = activeUsersData
          break
        case 'inactive':
          this.data = inactiveUsersData
          break
        case 'blocked':
          this.data = blockedUsersData
          break
        default:
          this.data = activeUsersData
          break
      }
    }
  }

  getAllUsers() {
    this.usersService.getAllUsers().subscribe(data => {
      this.usersData = data
      this.filter('active')
    })
  }

  onCreateClick() {
    this.router.navigate([`/app/users/create-user`])
  }

  onRoleClick(user: any) {
    this.router.navigate([`/app/users/${user.userId}/details`])
  }
}
