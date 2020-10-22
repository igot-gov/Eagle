import { Component, OnInit, Inject, ViewChild, ElementRef } from '@angular/core'
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog'
import { ENTER, COMMA } from '@angular/cdk/keycodes'
import { FormGroup, FormBuilder } from '@angular/forms'
import { MatChipInputEvent } from '@angular/material'
export interface IDialogData {
  animal: string
  name: string
}
@Component({
  selector: 'ws-auth-profanity-popup',
  templateUrl: './profanity-popup.html',
  styleUrls: ['./profanity-popup.scss'],
})
export class ProfanityPopUpComponent implements OnInit {
  startForm!: FormGroup
  separatorKeysCodes: number[] = [ENTER, COMMA]
  postTagsArray: string[] = []
  uploadSaveData = false
  showErrorMsg = false
  createErrorMsg = ''
  defaultError = 'Something went wrong, Please try again after sometime!'
  @ViewChild('toastSuccess', { static: true }) toastSuccess!: ElementRef<any>
  @ViewChild('toastError', { static: true }) toastError!: ElementRef<any>

  constructor(
    public dialogRef: MatDialogRef<ProfanityPopUpComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IDialogData,
    private formBuilder: FormBuilder,
  ) {
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
  submitPost() {

  }
  addPersonalInterests(event: MatChipInputEvent): void {
    const input = event.input
    const value = event.value
    if ((value && (value.length >= 3) && (value.length) <= 24)) {
      this.postTagsArray.push(value)
    } else {
      return
    }

    if (input) {
      input.value = ''
    }

    if (this.startForm.get('tags')) {
      // tslint:disable-next-line: no-non-null-assertion
      this.startForm.get('tags')!.setValue(null)
    }
  }

  removePersonalInterests(interest: any) {
    const index = this.postTagsArray.indexOf(interest)

    if (index >= 0) {
      this.postTagsArray.splice(index, 1)
    }
  }
}
