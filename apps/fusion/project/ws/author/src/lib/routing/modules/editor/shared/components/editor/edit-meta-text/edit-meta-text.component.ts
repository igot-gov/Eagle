import { Component, OnInit, Input, OnChanges, OnDestroy } from '@angular/core'
import { FormControl } from '@angular/forms'
import { debounceTime } from 'rxjs/operators'
import { Subscription } from 'rxjs'
import { EditorContentService } from '@ws/author/src/lib/routing/modules/editor/services/editor-content.service'
import { IMetaTextUnit, IMetaUnit, IConditionValue } from '@ws/author/src/lib/routing/modules/editor/interface/meta.ts'
import { NSContent } from '@ws/author/src/lib/interface/content'
import { ConditionCheckService } from '@ws/author/src/lib/modules/shared/services/condition-check.service.ts'
import { EditorContentV2Service } from '@ws/author/src/lib/routing/modules/editor/services/editor-content-v2.service.ts'
import { sampleData1 } from '../constants/edit-meta-childs-constants'
// import { IConditionsV2 } from '../../../../../../../interface/conditions-v2';
// import { IFormMeta } from '@ws/author/src/lib/interface/form.ts'
// import { AccessControlService } from '@ws/author/src/lib/modules/shared/services/access-control.service.ts'
// import { AuthInitService } from '@ws/author/src/lib/services/init.service.ts'

@Component({
  selector: 'ws-auth-edit-meta-text',
  templateUrl: './edit-meta-text.component.html',
  styleUrls: ['./edit-meta-text.component.scss'],
})
export class EditMetaTextComponent implements OnInit, OnChanges, OnDestroy {

  @Input() isSubmitPressed = false
  @Input() metaName!: string
  isEditEnabled = false
  textForm!: FormControl
  contentSubscription?: Subscription
  dataChangeSubscription?: Subscription
  metaChangeSubscription?: Subscription
  currentId = ''
  metaDetails!: IMetaUnit<IMetaTextUnit>
  textMaxLength: number | null = null
  textMinLength: number | null = null
  noOfWords: number | null = null
  tenantDisplayName: string | null = null
  tenantPlaceholder: string | null = null
  isDisabled = false
  isMandatory = false
  errorMessage: '' | 'noOfWords' | 'minLength' | 'empty' | 'maxLength' = ''

  constructor(
    private contentService: EditorContentService,
    private conditionCheckService: ConditionCheckService,
    private editorContentSvcV2: EditorContentV2Service,
    // private authInitService: AuthInitService,
    // private accessControl: AccessControlService,
  ) { }

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

  ngOnChanges() {
    if (this.isSubmitPressed && this.textForm) {
      this.textForm.markAsTouched()
      this.textForm.markAsDirty()
      this.errorMessage = this.validateField()
      if (this.errorMessage) {
        this.textForm.setErrors({ incorrect: true })
      }
    }
  }

  ngOnInit() {
    this.metaDetails = sampleData1
    this.contentSubscription = this.contentService.changeActiveCont.subscribe(id => {
      if (this.textForm) {
        this.storeData()
      }
      this.currentId = id
      this.setUp()
      this.createAndAssignForm()
      this.errorMessage = this.validateField()
    })
    this.dataChangeSubscription = this.editorContentSvcV2.onContentChange.subscribe(() => {
      // console.log('cont ', v)
      this.setUp()
      if (this.currentId) {
        this.createAndAssignForm()
      }
    })
    this.metaChangeSubscription = this.editorContentSvcV2.onMetaChange.subscribe(() => {
      // console.log('meta ', v)
      this.setUp()
      if (this.currentId) {
        this.createAndAssignForm()
      }
    })
    this.textForm.valueChanges.pipe(debounceTime(100)).subscribe(() => {
      this.storeData()
      this.errorMessage = this.validateField()
      if (!this.errorMessage) {
        this.textForm.setErrors(null)
      }
    })
  }

  /**
   * on content data change or when backend changes value for canCascade properities
   */
  setUp() {
    this.isEditEnabled = this.contentService.isEditEnabled
    this.isDisabled = this.metaDetails.isDisabled.find(c =>
      this.conditionCheckService.checkConditionV2(this.contentService.getUpdatedMeta(this.currentId), c)
    ) ? true : false
    this.isMandatory = this.metaDetails.isMandatory.find(c =>
      this.conditionCheckService.checkConditionV2(this.contentService.getUpdatedMeta(this.currentId), c)
    ) ? true : false
    this.textMaxLength = this.getValue(this.metaDetails.meta.validations.maxLength)
    this.textMinLength = this.getValue(this.metaDetails.meta.validations.minLength)
    this.tenantDisplayName = this.getValue(this.metaDetails.displayName)
    this.tenantPlaceholder = this.getValue(this.metaDetails.placeHolder)
    this.noOfWords = this.getValue(this.metaDetails.meta.validations.noOfWords)
  }

  /**
   * Gets updated value of the meta from service if null then assigns initial value to the field
   * @memberof EditMetaTextComponent
   */
  createAndAssignForm() {
    this.textForm = new FormControl()
    let textVal = this.contentService.getUpdatedMeta(this.currentId)[this.metaDetails.name as keyof NSContent.IContentMeta]
    if (!textVal && this.metaDetails.defaultValue && this.metaDetails.defaultValue.length) {
      textVal = this.getValue(this.metaDetails.defaultValue) || ''
    }
    this.textForm.setValue(textVal)
  }

  /**
   * Validates whether the data entered by user meets the required conditions
   * @returns {string} returns empty string if all conditions are statisfied else return error message
   * @memberof EditMetaTextComponent
   */
  validateField() {
    if (this.isSubmitPressed && this.isMandatory) {
      if (!this.textForm.value) {
        return 'empty'
      }  if (this.textMaxLength && this.textForm.value.length > this.textMaxLength) {
        return 'maxLength'
      }  if (this.textMinLength && this.textForm.value.length < this.textMinLength) {
        return 'minLength'
      }  if (this.noOfWords && this.textForm.value.match(/\S+/g).length < this.noOfWords) {
        return 'noOfWords'
      }
      return ''
    }
    return ''
  }

  /**
   * Function used to fetch default input properities for a meta by evaluating the given conditions.
   * @param data condition and data object array for which the evaluation needs to be done
   * @returns {T | null} returns value of the condition that passes evaluation and if none passed return null
   * @memberof EditMetaTextComponent
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
   * @memberof EditMetaTextComponent
   */
  storeData() {
    const originalMeta = this.contentService.getOriginalMeta(this.currentId)
    if (originalMeta && this.isEditEnabled) {
      const meta = <any>{}
      if (
        this.textForm.value !==
        originalMeta[this.metaDetails.name as keyof NSContent.IContentMeta]
      ) {
        meta[this.metaDetails.name as keyof NSContent.IContentMeta] = this.textForm.value
      }
      this.contentService.setUpdatedMeta(meta, this.currentId)
    }
  }

}
