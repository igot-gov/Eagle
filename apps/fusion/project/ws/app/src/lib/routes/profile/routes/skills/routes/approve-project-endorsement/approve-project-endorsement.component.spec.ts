import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ApproveProjectEndorsementComponent } from './approve-project-endorsement.component'

describe('ApproveProjectEndorsementComponent', () => {
  let component: ApproveProjectEndorsementComponent
  let fixture: ComponentFixture<ApproveProjectEndorsementComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ApproveProjectEndorsementComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ApproveProjectEndorsementComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
