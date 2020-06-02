import { Component, OnInit, Input, OnChanges, OnDestroy } from '@angular/core'
import { FormControl } from '@angular/forms'
import { debounceTime } from 'rxjs/operators'
import { Subscription, Observable } from 'rxjs'
import { EditorContentService } from '@ws/author/src/lib/routing/modules/editor/services/editor-content.service'
import { IMetaUnit, IConditionValue, IMetaDropDownUnit } from '@ws/author/src/lib/routing/modules/editor/interface/meta.ts'
import { NSContent } from '@ws/author/src/lib/interface/content'
import { ConditionCheckService } from '@ws/author/src/lib/modules/shared/services/condition-check.service.ts'
import { EditorContentV2Service } from '@ws/author/src/lib/routing/modules/editor/services/editor-content-v2.service.ts'

import { dropDown, dropDownOptions, contentTypeList } from '../constants/edit-meta-childs-constants'
import { AuthInitService } from '@ws/author/src/lib/services/init.service.ts'

@Component({
  selector: 'ws-auth-edit-meta-dropdown',
  templateUrl: './edit-meta-dropdown.component.html',
  styleUrls: ['./edit-meta-dropdown.component.scss'],
})
export class EditMetaDropdownComponent implements OnInit, OnChanges, OnDestroy {

  @Input() isSubmitPressed = false
  @Input() metaName!: string
  isEditEnabled = false
  dropDownControl!: FormControl
  ordinals!: any
  contentSubscription?: Subscription
  dataChangeSubscription?: Subscription
  metaChangeSubscription?: Subscription
  currentId = ''
  metaDetails!: IMetaUnit<IMetaDropDownUnit>
  errorMessage: '' | 'minLength' | 'empty' | 'maxLength' = ''
  dropDownOptions: string[] = []
  isDropOptionsArrayOfObj = false
  isDisabled = false
  isMandatory = false
  maxLength: number | null = null
  minLength: number | null = null
  multiSelectEnabled: boolean | null = null
  tenantPlaceholder: string | null = null
  tenantDisplayName: string | null = null

  constructor(
    private contentService: EditorContentService,
    private conditionCheckService: ConditionCheckService,
    private editorContentSvcV2: EditorContentV2Service,
    private authInitService: AuthInitService,
    // private accessControl: AccessControlService,
  ) { }

  ngOnChanges() {
    if (this.isSubmitPressed && this.dropDownControl) {
      this.dropDownControl.markAsTouched()
      this.dropDownControl.markAsDirty()
      this.errorMessage = this.validateField()
      if (this.errorMessage) {
        this.dropDownControl.setErrors({ incorrect: true })
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
    this.metaDetails = dropDown
    this.ordinals = this.authInitService.ordinals
    this.contentSubscription = this.contentService.changeActiveCont.subscribe(id => {
      if (this.dropDownControl) {
        this.storeData()
      }
      this.currentId = id
      this.setUp()
      this.filterOrdinals()
      this.assignForm()
      this.errorMessage = this.validateField()
    })
    this.dataChangeSubscription = this.editorContentSvcV2.onContentChange.subscribe(() => {
      this.setUp()
      if (this.currentId) {
        this.assignForm()
      }
    })
    this.metaChangeSubscription = this.editorContentSvcV2.onMetaChange.subscribe(v => {
      this.setUp()
      if (v === 'contentType') {
        this.filterOrdinals()
      }
      if (this.currentId) {
        this.assignForm()
      }
    })
    this.dropDownControl.valueChanges.pipe(debounceTime(100)).subscribe(() => {
      this.storeData()
      this.errorMessage = this.validateField()
      if (!this.errorMessage) {
        this.dropDownControl.setErrors(null)
      }
    })
  }

  fetchDDOptions(): Observable<any[]> {
    return dropDownOptions as any
  }

  getDropOptions(): string[] {
    if (this.metaDetails.meta.ordinalsName) {
      return this.ordinals[this.metaDetails.meta.ordinalsName]
    } if (this.metaName === 'contentType') {
      const optionsList: string[] = []
      contentTypeList.map(con => {
        if (this.conditionCheckService.checkConditionV2(
          this.contentService.getUpdatedMeta(this.currentId),
          con.conditions
        )) {
          optionsList.push(con.value)
        }
      })
      return optionsList
    }
    // else{
    //   this.fetchDDOptions()
    // }
    return []
  }

  filterOrdinals() {
    const complexityLevelList: string[] = []
    this.ordinals.complexityLevel.map((v: any) => {
      if (v.condition) {
        let canAdd = false
          // tslint:disable-next-line: whitespace
          ; (v.condition.showFor || []).map((con: any) => {
            let innerCondition = false
            Object.keys(con).map(meta => {
              if (
                con[meta].indexOf(
                  this.contentService.getUpdatedMeta(this.currentId)[meta as keyof NSContent.IContentMeta] > -1
                )
              ) {
                innerCondition = true
              }
            })
            if (innerCondition) {
              canAdd = true
            }
          })
        if (canAdd) {
          // tslint:disable-next-line: semicolon // tslint:disable-next-line: whitespace
          ; (v.condition.nowShowFor || []).map((con: any) => {
            let innerCondition = false
            Object.keys(con).map(meta => {
              if (
                con[meta].indexOf(
                  this.contentService.getUpdatedMeta(this.currentId)[meta as keyof NSContent.IContentMeta] < 0
                )
              ) {
                innerCondition = true
              }
            })
            if (innerCondition) {
              canAdd = false
            }
          })
        }
        if (canAdd) {
          complexityLevelList.push(v.value)
        }
      } else {
        if (typeof v === 'string') {
          complexityLevelList.push(v)
        } else {
          complexityLevelList.push(v.value)
        }
      }
    })
  }

  /**
   * Function that gets and sets input field properities for the meta.
   * Function needs to be triggered on content data change or when backend changes value for canCascade properities
   */
  setUp() {
    // get text area parameters
    this.maxLength = this.getValue(this.metaDetails.meta.validations.maxLength)
    this.minLength = this.getValue(this.metaDetails.meta.validations.minLength)
    this.tenantDisplayName = this.getValue(this.metaDetails.displayName)
    this.dropDownOptions = this.getDropOptions()
    this.isDropOptionsArrayOfObj = false
    if (this.metaDetails.meta.displayValue && this.metaDetails.meta.storedValue) {
      this.isDropOptionsArrayOfObj = true
    }
    this.tenantPlaceholder = this.getValue(this.metaDetails.placeHolder)
    // get action parameters of meta
    this.isDisabled = this.metaDetails.isDisabled.find(c =>
      this.conditionCheckService.checkConditionV2(this.contentService.getUpdatedMeta(this.currentId), c)
    ) ? true : false
    this.dropDownControl = new FormControl({ value: '', disabled: this.isDisabled })
    this.isMandatory = this.metaDetails.isMandatory.find(c =>
      this.conditionCheckService.checkConditionV2(this.contentService.getUpdatedMeta(this.currentId), c)
    ) ? true : false
    this.isEditEnabled = this.contentService.isEditEnabled
  }

  /**
   * Gets updated value of the meta from service if null then assigns initial value to the field
   * @memberof EditMetaDropdownComponent
   */
  assignForm() {
    let textVal = this.contentService.getUpdatedMeta(this.currentId)[this.metaDetails.name as keyof NSContent.IContentMeta]
    if (!textVal && this.metaDetails.defaultValue.length) {
      textVal = this.getValue(this.metaDetails.defaultValue) || ''
    }
    // console.log(textVal)
    this.dropDownControl.setValue(textVal)
  }

  /**
   * Validates whether the data entered by user meets the required conditions
   * @returns {string} returns empty string if all conditions are statisfied else return error message
   * @memberof EditMetaDropdownComponent
   */
  validateField() {
    if (this.isSubmitPressed && this.isMandatory) {
      if (!this.dropDownControl.value) {
        return 'empty'
      }  if (this.maxLength && this.dropDownControl.value > this.maxLength) {
        return 'maxLength'
      }  if (this.minLength && this.dropDownControl.value < this.minLength) {
        return 'minLength'
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
   * @memberof EditMetaDropdownComponent
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
   * @memberof EditMetaDropdownComponent
   */
  storeData() {
    const originalMeta = this.contentService.getOriginalMeta(this.currentId)
    if (originalMeta && this.isEditEnabled) {
      const meta = <any>{}
      if (
        this.dropDownControl.value !==
        originalMeta[this.metaDetails.name as keyof NSContent.IContentMeta]
      ) {
        meta[this.metaDetails.name as keyof NSContent.IContentMeta] = JSON.parse(JSON.stringify(this.dropDownControl.value))
      }
      this.contentService.setUpdatedMeta(meta, this.currentId)
    }
  }

}
