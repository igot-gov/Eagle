import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { AddLearningPathComponent } from './add-learning-path.component'

describe('AddLearningPathComponent', () => {
  let component: AddLearningPathComponent
  let fixture: ComponentFixture<AddLearningPathComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AddLearningPathComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(AddLearningPathComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
