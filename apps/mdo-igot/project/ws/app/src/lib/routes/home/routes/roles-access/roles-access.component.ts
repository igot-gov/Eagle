import { AfterViewInit, Component, OnInit, OnDestroy } from '@angular/core'
import { Router } from '@angular/router'
import { RolesAccessService } from '../../services/roles-access.service'
@Component({
  selector: 'ws-app-roles-access',
  templateUrl: './roles-access.component.html',
  styleUrls: ['./roles-access.component.scss'],
})
export class RolesAccessComponent implements OnInit, AfterViewInit, OnDestroy {
  tabledata: any = []
  data: any = []

  constructor(private router: Router, private roleSvc: RolesAccessService) { }

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
    this.fetchRoles()
    // this.fetchRolesNew()
  }

  ngAfterViewInit() {
    // this.elementPosition = this.menuElement.nativeElement.parentElement.offsetTop
  }

  /* Click event to navigate to a particular role */
  onRoleClick(role: any) {
    this.router.navigate([`/app/roles/${role.role}/users`])
  }

  /* API call to get all roles*/
  fetchRoles() {
    this.roleSvc.getRoles().subscribe(roles => {
      this.data = roles.data
    })
  }
  /* API call to get all roles*/
  // fetchRolesNew() {
  //   this.profile.getMyDepartment().subscribe(user => {
  //     // this.data = roles.data
  //     user.rolesInfo.forEach((element: { roleName: any, active_users: any }) => {
  //       console.log(element.roleName)
  //       console.log(element.active_users)

  //     })
  //   })
  // }

  ngOnDestroy() { }
}
