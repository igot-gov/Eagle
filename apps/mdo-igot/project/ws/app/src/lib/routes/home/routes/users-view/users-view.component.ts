import { Component, OnDestroy, OnInit } from '@angular/core'
import { NSProfileDataV2 } from '../../models/profile-v2.model'
import { MatDialog } from '@angular/material/dialog'
import { ActivatedRoute, Router } from '@angular/router'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'
import { UsersService } from '../../../users/services/users.service'
/* tslint:disable */
import _ from 'lodash'
import { environment } from 'src/environments/environment'
import { ITableData } from '../../../../../../../../../library/ws-widget/collection/src/public-api'
import { MatSnackBar } from '@angular/material'

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
  currentFilter = 'active'
  discussionList!: any
  discussProfileData!: any
  portalProfile!: NSProfileDataV2.IProfile
  userDetails: any
  location!: string | null
  tabs: any
  // tabsData: NSProfileDataV2.IProfileTab[]
  currentUser!: string | null
  connectionRequests!: any[]
  tabledata!: ITableData
  data: any = []
  usersData!: any

  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar,
    // private discussService: DiscussService,
    private configSvc: ConfigurationsService,
    // private networkV2Service: NetworkV2Service,
    // private profileV2Svc: ProfileV2Service
    private usersService: UsersService
  ) {
    this.Math = Math
    this.currentUser = this.configSvc.userProfile && this.configSvc.userProfile.userId
    // this.tabsData = this.route.parent && this.route.parent.snapshot.data.pageData.data.tabs || []
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
      actions: [],
      columns: [
        { displayName: 'Full Name', key: 'fullname' },
        { displayName: 'Email', key: 'email' },
        { displayName: 'Position', key: 'position' },
      ],
      needCheckBox: false,
      needHash: false,
      sortColumn: 'fullName',
      sortState: 'asc',
      needUserMenus: true,
    }

    this.getAllUsers()
  }

  filter(key: string) {
    const activeUsersData: any[] = []
    const blockedUsersData: any[] = []
    const inactiveUsersData: any[] = []
    if (this.usersData.active_users && this.usersData.active_users.length > 0) {
      this.usersData.active_users.forEach((user: any) => {
        const currentRole = []
        user.roleInfo.forEach((element: { roleName: any }) => {
          currentRole.push(element.roleName)
        })
        activeUsersData.push({
          fullname: user ? `${user.firstName} ${user.lastName}` : null,
          email: user.emailId,
          role: user.roleInfo,
          userId: user.userId,
          active: user.active,
          blocked: user.blocked,
        })
      })
    }

    if (this.usersData.blocked_users && this.usersData.blocked_users.length > 0) {
      this.usersData.blocked_users.forEach((user: any) => {
        blockedUsersData.push({

          fullname: user ? `${user.firstName} ${user.lastName}` : null,
          email: user.emailId,
          role: user.roleInfo,
          userId: user.userId,
          active: user.active,
          blocked: user.blocked,
        })
      })
    }
    if (this.usersData.inActive_users && this.usersData.inActive_users.length > 0) {
      this.usersData.inActive_users.forEach((user: any) => {
        inactiveUsersData.push({
          fullname: user ? `${user.firstName} ${user.lastName}` : null,
          email: user.emailId,
          role: user.roleInfo,
          userId: user.userId,
          active: user.active,
          blocked: user.blocked,
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
      this.filter(this.currentFilter)
    })
  }

  onCreateClick() {
    this.router.navigate([`/app/users/create-user`])
  }

  onRoleClick(user: any) {
    this.router.navigate([`/app/users/${user.userId}/details`])
  }
  menuActions($event: { action: string, row: any }) {
    const user = { userId: _.get($event.row, 'userId') }
    _.set(user, 'deptId', _.get(this.usersData, 'id'))
    _.set(user, 'isBlocked', _.get($event.row, 'blocked'))
    _.set(user, 'isActive', _.get($event.row, 'active'))

    switch ($event.action) {
      case 'showOnKarma':
        window.open(`${environment.karmYogiPath}/app/person-profile/${user.userId}`)
        break
      case 'block':
        _.set(user, 'isBlocked', true)
        _.set(user, 'isActive', false)
        _.set(user, 'roles', _.map(_.get($event.row, 'role'), i => i.roleName))
        this.usersService.blockUser(user).subscribe(response => {
          if (response) {
            this.getAllUsers()
            this.snackBar.open('Updated successfully !')
          }
        })
        break
      case 'unblock':
        _.set(user, 'isBlocked', false)
        _.set(user, 'roles', _.map(_.get($event.row, 'role'), i => i.roleName))
        this.usersService.blockUser(user).subscribe(response => {
          if (response) {
            this.getAllUsers()
            this.snackBar.open('Updated successfully !')
          }
        })
        break
      case 'deactive':
        _.set(user, 'isActive', false)
        _.set(user, 'roles', _.map(_.get($event.row, 'role'), i => i.roleName))
        this.usersService.deActiveUser(user).subscribe(response => {
          if (response) {
            this.getAllUsers()
            this.snackBar.open('Updated successfully !')
          }
        })
        break
      case 'active':
        _.set(user, 'isActive', true)
        _.set(user, 'roles', _.map(_.get($event.row, 'role'), i => i.roleName))
        this.usersService.deActiveUser(user).subscribe(response => {
          if (response) {
            this.getAllUsers()
            this.snackBar.open('Updated successfully !')
          }
        })
        break
      //   case 'delete':
      //     _.set(user, 'isBlocked', false)
      //     this.usersSvc.deleteUser(user)
      //     break
    }
  }
}
