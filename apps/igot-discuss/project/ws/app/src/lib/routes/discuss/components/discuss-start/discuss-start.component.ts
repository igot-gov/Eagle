import { Component, OnInit, Inject, ViewChild, ElementRef } from '@angular/core'
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog'
import { ENTER, COMMA } from '@angular/cdk/keycodes'
import { FormGroup, FormBuilder } from '@angular/forms'
import { MatChipInputEvent, MatSnackBar } from '@angular/material'
import { DiscussService } from '../../services/discuss.service'
import { NSDiscussData } from '../../models/discuss.model'
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
  allCategories!: NSDiscussData.ICategorie[]
  allTags!: NSDiscussData.ITag[]
  separatorKeysCodes: number[] = [ENTER, COMMA]
  postTagsArray: string[] = []
  uploadSaveData = false
  showErrorMsg = false
  createErrorMsg = ''
  defaultError = 'Something went wrong, Please try again after sometime!'
  @ViewChild('toastSuccess', { static: true }) toastSuccess!: ElementRef<any>
  @ViewChild('toastError', { static: true }) toastError!: ElementRef<any>

  constructor(
    public dialogRef: MatDialogRef<DiscussStartComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IDialogData,
    private formBuilder: FormBuilder,
    private discussService: DiscussService,
    private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
    this.initializeData()
    this.startForm = this.formBuilder.group({
      category: [],
      question: [],
      description: [],
      tags: [],
    })
  }
  private initializeData() {
    this.discussService.fetchAllCategories().then((data: any) => {
      this.allCategories = data.categories
      if (this.startForm.get('category')) {}
      this.startForm.controls['category'].setValue(this.allCategories[1].cid)
    })

    this.discussService.fetchAllTags().then((data: any) => {
      this.allTags = data.tags
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
    form.value.tags = this.postTagsArray
    this.uploadSaveData = true
    this.showErrorMsg = false
    const postCreateReq = {
      cid: form.value.category,
      title: form.value.question,
      content: form.value.description,
      tags: form.value.tags,
    }
    this.discussService.createPost(postCreateReq).subscribe(
      () => {
        form.reset()
        this.uploadSaveData = false
        this.openSnackbar(this.toastSuccess.nativeElement.value)
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
