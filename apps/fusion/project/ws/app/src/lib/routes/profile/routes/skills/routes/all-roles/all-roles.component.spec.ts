import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { AllRolesComponent } from './all-roles.component'

describe('AllRolesComponent', () => {
  let component: AllRolesComponent
  let fixture: ComponentFixture<AllRolesComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AllRolesComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(AllRolesComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
