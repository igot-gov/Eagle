/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing'
import { By } from '@angular/platform-browser'
import { DebugElement } from '@angular/core'

import { EditMetaInputNumberComponent } from './edit-meta-input-number.component'

describe('EditMetaInputNumberComponent', () => {
  let component: EditMetaInputNumberComponent
  let fixture: ComponentFixture<EditMetaInputNumberComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EditMetaInputNumberComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(EditMetaInputNumberComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
