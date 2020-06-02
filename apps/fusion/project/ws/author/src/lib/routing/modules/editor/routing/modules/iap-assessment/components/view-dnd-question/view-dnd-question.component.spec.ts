import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ViewDndQuestionComponent } from './view-dnd-question.component'

describe('ViewDndQuestionComponent', () => {
  let component: ViewDndQuestionComponent
  let fixture: ComponentFixture<ViewDndQuestionComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ViewDndQuestionComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewDndQuestionComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
