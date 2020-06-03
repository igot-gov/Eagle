import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { CompassRoleComponent } from './compass-role.component'

describe('CompassRoleComponent', () => {
  let component: CompassRoleComponent
  let fixture: ComponentFixture<CompassRoleComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CompassRoleComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(CompassRoleComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
