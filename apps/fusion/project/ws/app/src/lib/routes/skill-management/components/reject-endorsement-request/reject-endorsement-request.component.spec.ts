import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { RejectEndorsementRequestComponent } from './reject-endorsement-request.component'

describe('RejectEndorsementRequestComponent', () => {
  let component: RejectEndorsementRequestComponent
  let fixture: ComponentFixture<RejectEndorsementRequestComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RejectEndorsementRequestComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(RejectEndorsementRequestComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
