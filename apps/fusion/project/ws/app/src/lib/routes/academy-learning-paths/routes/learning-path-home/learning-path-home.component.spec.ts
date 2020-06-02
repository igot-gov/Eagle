import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { LearningPathHomeComponent } from './learning-path-home.component'

describe('LearningPathHomeComponent', () => {
  let component: LearningPathHomeComponent
  let fixture: ComponentFixture<LearningPathHomeComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [LearningPathHomeComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(LearningPathHomeComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
