import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { AcademyLoginComponent } from './academy-login.component'

describe('AcademyLoginComponent', () => {
  let component: AcademyLoginComponent
  let fixture: ComponentFixture<AcademyLoginComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AcademyLoginComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(AcademyLoginComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
