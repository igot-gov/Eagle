import { Component, OnInit, Inject, ViewChild, ElementRef } from '@angular/core'
import { FormGroup, Validators, FormControl } from '@angular/forms'
import { MAT_DIALOG_DATA, MatDialogRef, MatSnackBar } from '@angular/material'
import { TenantAdminService } from '../../../../tenant-admin.service'
import { EChangeReqActionTypes } from '../../user-department-management.model'

@Component({
  selector: 'ws-admin-action-dialog',
  templateUrl: './action-dialog.component.html',
  styleUrls: ['./action-dialog.component.scss'],
})
export class ActionDialogComponent implements OnInit {
  editForm: FormGroup
  processing = false
  reqdata!: any
  changeReqStatusTypes = EChangeReqActionTypes
  @ViewChild('toastSuccess', { static: true }) toastSuccess!: ElementRef<any>
  @ViewChild('toastError', { static: true }) toastError!: ElementRef<any>

  constructor(
    public dialogRef: MatDialogRef<ActionDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private tenantAdminSvc: TenantAdminService,
    private snackBar: MatSnackBar,
  ) {
    this.reqdata  = this.data.request
    this.editForm = new FormGroup({
      action: new FormControl('', [Validators.required]),
    })
  }

  ngOnInit() {
  }

  public takeAction(form: any) {
    this.processing = true
    const req = {
      ...form.value,
      reqId: this.reqdata.id,
    }
    this.tenantAdminSvc.actionOnDeptChangeReq(req).subscribe(
      () => {
        this.processing = false
        this.dialogRef.close({
          action: form.value.action,
          reqId: this.data.id,
        })
        this.openSnackbar(this.toastSuccess.nativeElement.value)
      },
      err => {
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
