import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { UserDepartmentManagementComponent } from './user-department-management.component'

describe('UserDepartmentManagementComponent', () => {
  let component: UserDepartmentManagementComponent
  let fixture: ComponentFixture<UserDepartmentManagementComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [UserDepartmentManagementComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(UserDepartmentManagementComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
