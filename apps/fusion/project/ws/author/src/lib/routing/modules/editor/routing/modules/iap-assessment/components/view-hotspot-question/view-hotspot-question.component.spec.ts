import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ViewHotspotQuestionComponent } from './view-hotspot-question.component'

describe('ViewHotspotQuestionComponent', () => {
  let component: ViewHotspotQuestionComponent
  let fixture: ComponentFixture<ViewHotspotQuestionComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ViewHotspotQuestionComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewHotspotQuestionComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
