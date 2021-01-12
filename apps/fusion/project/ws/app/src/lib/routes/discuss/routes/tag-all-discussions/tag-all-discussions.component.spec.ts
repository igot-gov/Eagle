import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { TagAllDiscussionsComponent } from './tag-all-discussions.component'

describe('TagAllDiscussionsComponent', () => {
  let component: TagAllDiscussionsComponent
  let fixture: ComponentFixture<TagAllDiscussionsComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TagAllDiscussionsComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(TagAllDiscussionsComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
