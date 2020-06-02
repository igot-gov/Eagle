import { Component, OnInit, Input, OnChanges, OnDestroy } from '@angular/core'
import { FormControl } from '@angular/forms'
import { debounceTime } from 'rxjs/operators'
import { Subscription } from 'rxjs'
import { EditorContentService } from '@ws/author/src/lib/routing/modules/editor/services/editor-content.service'
import { IMetaTextAreaUnit, IMetaUnit, IConditionValue } from '@ws/author/src/lib/routing/modules/editor/interface/meta.ts'
import { NSContent } from '@ws/author/src/lib/interface/content'
import { ConditionCheckService } from '@ws/author/src/lib/modules/shared/services/condition-check.service.ts'
import { EditorContentV2Service } from '@ws/author/src/lib/routing/modules/editor/services/editor-content-v2.service.ts'

import { textAreaSample } from '../constants/edit-meta-childs-constants'
// import { IFormMeta } from '@ws/author/src/lib/interface/form.ts'
// import { AccessControlService } from '@ws/author/src/lib/modules/shared/services/access-control.service.ts'
// import { AuthInitService } from '@ws/author/src/lib/services/init.service.ts'

@Component({
  selector: 'ws-auth-edit-meta-textarea',
  templateUrl: './edit-meta-textarea.component.html',
  styleUrls: ['./edit-meta-textarea.component.scss'],
})
export class EditMetaTextareaComponent implements OnInit, OnChanges, OnDestroy {

  @Input() isSubmitPressed = false
  @Input() metaName!: string
  isEditEnabled = false
  textAreaForm!: FormControl
  contentSubscription?: Subscription
  dataChangeSubscription?: Subscription
  metaChangeSubscription?: Subscription
  currentId = ''
  metaDetails!: IMetaUnit<IMetaTextAreaUnit>
  errorMessage: '' | 'noOfWords' | 'minLength' | 'empty' | 'minRows' | 'maxRows' | 'maxLength' = ''
  isDisabled = false
  isMandatory = false
  textAreaMaxLength: number | null = null
  textAreaMinLength: number | null = null
  noOfWords: number | null = null
  minRows: number | null = null
  maxRows: number | null = null
  tenantDisplayName: string | null = null
  tenantPlaceholder: string | null = null
  autoResize = true

  constructor(
    private contentService: EditorContentService,
    private conditionCheckService: ConditionCheckService,
    private editorContentSvcV2: EditorContentV2Service,
    // private authInitService: AuthInitService,
    // private accessControl: AccessControlService,
  ) { }

  ngOnChanges() {
    if (this.isSubmitPressed && this.textAreaForm) {
      this.textAreaForm.markAsTouched()
      this.textAreaForm.markAsDirty()
      this.errorMessage = this.validateField()
      if (this.errorMessage) {
        this.textAreaForm.setErrors({ incorrect: true })
      }
    }
  }

  ngOnDestroy() {
    if (this.contentSubscription) {
      this.contentSubscription.unsubscribe()
    }
    if (this.dataChangeSubscription) {
      this.dataChangeSubscription.unsubscribe()
    }
    if (this.metaChangeSubscription) {
      this.metaChangeSubscription.unsubscribe()
    }
  }

  ngOnInit() {
    this.metaDetails = textAreaSample

    this.contentSubscription = this.contentService.changeActiveCont.subscribe(id => {
      if (this.textAreaForm) {
        this.storeData()
      }
      this.currentId = id
      this.setUp()
      this.createAndAssignForm()
      this.errorMessage = this.validateField()
    })
    this.dataChangeSubscription = this.editorContentSvcV2.onContentChange.subscribe(() => {
      // console.log('cont ')
      this.setUp()
      if (this.currentId) {
        this.createAndAssignForm()
      }
    })
    this.metaChangeSubscription = this.editorContentSvcV2.onMetaChange.subscribe(() => {
      // console.log('meta ')
      this.setUp()
      if (this.currentId) {
        this.createAndAssignForm()
      }
    })
    this.textAreaForm.valueChanges.pipe(debounceTime(100)).subscribe(() => {
      this.storeData()
      this.errorMessage = this.validateField()
      if (!this.errorMessage) {
        this.textAreaForm.setErrors(null)
      }
    })
  }

  /**
   * Function that gets and sets input field properities for the meta.
   * Function needs to be triggered on content data change or when backend changes value for canCascade properities
   */
  setUp() {
    // get text area parameters
    this.textAreaMaxLength = this.getValue(this.metaDetails.meta.validations.maxLength)
    this.textAreaMinLength = this.getValue(this.metaDetails.meta.validations.minLength)
    this.autoResize = this.getValue(this.metaDetails.meta.autoExtend) || false
    this.minRows = this.getValue(this.metaDetails.meta.minRows)
    this.noOfWords = this.getValue(this.metaDetails.meta.validations.noOfWords)
    this.tenantDisplayName = this.getValue(this.metaDetails.displayName)
    this.tenantPlaceholder = this.getValue(this.metaDetails.placeHolder)
    // get action parameters of meta
    this.isDisabled = this.metaDetails.isDisabled.find(c =>
      this.conditionCheckService.checkConditionV2(this.contentService.getUpdatedMeta(this.currentId), c)
    ) ? true : false
    this.isMandatory = this.metaDetails.isMandatory.find(c =>
      this.conditionCheckService.checkConditionV2(this.contentService.getUpdatedMeta(this.currentId), c)
    ) ? true : false
    this.isEditEnabled = this.contentService.isEditEnabled
  }

  /**
   * Gets updated value of the meta from service if null then assigns initial value to the field
   * @memberof EditMetaTextAreaComponent
   */
  createAndAssignForm() {
    this.textAreaForm = new FormControl()
    let textVal = this.contentService.getUpdatedMeta(this.currentId)[this.metaDetails.name as keyof NSContent.IContentMeta]
    if (!textVal && this.metaDetails.defaultValue.length) {
      textVal = this.getValue(this.metaDetails.defaultValue) || ''
    }
    this.textAreaForm.setValue(textVal)
  }

  /**
   * Validates whether the data entered by user meets the required conditions
   * @returns {string} returns empty string if all conditions are statisfied else return error message
   * @memberof EditMetaTextAreaComponent
   */
  validateField() {
    if (this.isSubmitPressed && this.isMandatory) {
      const noOfRows = this.textAreaForm.value.match(/\n/g) ? this.textAreaForm.value.match(/\n/g).length + 1 : 1
      if (!this.textAreaForm.value) {
        return 'empty'
      }  if (this.textAreaMaxLength && this.textAreaForm.value.length > this.textAreaMaxLength) {
        return 'maxLength'
      }  if (this.textAreaMinLength && this.textAreaForm.value.length < this.textAreaMinLength) {
        return 'minLength'
      }  if (this.noOfWords && this.textAreaForm.value.match(/\S+/g).length < this.noOfWords) {
        return 'noOfWords'
      }  if (this.minRows && noOfRows < this.minRows) {
        return 'minRows'
      }  if (this.maxRows && noOfRows > this.maxRows) {
        return 'maxRows'
      }
        return ''
    }
    return ''
  }

  /**
   * Function used to fetch default input properities for a meta by evaluating the given conditions.
   * Returns value of the condition which passed the evaluation; if no condition passes return null
   * @param data condition and data object array for which the evaluation needs to be done
   * @returns {T | null} returns value of the condition that passes evaluation and if none passed return null
   * @memberof EditMetaTextAreaComponent
   */
  getValue<T>(data: IConditionValue<T>[]): T | null {
    if (data && data.length) {
      const filteredVal = data.find(e =>
        this.conditionCheckService.checkConditionV2(
          this.contentService.getUpdatedMeta(this.currentId),
          e.conditions
        )
      )
      return filteredVal ? filteredVal.value : null
    }
    return null
  }

  /**
   * This function updates the changes to EditorContentService only if the current change is differnt from original data
   * @memberof EditMetaTextAreaComponent
   */
  storeData() {
    const originalMeta = this.contentService.getOriginalMeta(this.currentId)
    if (originalMeta && this.isEditEnabled) {
      const meta = <any>{}
      if (
        this.textAreaForm.value !==
        originalMeta[this.metaDetails.name as keyof NSContent.IContentMeta]
      ) {
        meta[this.metaDetails.name as keyof NSContent.IContentMeta] = JSON.parse(JSON.stringify(this.textAreaForm.value))
      }
      this.contentService.setUpdatedMeta(meta, this.currentId)
    }
  }

}
