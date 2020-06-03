import { Component, OnInit, Inject } from '@angular/core'
import { Router } from '@angular/router'
import { MatDialogRef, MAT_DIALOG_DATA, MatSnackBar } from '@angular/material'
import { SkillsService } from '../../services/skills.service'
@Component({
  selector: 'ws-app-delete-role',
  templateUrl: './delete-role.component.html',
  styleUrls: ['./delete-role.component.scss'],
})
export class DeleteRoleComponent implements OnInit {
  roleId = ''
  constructor(
    public dialogRef: MatDialogRef<DeleteRoleComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private skillSvc: SkillsService,
    private snackBar: MatSnackBar,
    private router: Router,

  ) { }

  ngOnInit() {
  }
  deleteRole() {
    this.skillSvc.deleteRole(this.data).subscribe(() => {
      this.snackBar.open('Deleted Successfully', 'Close', { duration: 1500 })
      this.dialogRef.close('deleted')
      this.router.navigate(['/app/profile/skills/roles'])
    })
  }
  onNoClick(): void {
    this.dialogRef.close()
  }
  closeDialog() {
    this.dialogRef.close()
  }
}
