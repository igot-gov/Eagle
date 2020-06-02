import { Component, OnInit, Inject } from '@angular/core'
import { MAT_DIALOG_DATA, MatDialogRef, MatSnackBar } from '@angular/material'
import { NsAutoComplete } from '@ws-widget/collection'
import { SystemRolesManagementService } from '../../../system-roles-management.service'
import { IUserRoleDetail, IManageUser } from '../../../system-roles-management.model'

@Component({
  selector: 'ws-admin-add-users-dialog',
  templateUrl: './add-users-dialog.component.html',
  styleUrls: ['./add-users-dialog.component.scss'],
})
export class AddUsersDialogComponent implements OnInit {

  isAdding = false
  addList: IUserRoleDetail[] = []
  addUserId: string[] = []
  rolesUserId: string[] = []
  constructor(
    private snackBar: MatSnackBar,
    private rolesSvc: SystemRolesManagementService,
    public dialogRef: MatDialogRef<AddUsersDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { role: string, userIds: string[] },
  ) {
    this.rolesUserId = this.data.userIds
   }

  ngOnInit() {
  }

  addUser(user: NsAutoComplete.IUserAutoComplete): void {
    this.addList.push(user)
  }

  addUserRole() {
    this.isAdding = true
    this.addList.forEach(data => {
      this.addUserId.push(data.wid)
    })
    const addBody: IManageUser = {
      users: this.addUserId,
      operation: 'add',
      roles: this.data.role ? [this.data.role] : [],
    }
    if (this.data.role && this.addUserId.length) {
      this.rolesSvc.manageUser(addBody).then(
        () => {
          this.rolesUserId = this.rolesUserId.concat(this.addUserId)
          this.addUserId = []
          this.addList = []
          this.isAdding = false
          this.snackBar.open(`Users added with ${this.data.role} role`, undefined, { duration: 3000 })
        }).catch(
        () => {
          this.isAdding = false
          this.snackBar.open('Error occurred', undefined, { duration: 3000 })
        },
      )
    }

  }
  removeUser(user: NsAutoComplete.IUserAutoComplete) {
    this.addList = this.addList.filter((data: IUserRoleDetail) => data.wid !== user.wid)
  }

  close() {
    this.dialogRef.close()
  }

}
