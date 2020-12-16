import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ContentSummaryComponent } from './content-summary.component'

describe('ContentSummaryComponent', () => {
  let component: ContentSummaryComponent
  let fixture: ComponentFixture<ContentSummaryComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ContentSummaryComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ContentSummaryComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
