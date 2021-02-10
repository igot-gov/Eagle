import { ProfileV2Service } from './../../services/home.servive'
import { AfterViewInit, Component, OnInit, OnDestroy } from '@angular/core'
import { Router } from '@angular/router'
@Component({
  selector: 'ws-app-roles-access',
  templateUrl: './roles-access.component.html',
  styleUrls: ['./roles-access.component.scss'],
})
export class RolesAccessComponent implements OnInit, AfterViewInit, OnDestroy {
  tabledata: any = []
  data: any = []

  constructor(private router: Router, private profile: ProfileV2Service) { }

  ngOnInit() {
    this.tabledata = {
      // actions: [{ name: 'Details', label: 'Details', icon: 'remove_red_eye', type: 'link' }],
      columns: [
        { displayName: 'Role', key: 'role' },
        { displayName: 'Number of users', key: 'count' },
      ],
      needCheckBox: false,
      needHash: false,
      sortColumn: '',
      sortState: 'asc',
    }
    this.fetchRolesNew()
  }

  ngAfterViewInit() {
    // this.elementPosition = this.menuElement.nativeElement.parentElement.offsetTop
  }

  /* Click event to navigate to a particular role */
  onRoleClick(role: any) {
    this.router.navigate([`/app/roles/${role.role}/users`])
  }

  /* API call to get all roles*/
  fetchRolesNew() {
    const totalUsers: any[] = []
    this.profile.getMyDepartment().subscribe(user => {
      user.rolesInfo.forEach((element: { roleName: any, noOfUsers: any }) => {
        element.roleName = element.roleName.replace(/[/_/]/g, ' ')
        totalUsers.push({
          role: element.roleName,
          count: element.noOfUsers,
        })

      })
      this.data = totalUsers

    })
  }

  ngOnDestroy() { }
}
