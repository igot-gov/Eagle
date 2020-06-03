import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ConfirmPublishComponent } from './confirm-publish.component'

describe('ConfirmPublishComponent', () => {
  let component: ConfirmPublishComponent
  let fixture: ComponentFixture<ConfirmPublishComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConfirmPublishComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmPublishComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
