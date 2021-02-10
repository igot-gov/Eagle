import { Component, OnInit, Inject, ViewChild, ElementRef } from '@angular/core'
import { MAT_DIALOG_DATA, MatDialogRef, MatSnackBar } from '@angular/material'
import { FormGroup, Validators, FormControl } from '@angular/forms'
import { TenantAdminService } from '../../../../services/tenant-admin.service'

@Component({
  selector: 'ws-admin-edit-department-dialog',
  providers: [TenantAdminService],
  templateUrl: './edit-department-dialog.component.html',
  styleUrls: ['./edit-department-dialog.component.scss'],
})
export class EditDepartmentDialogComponent implements OnInit {
  editForm: FormGroup
  uploadSaveData = false
  processing = false
  departments = []
  userDepartment = ''
  @ViewChild('toastSuccess', { static: true }) toastSuccess!: ElementRef<any>
  @ViewChild('toastError', { static: true }) toastError!: ElementRef<any>
  constructor(
    public dialogRef: MatDialogRef<EditDepartmentDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private tenantAdminSvc: TenantAdminService,
    private snackBar: MatSnackBar,
  ) {
    this.userDepartment = this.data.department
    this.editForm = new FormGroup({
      department: new FormControl('', [Validators.required]),
    })
  }

  ngOnInit() {
    this.getUserDepartments()
  }

  public getUserDepartments() {
    this.processing = true
    this.tenantAdminSvc.getUserDepartments().then((res: any) => {
      this.processing = false
      this.departments = res
      this.editForm.patchValue({
        department: this.userDepartment,
      })
    })
      .catch(() => { })
      .finally(() => {
        this.processing = false
      })
  }

  public changeDepartment(form: any) {
    this.processing = true
    const req = {
      ...form.value,
      userId: this.data.userId,
    }
    this.tenantAdminSvc.updateUserDepartment(req).subscribe(
      () => {
        this.processing = false
        this.dialogRef.close({
          department: form.value.department,
          userId: this.data.userId,
        })
        this.openSnackbar(this.toastSuccess.nativeElement.value)
      },
      (err: any) => {
        this.openSnackbar(err.error.split(':')[1])
        this.processing = true
      })
  }

  close() {
    this.dialogRef.close()
  }

  private openSnackbar(primaryMsg: string, duration: number = 5000) {
    this.snackBar.open(primaryMsg, 'X', {
      duration,
    })
  }
}
