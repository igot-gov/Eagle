import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { EditDepartmentDialogComponent } from './edit-department-dialog.component'

describe('EditDepartmentDialogComponent', () => {
  let component: EditDepartmentDialogComponent
  let fixture: ComponentFixture<EditDepartmentDialogComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EditDepartmentDialogComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(EditDepartmentDialogComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
