import { Component, OnInit, Inject } from '@angular/core'
import { MAT_DIALOG_DATA, MatDialogRef, MatSnackBar } from '@angular/material'
import { IManageUser, IUserRoleDetail } from '../../../system-roles-management.model'
import { SystemRolesManagementService } from '../../../system-roles-management.service'

@Component({
  selector: 'ws-admin-confirm-action',
  templateUrl: './confirm-action.component.html',
  styleUrls: ['./confirm-action.component.scss'],
})
export class ConfirmActionComponent implements OnInit {
  removeUserData: IUserRoleDetail[] = []
  removeUserId: string[] = []
  isRemoving = false
  constructor(
    public dialogRef: MatDialogRef<ConfirmActionComponent>,
    private rolesSvc: SystemRolesManagementService,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: { userId: string[], userData: IUserRoleDetail[], role: string },
  ) {
     this.removeUserData = this.data.userData
     this.removeUserId = this.data.userId
   }

  ngOnInit() {
  }

  removeUser() {
    this.isRemoving = true
    const removeBody: IManageUser = {
      users: this.removeUserId,
      operation: 'remove',
      roles: [this.data.role],
    }
    this.rolesSvc.manageUser(removeBody).then(
      () => {
        this.isRemoving = false
        this.dialogRef.close('removed')
      }).catch(
        () => {
          this.isRemoving = false
          this.snackBar.open('Error occured', undefined, { duration: 3000 })
        }
      )
  }
  close(): void {
    this.dialogRef.close()
  }
  alterUser(id: string) {
    this.removeUserData = this.removeUserData.filter(data => data.wid !== id)
    this.removeUserId = this.removeUserId.filter(data => data !== id)
  }

}
