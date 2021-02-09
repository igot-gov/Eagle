import { AfterViewInit, Component, OnInit, OnDestroy } from '@angular/core'
import { Router } from '@angular/router'
import { ITableData } from '../../../../../../../../../library/ws-widget/collection/src/public-api'
import { environment } from '../../../../../../../../../src/environments/environment'
import { UsersService } from '../../services/users.service'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */
@Component({
  selector: 'ws-app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss'],
})

export class UsersComponent implements OnInit, AfterViewInit, OnDestroy {
  tabledata!: ITableData
  data: any = []
  data2: any
  role: any
  private defaultSideNavBarOpenedSubscription: any

  constructor(private usersSvc: UsersService, private router: Router) { }
  ngOnInit() {
    const url = this.router.url.split('/')
    this.role = url[url.length - 2]
    this.fetchUsersWithRole()
    // int left blank
    this.tabledata = {
      actions: [{ name: 'Details', label: 'Details', icon: 'remove_red_eye', type: 'link' }],
      columns: [
        { displayName: 'Full name', key: 'fullName' },
        { displayName: 'Email', key: 'email' },
        { displayName: 'Position', key: 'position' },
        { displayName: 'Role', key: 'role' },
      ],
      needCheckBox: false,
      needHash: false,
      sortColumn: 'fullName',
      sortState: 'asc',
      needUserMenus: false,
    }
  }

  ngAfterViewInit() {
    // this.elementPosition = this.menuElement.nativeElement.parentElement.offsetTop
  }

  /* API call to get all roles*/
  fetchUsersWithRole() {
    this.usersSvc.getUsers(this.role).subscribe(res => {
      this.data2 = res
      this.data = res.users.map((user: any) => {
        return {
          fullName: `${user.first_name} ${user.last_name}`,
          email: user.email,
          position: user.department_name,
          role: this.role,
          wid: user.wid,
        }
      })
    })
  }

  ngOnDestroy() {
    if (this.defaultSideNavBarOpenedSubscription) {
      this.defaultSideNavBarOpenedSubscription.unsubscribe()
    }
  }
  menuActions($event: { action: string, row: any }) {
    const user = { userId: _.get($event.row, 'wid') }
    _.set(user, 'deptId', _.get(this.data2, 'id'))
    switch ($event.action) {
      case 'showOnKarma':
        window.open(`${environment.karmYogiPath}/app/person-profile/${user.userId}`)
        break
      case 'block':
        _.set(user, 'isBlocked', true)
        _.set(user, 'roles', _.map(_.get($event.row, 'roleInfo'), i => i.roleName))
        this.usersSvc.blockUser(user)
        break
      case 'unblock':
        _.set(user, 'isBlocked', false)
        _.set(user, 'roles', _.map(_.get($event.row, 'roleInfo'), i => i.roleName))
        this.usersSvc.blockUser(user)
        break
      case 'deactive':
        _.set(user, 'isActive', false)
        _.set(user, 'roles', _.map(_.get($event.row, 'roleInfo'), i => i.roleName))

        this.usersSvc.deActiveUser(user)
        break
      case 'active':
        _.set(user, 'isActive', true)
        _.set(user, 'roles', _.map(_.get($event.row, 'roleInfo'), i => i.roleName))
        this.usersSvc.deActiveUser(user)
        break
      //   case 'delete':
      //     _.set(user, 'isBlocked', false)
      //     this.usersSvc.deleteUser(user)
      //     break
    }
  }
}
