
import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core'
import { NSProfileDataV2 } from '../../models/profile-v2.model'
import { MatDialog } from '@angular/material/dialog'
import { ActivatedRoute } from '@angular/router'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'
/* tslint:disable */
import _ from 'lodash'
import { UserViewService } from './user-view.services'
interface IUser { fullname: string; email: string; type: string }

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
  fullUserData: any = []

  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    // private discussService: DiscussService,
    private configSvc: ConfigurationsService,
    private userViewServcie: UserViewService,
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
        { displayName: 'Full Name', key: 'fullname' },
        { displayName: 'Email', key: 'email' },
        { displayName: 'Type', key: 'type' },
      ],
      needCheckBox: false,
      needHash: false,
      sortColumn: '',
      sortState: 'asc',
    }
    this.getAllActiveUsersAPI()
  }

  getAllActiveUsersAPI() {
    this.userViewServcie.getAllDepartments().subscribe(res => {
      this.fullUserData = res
    })
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

  filter(key: string | 'timestamp' | 'best' | 'saved') {
    if (key) {
      this.currentFilter = key
      switch (key) {
        case 'underreview':
          this.data = this.getAllUserByKey(this.fullUserData.user_review)
          break
        case 'active':
          this.data = this.getAllUserByKey(this.fullUserData.active_users)
          break
        case 'inactive':
          this.data = this.getAllUserByKey(this.fullUserData.inActive_users)
          break
        case 'blocked':
          this.data = this.getAllUserByKey(this.fullUserData.blocked_users)
          break
        default:
          this.getAllActiveUsersAPI()
          break
      }
    }
  }
  getAllUserByKey(userObj: any) {
    if (userObj && userObj !== null && userObj !== undefined) {
      const tempArray: IUser[] = []
      userObj.forEach((users: any) => {
        const obj: IUser = {
          fullname: `${users.firstName} ${users.lastName}`,
          email: users.emailId,
          type: users.roleInfo.roleName,
        }
        tempArray.push(obj)
      })
      return tempArray
    }
    return []

  }

  // need to enhance

}
