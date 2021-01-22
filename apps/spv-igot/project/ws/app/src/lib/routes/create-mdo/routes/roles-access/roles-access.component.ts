import { AfterViewInit, Component, OnInit, OnDestroy } from '@angular/core'
import { Router, ActivatedRoute } from '@angular/router'
import { ProfileV2Service } from '../../../home/services/home.servive'
// import { RolesAccessService } from '../../services/roles-access.service'
@Component({
  selector: 'ws-app-roles-access',
  templateUrl: './roles-access.component.html',
  styleUrls: ['./roles-access.component.scss'],
})
export class RolesAccessComponent implements OnInit, AfterViewInit, OnDestroy {
  tabledata: any = []
  data: any = []
  roleId!: number

  constructor(private router: Router, private homeResolver: ProfileV2Service, private activatedRoute: ActivatedRoute) {
    this.activatedRoute.params.subscribe(params => {
      this.roleId = params['roleId']
      console.log(this.roleId)
    })
  }

  ngOnInit() {
    this.tabledata = {
      actions: [{ name: 'Details', label: 'Details', icon: 'remove_red_eye', type: 'link' }],
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
  }

  ngAfterViewInit() {
  }

  /* Click event to navigate to a particular role */
  onRoleClick() {
    this.router.navigate([`/app/roles/${this.roleId}/users`])
  }

  /* API call to get all roles*/
  fetchRoles() {
    this.homeResolver.getMyDepartment().subscribe(roles => {

      roles.rolesInfo.forEach((role: { roleName: string }) => {

        if (role.roleName === 'SPV ADMIN') {
          const obj = {
            role: role.roleName,
            count: roles.noOfUsers,
          }
          this.data.push(obj)
        }
      })

    })
  }

  ngOnDestroy() { }
}
