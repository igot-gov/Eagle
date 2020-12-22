import { AfterViewInit, Component, OnInit, OnDestroy } from '@angular/core'
import { Router } from '@angular/router'
import { ActivatedRoute } from '@angular/router'
import { UsersService } from '../../services/users.service'

@Component({
  selector: 'ws-app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss'],
})

export class UsersComponent implements OnInit, AfterViewInit, OnDestroy {
  tabledata: any = []
  data: any = []
  role: any
  id!: string 
  private defaultSideNavBarOpenedSubscription: any

  constructor(private usersSvc: UsersService, private router: Router,  private route: ActivatedRoute) { }
  ngOnInit() {
    const url = this.router.url.split('/')
    this.role = url[url.length - 2]
    this.fetchUsersWithRole()
    this.route.params.subscribe(params => {
      this.id = params['id'];
      this.getAllActiveUsersByDepartmentId(this.id)
      });
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
      sortColumn: '',
      sortState: 'asc',
    }

  }
  ngAfterViewInit() {
    // this.elementPosition = this.menuElement.nativeElement.parentElement.offsetTop
  }

  /* API call to get all roles*/
  getAllActiveUsersByDepartmentId(id: string){
    this.usersSvc.getUsersByDepartment(id).subscribe(res => {
   
      this.data = res.active_users.map((user: any) => {
        console.log(user)
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

  ngOnDestroy() {
    if (this.defaultSideNavBarOpenedSubscription) {
      this.defaultSideNavBarOpenedSubscription.unsubscribe()
    }
  }
}
