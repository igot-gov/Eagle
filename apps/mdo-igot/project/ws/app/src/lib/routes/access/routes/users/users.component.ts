import { AfterViewInit, Component, OnInit, OnDestroy } from '@angular/core'
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

  constructor(private usersSvc: UsersService, private activeRoute: ActivatedRoute) { }

  ngOnInit() {
    // int left blank
    this.tabledata = {
      actions: [{ name: 'Details', label: 'Details', icon: 'remove_red_eye', type: 'link' }],
      columns: [
        { displayName: 'Full name', key: 'fname' },
        { displayName: 'Email', key: 'email' },
        { displayName: 'Position', key: 'position' },
        { displayName: 'Role', key: 'role' },
      ],
      needCheckBox: false,
      needHash: false,
      sortColumn: '',
      sortState: 'asc',
    }
    this.fetchUsersWithRole()
    if (this.activeRoute.snapshot.queryParams.role) {
      this.role = this.activeRoute.snapshot.queryParams.role
    }
  }
  ngAfterViewInit() {
    // this.elementPosition = this.menuElement.nativeElement.parentElement.offsetTop
  }

  /* API call to get all roles*/
  fetchUsersWithRole() {
    this.usersSvc.getUsers(this.role).subscribe(data => {
      this.data = data.users
    })
  }

  ngOnDestroy() { }
}
