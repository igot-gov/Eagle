import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ContentQualityComponent } from './content-quality.component'

describe('ContentQualityComponent', () => {
  let component: ContentQualityComponent
  let fixture: ComponentFixture<ContentQualityComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ContentQualityComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ContentQualityComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
