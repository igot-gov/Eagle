import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { EpochLoginComponent } from './epoch-login.component'

describe('EpochLoginComponent', () => {
  let component: EpochLoginComponent
  let fixture: ComponentFixture<EpochLoginComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EpochLoginComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(EpochLoginComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
