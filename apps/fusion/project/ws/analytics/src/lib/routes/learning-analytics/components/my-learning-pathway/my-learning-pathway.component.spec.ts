import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { MyLearningPathwayComponent } from './my-learning-pathway.component'

describe('MyLearningPathwayComponent', () => {
  let component: MyLearningPathwayComponent
  let fixture: ComponentFixture<MyLearningPathwayComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MyLearningPathwayComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(MyLearningPathwayComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
