import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { CategoryAllDiscussionsComponent } from './category-all-discussions.component'

describe('CategoryAllDiscussionsComponent', () => {
  let component: CategoryAllDiscussionsComponent
  let fixture: ComponentFixture<CategoryAllDiscussionsComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CategoryAllDiscussionsComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(CategoryAllDiscussionsComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
