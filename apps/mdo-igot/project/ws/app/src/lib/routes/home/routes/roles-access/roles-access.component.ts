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

  constructor(private router: Router) { }

  ngOnDestroy() {
    // if (this.tabs) {
    //   this.tabs.unsubscribe()
    // }
  }
  ngOnInit() {
    // int left blank
    this.tabledata = {
      actions: [{ name: 'Details', label: 'Details', icon: 'remove_red_eye', type: 'link' }],
      columns: [
        { displayName: 'Role', key: 'role' },
        { displayName: 'Number of users', key: 'noOfUsers' },
      ],
      needCheckBox: false,
      needHash: false,
      sortColumn: '',
      sortState: 'asc',
    }
    this.data = [
      {
        role: 'MDO Admin',
        noOfUsers: '5',
      },
      {
        role: 'Moderator',
        noOfUsers: '4',
      },
      {
        role: 'IFU Member',
        noOfUsers: '5',
      },
      {
        role: 'Member',
        noOfUsers: '10',
      },
    ]
  }
  ngAfterViewInit() {
    // this.elementPosition = this.menuElement.nativeElement.parentElement.offsetTop
  }

  /* Click event to navigate to a particular role */
  onRoleClick(role: any) {
    this.router.navigate(['/app/roles-access/privileges'], {
      queryParams: {
        role: role.role,
      },
    })
  }
}
