import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { LearningPathDetailsComponent } from './learning-path-details.component'

describe('LearningPathDetailsComponent', () => {
  let component: LearningPathDetailsComponent
  let fixture: ComponentFixture<LearningPathDetailsComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [LearningPathDetailsComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(LearningPathDetailsComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
