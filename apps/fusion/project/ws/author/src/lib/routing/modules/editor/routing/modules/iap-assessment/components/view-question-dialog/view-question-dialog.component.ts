import { Component, Inject, OnInit } from '@angular/core'
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog'
import * as iapInterface from '../../interface/iap-assessment.interface'
import { IapAssessmentService } from '../../services/iap-assessment.service'

@Component({
  selector: 'ws-auth-view-question-dialog',
  templateUrl: './view-question-dialog.component.html',
  styleUrls: ['./view-question-dialog.component.scss'],
})
export class ViewQuestionDialogComponent implements OnInit {
  constructor(
    public iapService: IapAssessmentService,
    public dialogRef: MatDialogRef<ViewQuestionDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: iapInterface.IQuestionDetailsContent,
  ) { }

  ngOnInit() {
    if (this.data.questionType === 'hotspot'
      || this.data.questionType === 'dnd'
      || this.data.questionType === 'rdnd') {
      this.dialogRef.updateSize('60%')
    }
    const blank = /&amp;blank/
    this.data.problemStatement = this.data.problemStatement.replace(blank, '_________')
    const element = <any>document.getElementById('problemStatement')
    element.innerHTML = this.data.problemStatement
  }
}
