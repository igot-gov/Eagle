import { Component, OnInit, Input, OnChanges, OnDestroy } from '@angular/core'
import { FormControl } from '@angular/forms'
import { debounceTime } from 'rxjs/operators'
import { Subscription } from 'rxjs'
import { EditorContentService } from '@ws/author/src/lib/routing/modules/editor/services/editor-content.service'
import { IMetaNumberUnit, IMetaUnit, IConditionValue } from '@ws/author/src/lib/routing/modules/editor/interface/meta.ts'
import { NSContent } from '@ws/author/src/lib/interface/content'
import { ConditionCheckService } from '@ws/author/src/lib/modules/shared/services/condition-check.service.ts'
import { EditorContentV2Service } from '@ws/author/src/lib/routing/modules/editor/services/editor-content-v2.service.ts'
import { sampleNumberData } from '../constants/edit-meta-childs-constants'
// import { IConditionsV2 } from '../../../../../../../interface/conditions-v2';
// import { IFormMeta } from '@ws/author/src/lib/interface/form.ts'
// import { AccessControlService } from '@ws/author/src/lib/modules/shared/services/access-control.service.ts'
// import { AuthInitService } from '@ws/author/src/lib/services/init.service.ts'

@Component({
  selector: 'ws-auth-edit-meta-input-number',
  templateUrl: './edit-meta-input-number.component.html',
  styleUrls: ['./edit-meta-input-number.component.scss'],
})
export class EditMetaInputNumberComponent implements OnInit, OnChanges, OnDestroy {

  @Input() isSubmitPressed = false
  @Input() metaName!: string
  isEditEnabled = false
  metaContentCtrl!: FormControl
  contentSubscription?: Subscription
  dataChangeSubscription?: Subscription
  metaChangeSubscription?: Subscription
  currentId = ''
  metaDetails!: IMetaUnit<IMetaNumberUnit>
  textMaxValue: number | null = null
  textMinValue: number | null = null
  tenantDisplayName: string | null = null
  tenantPlaceholder: string | null = null
  isDisabled = false
  isMandatory = false
  errorMessage: '' | 'minValue' | 'empty' | 'maxValue' = ''

  constructor(
    private contentService: EditorContentService,
    private conditionCheckService: ConditionCheckService,
    private editorContentSvcV2: EditorContentV2Service,
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
    if (this.isSubmitPressed && this.metaContentCtrl) {
      this.metaContentCtrl.markAsTouched()
      this.metaContentCtrl.markAsDirty()
      this.errorMessage = this.validateField()
      if (this.errorMessage) {
        this.metaContentCtrl.setErrors({ incorrect: true })
      }
    }
  }

  ngOnInit() {
    this.metaDetails = sampleNumberData
    this.contentSubscription = this.contentService.changeActiveCont.subscribe(id => {
      if (this.metaContentCtrl) {
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
    this.metaContentCtrl.valueChanges.pipe(debounceTime(100)).subscribe(() => {
      this.storeData()
      this.errorMessage = this.validateField()
      if (!this.errorMessage) {
        this.metaContentCtrl.setErrors(null)
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
    this.textMaxValue = this.getValue(this.metaDetails.meta.validations.maxValue)
    this.textMinValue = this.getValue(this.metaDetails.meta.validations.minValue)
    this.tenantDisplayName = this.getValue(this.metaDetails.displayName)
    this.tenantPlaceholder = this.getValue(this.metaDetails.placeHolder)
  }

  /**
   * Gets updated value of the meta from service if null then assigns initial value to the field
   * @memberof EditMetaInputNumberComponent
   */
  createAndAssignForm() {
    this.metaContentCtrl = new FormControl()
    let textVal = this.contentService.getUpdatedMeta(this.currentId)[this.metaDetails.name as keyof NSContent.IContentMeta]
    if (!textVal && this.metaDetails.defaultValue && this.metaDetails.defaultValue.length) {
      textVal = this.getValue(this.metaDetails.defaultValue) || ''
    }
    this.metaContentCtrl.setValue(textVal)
  }

  /**
   * Validates whether the data entered by user meets the required conditions
   * @returns {string} returns empty string if all conditions are statisfied else return error message
   * @memberof EditMetaInputNumberComponent
   */
  validateField() {
    if (this.isSubmitPressed && this.isMandatory) {
      if (!this.metaContentCtrl.value) {
        return 'empty'
      }  if (this.textMaxValue && this.metaContentCtrl.value > this.textMaxValue) {
        return 'maxValue'
      }  if (this.textMinValue && this.metaContentCtrl.value < this.textMinValue) {
        return 'minValue'
      }
        return ''

    }
    return ''
  }

  /**
   * Function used to fetch default input properities for a meta by evaluating the given conditions.
   * @param data condition and data object array for which the evaluation needs to be done
   * @returns {T | null} returns value of the condition that passes evaluation and if none passed return null
   * @memberof EditMetaInputNumberComponent
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
   * @memberof EditMetaInputNumberComponent
   */
  storeData() {
    const originalMeta = this.contentService.getOriginalMeta(this.currentId)
    if (originalMeta && this.isEditEnabled) {
      const meta = <any>{}
      if (
        this.metaContentCtrl.value !==
        originalMeta[this.metaDetails.name as keyof NSContent.IContentMeta]
      ) {
        meta[this.metaDetails.name as keyof NSContent.IContentMeta] = this.metaContentCtrl.value
      }
      this.contentService.setUpdatedMeta(meta, this.currentId)
    }
  }

}
