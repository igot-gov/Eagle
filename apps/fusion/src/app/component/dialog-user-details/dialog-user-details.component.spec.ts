import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { DialogUserDetailsComponent } from './dialog-user-details.component'

describe('DialogUserDetailsComponent', () => {
  let component: DialogUserDetailsComponent
  let fixture: ComponentFixture<DialogUserDetailsComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DialogUserDetailsComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogUserDetailsComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
