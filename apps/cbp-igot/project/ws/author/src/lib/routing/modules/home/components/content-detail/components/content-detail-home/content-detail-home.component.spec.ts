import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ContentDetailHomeComponent } from './content-detail-home.component'

describe('ContentDetailHomeComponent', () => {
  let component: ContentDetailHomeComponent
  let fixture: ComponentFixture<ContentDetailHomeComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ContentDetailHomeComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ContentDetailHomeComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
