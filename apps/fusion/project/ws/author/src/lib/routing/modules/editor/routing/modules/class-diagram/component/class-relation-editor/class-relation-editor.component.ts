import { Component, OnInit, Inject } from '@angular/core'
import { MAT_DIALOG_DATA, MatDialogRef, MatSnackBar } from '@angular/material'
import { FormArray, FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms'
import { debounceTime } from 'rxjs/operators'

import { IClassObject, IRelationObject, IOptionObject } from '../../interface/class-diagram.interface'

import { NotificationComponent } from '@ws/author/src/lib/modules/shared/components/notification/notification.component'
import { Notify } from '@ws/author/src/lib/constants/notificationMessage'
import { NOTIFICATION_TIME } from '../../constants/class-diagram.constants'

export interface IClassRelation {
  type: string,
  index: number,
  options: IOptionObject,
  action: string,
}

@Component({
  selector: 'ws-auth-class-relation-editor',
  templateUrl: './class-relation-editor.component.html',
  styleUrls: ['./class-relation-editor.component.scss'],
})
export class ClassRelationEditorComponent implements OnInit {

  result = {}
  originalClassName = ''
  /**
   * attributesCount & methodsCount are for allowing the user to delete
   */
  attributesCount = 0
  methodsCount = 0
  sourceArr: string[] = []
  targetArr: string[] = []
  optionsForm!: FormGroup

  constructor(
    public dialogRef: MatDialogRef<ClassRelationEditorComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IClassRelation,
    private snackbar: MatSnackBar,
    private formBuilder: FormBuilder,

  ) {
    if (this.data.action === 'add') {
      if (this.data.type === 'classes') {
        this.data.options.classes.push({ name: '', type: 'class', belongsTo: '', access: '' })
      } else {
        this.data.options.relations.push({ source: '', relation: 'is-a', target: '' })
      }
    }
    this.sourceArr = this.data.options.classes.filter(elem => elem.type === 'class').map(e => e.name)
    if (this.data.type === 'classes') {
      this.originalClassName = this.data.options.classes[this.data.index].name
      this.attributesCount = this.data.options.classes.filter(elem =>
        elem.belongsTo === this.originalClassName && elem.type === 'attribute').length
      this.methodsCount = this.data.options.classes.filter(elem =>
        elem.belongsTo === this.originalClassName && elem.type === 'method').length
    } else {
      const sourceName = this.data.options.relations[this.data.index].source
      this.targetArr = sourceName ? this.sourceArr.filter(name => name !== sourceName) : []
    }
    this.createForm()
  }

  ngOnInit() {

  }

  showError() {
    const className = (this.optionsForm.controls.classes as FormArray).at(this.data.index).value
    return this.optionsForm.controls.classes.value.filter(
      (elem: IClassObject) => elem.type === 'class' && elem.name === className).length > 1
  }

  canAcceptChange(): boolean {
    const updatedObj = this.optionsForm.controls.relations.value[0]
    const relationObjPresent = this.data.options.relations.filter(e => e.source === updatedObj.source &&
      e.target === updatedObj.target && e.relation === updatedObj.relation).length > 1
    if (relationObjPresent) {
      this.showNotification(Notify.RELATION_EXISTS)
      return false
    }
    return true
  }

  saveData() {
    this.assignFields()
    if (this.data.type === 'classes') {
      const updatedClasses = this.optionsForm.controls.classes.value
      const className = (this.optionsForm.controls.classes as FormArray).at(this.data.index).value.name
      if (!className) {
        this.showNotification(Notify.EMPTY_CLASS_NAME)
      } else if (updatedClasses.filter((e: IClassObject) => e.type === 'class' && e.name === className).length > 1) {
        this.showNotification(Notify.CLASS_NAME_TAKEN)
      } else if (updatedClasses.filter((e: IClassObject) => !e.name && e.type !== 'class').length) {
        this.showNotification(Notify.MANDATORY_FIELD_ERROR)
      } else if (this.methodsCount || this.attributesCount) {
        const updatedVal = this.optionsForm.value
        updatedVal.classes.map((e: IClassObject) => {
          if (e.type !== 'class' && e.belongsTo === this.originalClassName) {
            e.belongsTo = className
          }
        })
        updatedVal.relations = this.data.options.relations
        this.dialogRef.close(updatedVal)
      } else {
        this.showNotification(Notify.CLASS_NO_ATTRIBUTE_NO_METHOD)
      }
    } else {
      if (this.optionsForm.controls.relations.value.filter((e: IRelationObject) => !e.source || !e.target).length) {
        this.showNotification(Notify.MANDATORY_FIELD_ERROR)
      } else if (this.canAcceptChange()) {
        const updatedVal = this.data.options
        updatedVal.relations[this.data.index] = this.optionsForm.controls.relations.value[0]
        this.dialogRef.close(updatedVal)
      }
    }
  }

  createOptionControl(objectType: string, obj: any) {
    const classControl = this.formBuilder.group({
      name: [obj.name || '', [Validators.required]],
      access: [obj.access || '', Validators.required],
      type: [obj.type || '', Validators.required],
      belongsTo: [obj.belongsTo || '', Validators.required],
    })
    const relationControl = this.formBuilder.group({
      source: [obj.source || '', [Validators.required]],
      target: [obj.target || '', Validators.required],
      relation: [obj.relation || 'is-a', Validators.required],
    })
    const formArray = this.optionsForm.controls[objectType] as FormArray
    formArray.push(objectType === 'classes' ? classControl : relationControl)
  }

  createForm() {
    this.optionsForm = this.formBuilder.group({
      classes: this.formBuilder.array([]),
      relations: this.formBuilder.array([]),
    })
    if (this.data.type === 'classes') {
      this.data.options.classes.forEach(v => {
        this.createOptionControl('classes', v)
      })
    } else {
      this.createOptionControl('relations', this.data.options.relations[this.data.index])
    }
    if (this.data.type === 'relations' && this.optionsForm.controls.relations) {
      const optionsForm = this.optionsForm.controls.relations as FormArray
      (optionsForm.at(0).get('source') as AbstractControl).valueChanges
        .pipe(debounceTime(100)).subscribe(() => {
          const optionsArr = this.optionsForm.controls.relations as FormArray
          this.targetArr = this.sourceArr.filter(e => e !== optionsArr.at(0).value.source)
          if (optionsArr && optionsArr.at(0) && optionsArr.at(0).get('target')) {
            (optionsArr.at(0).get('target') as AbstractControl).setValue('')
          }
        })
    }
  }

  assignFields() {
    this.optionsForm.controls[this.data.type].markAllAsTouched()
  }

  addEntity(objectType: 'attribute' | 'method') {
    const opAccess = objectType === 'attribute' ? 'private' : 'public'
    const newOption: IClassObject = { name: '', type: objectType, access: opAccess, belongsTo: this.originalClassName }
    this.createOptionControl('classes', newOption)
    this.data.options.classes.push(newOption)
    if (objectType === 'attribute') {
      this.attributesCount = this.attributesCount + 1
    } else {
      this.methodsCount = this.methodsCount + 1
    }
  }

  removeEntity(objectType: string, index: number) {
    if (objectType === 'attribute') {
      this.attributesCount = this.attributesCount - 1
    } else {
      this.methodsCount = this.methodsCount - 1
    }
    const optionsArr = this.optionsForm.controls['classes'] as FormArray
    optionsArr.removeAt(index)
    this.data.options.classes.splice(index, 1)
  }

  showNotification(message: string) {
    this.snackbar.openFromComponent(NotificationComponent, {
      data: {
        type: message,
      },
      duration: NOTIFICATION_TIME * 1000,
    })
  }
}
