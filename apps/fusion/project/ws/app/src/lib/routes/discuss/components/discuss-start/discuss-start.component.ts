import { Component, OnInit, Inject } from '@angular/core'
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog'
import { FormGroup, FormBuilder } from '@angular/forms'
export interface IDialogData {
  animal: string
  name: string
}
@Component({
  selector: 'app-discuss-start',
  templateUrl: './discuss-start.component.html',
  styleUrls: ['./discuss-start.component.scss'],
})
export class DiscussStartComponent implements OnInit {
  startForm!: FormGroup
  constructor(
    public dialogRef: MatDialogRef<DiscussStartComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IDialogData,
    private formBuilder: FormBuilder) {

  }

  ngOnInit(): void {
    this.startForm = this.formBuilder.group({
      category: [],
      question: [],
      description: [],
      tags: [],
    })
  }
  onNoClick(): void {
    this.dialogRef.close()
  }
  showError(meta: string) {
    if (meta) {
      return true
    }
    return false
  }
}
