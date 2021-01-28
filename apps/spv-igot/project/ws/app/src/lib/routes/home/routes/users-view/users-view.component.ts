
import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core'
import { NSProfileDataV2 } from '../../models/profile-v2.model'
import { MatDialog } from '@angular/material/dialog'
import { ActivatedRoute, Router } from '@angular/router'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'
/* tslint:disable */
import _ from 'lodash'
import { UsersService } from '../../services/users.service'
import { MatSnackBar } from '@angular/material/snack-bar'
import { environment } from 'src/environments/environment'

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
  currentFilter = 'active'
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
  fullUserData: any = []

  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    // private discussService: DiscussService,
    private router: Router,
    private usersService: UsersService,
    private configSvc: ConfigurationsService,
    private snackBar: MatSnackBar,
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

  ngAfterViewInit() {
    // this.elementPosition = this.menuElement.nativeElement.parentElement.offsetTop
  }
  tEIDTableTableAction() {

  }
  fetchUserDetails() {
  }
  fetchConnectionDetails() {

  }
  onCreateClick() {
    this.router.navigate([`/app/users/create-user`])
  }

  onRoleClick() {
    // this.router.navigate([`/app/users/${user.userId}/details`])
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
        _.set(user, 'isActive', true)
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
        _.set(user, 'isBlocked', false)
        _.set(user, 'roles', _.map(_.get($event.row, 'role'), i => i.roleName))
        this.usersService.deActiveUser(user).subscribe(response => {
          if (response) {
            this.getAllUsers()
            this.snackBar.open('Updated successfully !')
          }
        })
        break
    }
  }
  getAllUsers() {
    this.usersService.getAllUsers().subscribe(data => {
      this.usersData = data
      this.filter(this.currentFilter)
    })
  }
  filter(key: string) {
    const activeUsersData: any[] = []
    const blockedUsersData: any[] = []
    const inactiveUsersData: any[] = []
    if (this.usersData.active_users && this.usersData.active_users.length > 0) {
      this.usersData.active_users.forEach((user: any) => {
        if (this.currentUser !== user.userId) {
          activeUsersData.push({
            fullname: user ? `${user.firstName} ${user.lastName}` : null,
            email: user.emailId,
            role: user.roleInfo,
            userId: user.userId,
            active: user.active,
            blocked: user.blocked,
          })
        }
      })
    }

    if (this.usersData.blocked_users && this.usersData.blocked_users.length > 0) {
      this.usersData.blocked_users.forEach((user: any) => {
        if (this.currentUser !== user.userId) {
          blockedUsersData.push({
            fullname: user ? `${user.firstName} ${user.lastName}` : null,
            email: user.emailId,
            role: user.roleInfo,
            userId: user.userId,
            active: user.active,
            blocked: user.blocked,
          })
        }
      })
    }
    if (this.usersData.inActive_users && this.usersData.inActive_users.length > 0) {
      this.usersData.inActive_users.forEach((user: any) => {
        if (this.currentUser !== user.userId) {
          inactiveUsersData.push({
            fullname: user ? `${user.firstName} ${user.lastName}` : null,
            email: user.emailId,
            role: user.roleInfo,
            userId: user.userId,
            active: user.active,
            blocked: user.blocked,
          })
        }
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
}
