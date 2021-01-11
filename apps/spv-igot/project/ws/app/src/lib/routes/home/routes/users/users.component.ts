import { Component, OnInit, Output, EventEmitter } from '@angular/core'
import { MatDialog, MatSnackBar } from '@angular/material'
import { ActivatedRoute } from '@angular/router'
import { NsAutoComplete } from '@ws-widget/collection'
import { SystemRolesManagementService } from '../../services/system-roles-management.service'
import { IUserRoleDetail } from './users.model'
import { OpenRolesDialogComponent } from './components/open-roles-dialog/open-roles-dialog.component'
import { EditDepartmentDialogComponent } from './components/edit-department-dialog/edit-department-dialog.component'
import { TenantAdminService } from '../../services/tenant-admin.service'

@Component({
  selector: 'ws-admin-users',
  providers: [TenantAdminService],
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss'],

})
export class UsersComponent implements OnInit {
  @Output() selectedUser: EventEmitter<any> = new EventEmitter<any>()
  roleList: string[] = []
  rolesHash: { [key: string]: IUserRoleDetail } = {}
  isAdding = false
  userDetails: { [key: string]: any } = {}
  userIds: string[] = []
  departments: string[] = []
  constructor(
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private activatedRoute: ActivatedRoute,
    public rolesSvc: SystemRolesManagementService,
    private tenantAdminSvc: TenantAdminService,
  ) {
    if (this.activatedRoute.parent && this.activatedRoute.parent.parent) {
      this.activatedRoute.parent.parent.data.subscribe(data => {
        const featureData = data.featureData.data
        Object.keys(featureData.roleList).map(role => {
          this.rolesHash[role] = {
            about: featureData.roleList[role],
            hasRole: false,
            isSelected: false,
          }
        })
        this.roleList = Object.keys(this.rolesHash || {})
      })
    }
  }

  ngOnInit() {
    this.getUserDepartments()
  }
  addUser(user: NsAutoComplete.IUserAutoComplete): void {
    this.isAdding = true
    this.rolesSvc.checkUser(user.wid).subscribe(
      data => {
        this.userDetails[user.wid] = {
          ...user,
          roles: data.user_roles.filter(roles => this.roleList.includes(roles)),
        }
        this.userIds = Object.keys(this.userDetails)
        this.isAdding = false
      },
      _err => {
        this.isAdding = false
      })
    this.seletedUserFun(user)
  }
  removeUser(user: NsAutoComplete.IUserAutoComplete) {
    if (this.userDetails[user.wid]) {
      delete this.userDetails[user.wid]
      this.userIds = this.userIds.filter(data => data !== user.wid)
    }
  }

  openDialog(wid: string) {
    if (this.userDetails[wid] && this.userDetails[wid].roles.length) {
      this.userDetails[wid].roles.forEach((data: string) => {
        this.rolesHash[data].isSelected = true
        this.rolesHash[data].hasRole = true
      })
    }
    const dialogRef = this.dialog.open(OpenRolesDialogComponent, {
      width: '80vw',
      data: {
        userId: wid,
        rolesData: this.rolesHash,
      },
    })
    dialogRef.afterClosed().subscribe(data => {
      if (data && data.wid && data.newRoles) {
        this.userDetails[data.wid].roles = data.newRoles
        if (data.addingError && data.removingError) {
          this.snackBar.open('Error Occured', 'X', { duration: 2000 })
        } else if (data.addingError) {
          this.snackBar.open('Error Occured adding roles', 'X', { duration: 2000 })
        } else if (data.removingError) {
          this.snackBar.open('Error Occured removing roles', 'X', { duration: 2000 })
        } else {
          this.snackBar.open('Changes Saved', 'X', { duration: 2000 })
        }
      }
      this.roleList.forEach(role => {
        this.rolesHash[role].isSelected = false
        this.rolesHash[role].hasRole = false
      })
    })
  }

  openDepartmentDialog(wid: string, department: string) {
    const dialogRef = this.dialog.open(EditDepartmentDialogComponent, {
      width: '80vw',
      data: {
        department,
        userId: wid,
      },
    })
    dialogRef.afterClosed().subscribe(data => {
      if (data && data.userId && data.department) {
        this.userDetails[data.userId].department_name = data.department
      }
    })
  }

  getUserDepartments() {
    this.tenantAdminSvc.getUserDepartments().then((res: any) => {
      if (res && res.length) {
        res.map((r: any) => {
          return this.departments.push(r.department_name)
        })
      }
    })
      .catch(() => {
      })
      .finally(() => {
      })
  }
  public seletedUserFun(data: any): void {
    this.selectedUser.emit(data)
  }

}
