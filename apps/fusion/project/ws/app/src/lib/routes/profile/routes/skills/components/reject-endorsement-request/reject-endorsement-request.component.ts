import { Component, OnInit, Inject, ElementRef, ViewChild } from '@angular/core'
import { SkillsService } from '../../services/skills.service'
import { FormGroup, FormBuilder, Validators } from '@angular/forms'
import { MAT_DIALOG_DATA, MatDialogRef, MatSnackBar } from '@angular/material'
@Component({
  selector: 'ws-app-reject-endorsement-request',
  templateUrl: './reject-endorsement-request.component.html',
  styleUrls: ['./reject-endorsement-request.component.scss'],
})
export class RejectEndorsementRequestComponent implements OnInit {
  endorseForm: FormGroup | null = null
  @ViewChild('provideContent', { static: true })
  provideContentMessage!: ElementRef<any>
  constructor(
    public dialogRef: MatDialogRef<RejectEndorsementRequestComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private skillSrv: SkillsService,
    private form: FormBuilder,
    private snackBar: MatSnackBar,
  ) {
    this.endorseForm = this.form.group({
      comments: ['', Validators.required],
    })
  }

  ngOnInit() { }
  rejectEndorsement(endorseId: string) {
    if (this.endorseForm && this.endorseForm.controls.comments.value !== '') {
      const obj = {
        ...this.data,
        endorseId,
        status: 'shortlisted',
        endorsed_status: 'rejected',
        approver_message: this.endorseForm.controls.comments.value,
      }
      this.skillSrv.endorsementRequest(obj).subscribe(() => {
        this.dialogRef.close()
      })
    } else if (this.endorseForm && this.endorseForm.controls.comments.value === '') {
      this.snackBar.open(this.provideContentMessage.nativeElement.value)
    }
  }
  closeDialog() {
    this.dialogRef.close()
  }
}
