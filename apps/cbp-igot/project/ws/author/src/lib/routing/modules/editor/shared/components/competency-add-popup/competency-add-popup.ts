import { Component, OnInit, Inject, ViewChild, ElementRef } from '@angular/core'
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog'
import { ENTER, COMMA } from '@angular/cdk/keycodes'
import { FormGroup, FormBuilder } from '@angular/forms'
import { MatChipInputEvent, MatSnackBar } from '@angular/material'
import { CompetenceService } from '../../services/competence.service'

export interface IDialogData {
  animal: string
  name: string
}
@Component({
  selector: 'ws-auth-competency-add-popup',
  templateUrl: './competency-add-popup.html',
  styleUrls: ['./competency-add-popup.scss'],
})
export class CompetencyAddPopUpComponent implements OnInit {
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
    public dialogRef: MatDialogRef<CompetencyAddPopUpComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IDialogData,
    private formBuilder: FormBuilder,
    private interestService: CompetenceService,
    private snackBar: MatSnackBar,
  ) {
  }

  ngOnInit(): void {
    this.startForm = this.formBuilder.group({
      category: [],
      question: [],
      description: [],
      // tags: [],
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

  public submitPost(form: any) {
    if (form) {

    }
    this.uploadSaveData = true
    this.showErrorMsg = false
    const postCreateReq = {
      type: 'COMPETENCY',
      name: form.value.question,
      description: form.value.description,
      additionalProperties: {
        competencyType: form.value.category,
      },
    }
    // source: form.value.tags,
    this.interestService.createPost(postCreateReq).subscribe(
      () => {
        form.reset()
        this.uploadSaveData = false
        // this.openSnackbar(this.toastSuccess.nativeElement.value)
        this.openSnackbar('Competency Request created succesfully! Please wait for reviewer to approve it')
        this.dialogRef.close('postCreated')
      },
      err => {
        this.openSnackbar(this.toastError.nativeElement.value)
        this.uploadSaveData = false
        if (err) {
          if (err.error && err.error.message) {
            this.showErrorMsg = true
            this.createErrorMsg = err.error.message.split('|')[1] || this.defaultError
          }
        }
      })
  }

  private openSnackbar(primaryMsg: string, duration: number = 5000) {
    this.snackBar.open(primaryMsg, 'X', {
      duration,
    })
  }
}
