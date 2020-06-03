/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing'
import { By } from '@angular/platform-browser'
import { DebugElement } from '@angular/core'

import { EditMetaDropdownComponent } from './edit-meta-dropdown.component'

describe('EditMetaDropdownComponent', () => {
  let component: EditMetaDropdownComponent
  let fixture: ComponentFixture<EditMetaDropdownComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EditMetaDropdownComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(EditMetaDropdownComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
