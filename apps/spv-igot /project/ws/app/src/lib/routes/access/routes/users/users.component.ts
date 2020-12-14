import { AfterViewInit, Component, OnInit, OnDestroy } from '@angular/core'
import { Router } from '@angular/router'
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
      sortColumn: '',
      sortState: 'asc',
    }
    // this.data = [
    //   {
    //     "fullName": "Rangabashyam",
    //     "last_name": "P K",
    //     "email": "rangabashyam.krishnamachari@tarento.com",
    //     "source_id": null,
    //     "department_name": "Secretary",
    //     "wid": "28f87093-9eed-4c03-a2f7-517282d4174a",
    //     "role": "publisher"
    //   },
    //   {
    //     "fullName": "Pritha",
    //     "last_name": "Chattopadhyay",
    //     "email": "pritha.chattopadhyay@tarento.com",
    //     "source_id": null,
    //     "department_name": "Admin",
    //     "wid": "f3834e1b-315a-4de2-88a7-895ce0b7f46d",
    //     "role": "publisher"
    //   },

    //   {
    //     "fullName": "Thillai",
    //     "last_name": "Rajan",
    //     "email": "thillai.rajan@tarento.com",
    //     "source_id": null,
    //     "department_name": "Super Admin",
    //     "wid": "70da02ec-9274-4d35-b372-93140b38e292",
    //     "role": "publisher"
    //   },
    // ]

  }
  ngAfterViewInit() {
    // this.elementPosition = this.menuElement.nativeElement.parentElement.offsetTop
  }

  /* API call to get all roles*/
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
