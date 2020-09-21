import { Component, OnInit, Inject } from '@angular/core'
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog'
import { ENTER, COMMA } from '@angular/cdk/keycodes'
import { FormGroup, FormBuilder } from '@angular/forms'
import { MatChipInputEvent } from '@angular/material'
import { DiscussService } from '../../services/discuss.service'
import { NSDiscussData } from '../../models/discuss.model'
import { IChipItems } from '../../../user-profile/models/user-profile.model'
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
  public postTagsArray: IChipItems[] = []
  uploadSaveData = false

  constructor(
    public dialogRef: MatDialogRef<DiscussStartComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IDialogData,
    private formBuilder: FormBuilder,
    private discussService: DiscussService) {
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
    const value = event.value as unknown as IChipItems

    if ((value || '')) {
      this.postTagsArray.push(value)
    }

    if (input) {
      input.value = ''
    }

    // this.knownLanguagesInputRef.nativeElement.value = ''
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
    console.log('Form value : ', form.value)

  }
}
