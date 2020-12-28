import { Component, EventEmitter, Inject, OnDestroy, OnInit, Output } from '@angular/core'
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms'
// import { MatSnackBar } from '@angular/material'
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material'

@Component({
  selector: 'ws-auth-pick-name',
  templateUrl: './pick-name.component.html',
  styleUrls: ['./pick-name.component.scss'],
})
export class PickNameComponent implements OnInit, OnDestroy {
  @Output() action = new EventEmitter<{ type: string; identifier: string }>()
  @Output() closeEvent = new EventEmitter<boolean>()
  createCourseForm!: FormGroup

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<PickNameComponent>,
    private formBuilder: FormBuilder,
  ) {
    dialogRef.disableClose = true
    this.createForm()
  }
  createForm() {
    this.createCourseForm = this.formBuilder.group({
      name: new FormControl('', [Validators.required, Validators.minLength(10)]),
    })
  }
  createContent() {
    const name = this.createCourseForm.get('name')
    if (name && name.value) {
      this.dialogRef.close({
        name: name.value,
        action: 'YES',
      })
    }
  }

  cancel() {
    this.createCourseForm.reset()
    this.dialogRef.close({
      name: '',
      action: 'NO',
    })
  }

  ngOnInit() {

  }

  ngOnDestroy() {
    // this.loaderService.changeLoad.next(false)
  }
}
