import { Component, OnInit, Inject } from '@angular/core'
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material'
import { SkillsService } from '../../services/skills.service'
import { FormGroup, FormBuilder, Validators } from '@angular/forms'

@Component({
  selector: 'ws-app-approve-endorsement-request',
  templateUrl: './approve-endorsement-request.component.html',
  styleUrls: ['./approve-endorsement-request.component.scss'],
})
export class ApproveEndorsementRequestComponent implements OnInit {
  approverDescription = ''
  type = 'manager'
  selected = ''
  endorsementList = []
  skillLevelList = ['Starter', 'Basic', 'Advanced', 'Expert']
  endorseForm: FormGroup | null = null
  constructor(
    public dialogRef: MatDialogRef<ApproveEndorsementRequestComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private form: FormBuilder,
    private skillSrv: SkillsService) {
    this.endorseForm = this.form.group({
      skillName: '',
      skillLevel: ['', Validators.required],
      comments: ['', Validators.required],
    })
  }

  ngOnInit() {
    if (this.endorseForm) {
      this.endorseForm.controls.skillName.setValue(this.data.skill_name)
    }
    this.selected = this.data.skill_level
  }
  approveEndorsement() {
    if (this.endorseForm && this.endorseForm.controls.comments.value !== '') {
      const obj = {
        ...this.data,
        endorseId: this.data.endorse_id,
        status: 'approved',
        approved_skill_level: this.endorseForm.controls.skillLevel.value,
        approver_message: this.endorseForm.controls.comments.value,
      }
      this.skillSrv.endorsementRequest(obj).subscribe(() => {
        this.dialogRef.close('approved')
      })
    }
  }
  closeDialog() {
    this.dialogRef.close()
  }
}
