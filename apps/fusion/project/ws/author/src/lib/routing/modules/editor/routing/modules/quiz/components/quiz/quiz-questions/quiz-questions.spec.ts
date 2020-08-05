import { async, ComponentFixture, TestBed } from '@angular/core/testing'
import { QuizQusetionsComponent } from './quiz-questions.component'

describe('QuizQusetionsComponent', () => {
  let component: QuizQusetionsComponent
  let fixture: ComponentFixture<QuizQusetionsComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [QuizQusetionsComponent],
    }).compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(QuizQusetionsComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
