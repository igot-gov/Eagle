import { AfterViewInit, Component, OnInit, OnDestroy, ElementRef, HostListener, ViewChild } from '@angular/core'
import { Router, ActivatedRoute } from '@angular/router'
import { ProfileV2Service } from '../../../home/services/home.servive'
import { UsersService } from '../../services/users.service'

@Component({
  selector: 'ws-app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss'],
})

export class UsersComponent implements OnInit, AfterViewInit, OnDestroy {
  tabledata: any = []
  currentTab = 'users'
  data: any = []
  role: any
  tabsData!: any[]
  elementPosition: any
  sticky = false
  basicInfo: any
  id!: string
  currentDept!: string
  private defaultSideNavBarOpenedSubscription: any
  @ViewChild('stickyMenu', { static: true }) menuElement!: ElementRef

  @HostListener('window:scroll', ['$event'])
  handleScroll() {
    const windowScroll = window.pageYOffset
    if (windowScroll >= this.elementPosition) {
      this.sticky = true
    } else {
      this.sticky = false
    }
  }

  constructor(private usersSvc: UsersService, private router: Router, private route: ActivatedRoute, private profile: ProfileV2Service) {
  }
  ngOnInit() {
    this.tabsData = [
      {
        name: 'Users',
        key: 'users',
        render: true,
        enabled: true,
      },
      {
        name: 'Roles And Access',
        key: 'rolesandaccess',
        render: true,
        enabled: true,
      }]

    const url = this.router.url.split('/')
    this.role = url[url.length - 2]
    this.route.params.subscribe(params => {
      this.id = params['id']
      this.id = params['roleId']
      this.currentDept = params['currentDept']
      if (this.id === 'SPV ADMIN') {
        this.getAllActiveUsers()
      } else {
        this.getAllActiveUsersByDepartmentId(this.id)
      }

    })
    // int left blank
    this.tabledata = {
      columns: [
        { displayName: 'Full name', key: 'fullName' },
        { displayName: 'Email', key: 'email' },
        { displayName: 'Position', key: 'position' },
      ],
      needCheckBox: false,
      needHash: false,
      sortColumn: '',
      sortState: 'asc',
    }

  }
  ngAfterViewInit() {
    // this.elementPosition = this.menuElement.nativeElement.parentElement.offsetTop
    this.elementPosition = 127
  }
  onSideNavTabClick(id: string) {
    this.currentTab = id
    const el = document.getElementById(id)
    if (el != null) {
      el.scrollIntoView({ behavior: 'smooth', block: 'start', inline: 'start' })
    }
  }

  /* API call to get all roles*/
  getAllActiveUsersByDepartmentId(id: string) {
    this.usersSvc.getUsersByDepartment(id).subscribe(res => {

      this.data = res.active_users.map((user: any) => {
        return {
          fullName: `${user.firstName} ${user.lastName}`,
          email: user.emailId,
          position: user.roleInfo.descritpion,
          role: user.roleInfo.roleName,
        }
      })
    })

  }
  /* API call to get all roles*/
  getAllActiveUsers() {

    this.profile.getMyDepartment().subscribe(res => {

      this.data = res.active_users.map((user: any) => {
        return {
          fullName: `${user.firstName} ${user.lastName}`,
          email: user.emailId,
          position: user.roleInfo.descritpion,
          role: user.roleInfo.roleName,
        }
      })
    })
  }
  fetchUsersWithRole() {
    this.usersSvc.getUsers(this.role).subscribe(res => {
      this.data = res.users.map((user: any) => {
        return {
          fullName: `${user.first_name} ${user.last_name}`,
          email: user.email,
          position: user.department_name,
          role: this.role,
        }
      })
    })
  }
  gotoAddAdmin() {
    this.router.navigate([`/app/roles/${this.id}/basicinfo`, { addAdmin: true, currentDept: this.currentDept }])
  }
  ngOnDestroy() {
    if (this.defaultSideNavBarOpenedSubscription) {
      this.defaultSideNavBarOpenedSubscription.unsubscribe()
    }
  }
}
