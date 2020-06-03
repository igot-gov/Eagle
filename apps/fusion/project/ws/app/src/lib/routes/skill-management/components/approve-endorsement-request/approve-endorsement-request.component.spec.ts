import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ApproveEndorsementRequestComponent } from './approve-endorsement-request.component'

describe('ApproveEndorsementRequestComponent', () => {
  let component: ApproveEndorsementRequestComponent
  let fixture: ComponentFixture<ApproveEndorsementRequestComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ApproveEndorsementRequestComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ApproveEndorsementRequestComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
