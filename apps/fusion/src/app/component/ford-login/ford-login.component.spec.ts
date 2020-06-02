import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { FordLoginComponent } from './ford-login.component'

describe('FordLoginComponent', () => {
  let component: FordLoginComponent
  let fixture: ComponentFixture<FordLoginComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [FordLoginComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(FordLoginComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
