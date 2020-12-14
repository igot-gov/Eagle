import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ContentStripVerticalComponent } from './content-strip-vertical.component'

describe('ContentStripVerticalComponent', () => {
  let component: ContentStripVerticalComponent
  let fixture: ComponentFixture<ContentStripVerticalComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ContentStripVerticalComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ContentStripVerticalComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
