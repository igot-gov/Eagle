import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { EditLearningPathComponent } from './edit-learning-path.component'

describe('EditLearningPathComponent', () => {
  let component: EditLearningPathComponent
  let fixture: ComponentFixture<EditLearningPathComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EditLearningPathComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(EditLearningPathComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
