import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ViewSliderQuestionComponent } from './view-slider-question.component'

describe('ViewSliderQuestionComponent', () => {
  let component: ViewSliderQuestionComponent
  let fixture: ComponentFixture<ViewSliderQuestionComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ViewSliderQuestionComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewSliderQuestionComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
