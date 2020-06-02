import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { EpochLoginV2Component } from './epoch-login-v2.component'

describe('EpochLoginV2Component', () => {
  let component: EpochLoginV2Component
  let fixture: ComponentFixture<EpochLoginV2Component>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EpochLoginV2Component],
    }).compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(EpochLoginV2Component)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
