import { Component, OnInit } from '@angular/core'
import { MatTableDataSource, MatSnackBar, MatDialog } from '@angular/material'
import { EChangeReqStatusTypes, IUserDepartmentChange } from './user-department-management.model'
import { TenantAdminService } from '../../tenant-admin.service'
import { ActionDialogComponent } from './components/action-dialog/action-dialog.component'

@Component({
  selector: 'ws-admin-user-department-management',
  templateUrl: './user-department-management.component.html',
  styleUrls: ['./user-department-management.component.scss'],
})
export class UserDepartmentManagementComponent implements OnInit {
  pendingTable: IUserDepartmentChange[] = []
  otherTable: IUserDepartmentChange[] = []
  displayedPendingColumns: string[] = ['firstname', 'lastname', 'email', 'currentdept', 'newDept', 'status', 'action']
  displayedOtherColumns: string[] = ['firstname', 'lastname', 'email', 'currentdept', 'newDept', 'status',
  'approverFname', 'approverLname',  'approverEmail']
  pendingSource = new MatTableDataSource(this.pendingTable)
  otherSource = new MatTableDataSource(this.otherTable)
  changeReqStatusTypes = EChangeReqStatusTypes
  isFetching = false
  departments: string[] = []
  currentTabIndex = 0

  constructor(
    private tenantAdminSvc: TenantAdminService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
  ) { }

  ngOnInit() {
    this.getUserDepartments()
  }

  tabChange(event: any) {
    if (event.index === 0) {
      this.getPendingRequests()
    } else {
      this.getOtherRequests()
    }
  }

  async getUserDepartments(): Promise<any> {
      this.tenantAdminSvc.getUserDepartments().then((res: any) => {
        if (res && res.length) {
          res.map((r: any) => {
            this.departments.push(r.department_name)
          })
        }
        this.getPendingRequests()
      })
      .catch(() => {
      })
      .finally(() => {
      })
  }

  getPendingRequests() {
    const statusTypes = [EChangeReqStatusTypes.pending]
    this.getDeptChangeRequests(statusTypes)
  }

  getOtherRequests() {
    const statusTypes = [
      EChangeReqStatusTypes.approved,
      EChangeReqStatusTypes.rejected,
    ]
    this.getDeptChangeRequests(statusTypes)
  }

  getDeptChangeRequests(statusTypes: any) {
    const req = {
      statusTypes,
      approverDept: this.departments,
    }
    this.tenantAdminSvc.getPendingRequests(req).subscribe(
      (data: any) => {
        if (this.currentTabIndex === 0) {
          this.pendingTable = data
          this.pendingSource = new MatTableDataSource(this.pendingTable)
        } else {
          this.otherTable = data
          this.otherSource = new MatTableDataSource(this.otherTable)
        }
      },
      (err: any) => {
        // console.log('err :', err)
        this.openSnackbar(err.error.split(':')[1])
      })
  }

  private openSnackbar(primaryMsg: string, duration: number = 5000) {
    this.snackBar.open(primaryMsg, 'X', {
      duration,
    })
  }

  takeAction(data: any) {
    this.openActionDialog(data)
  }

  openActionDialog(request: any) {
    const dialogRef = this.dialog.open(ActionDialogComponent, {
      width: '80vw',
      data: {
        request,
      },
    })
    dialogRef.afterClosed().subscribe((data: any) => {
      this.getPendingRequests()
      console.log('data: ', data)
    })
  }
}
