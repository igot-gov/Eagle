import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ContentDiscussionComponent } from './content-discussion.component'

describe('ContentDiscussionComponent', () => {
  let component: ContentDiscussionComponent
  let fixture: ComponentFixture<ContentDiscussionComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ContentDiscussionComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ContentDiscussionComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
