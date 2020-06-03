import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { AssistedgeLoginComponent } from './assistedge-login.component'

describe('AssistedgeLoginComponent', () => {
  let component: AssistedgeLoginComponent
  let fixture: ComponentFixture<AssistedgeLoginComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AssistedgeLoginComponent],
    }).compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(AssistedgeLoginComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
