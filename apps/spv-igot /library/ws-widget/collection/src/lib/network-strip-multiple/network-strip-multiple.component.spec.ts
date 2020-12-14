import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ContentStripNewMultipleComponent } from './network-strip-multiple.component'

describe('ContentStripNewMultipleComponent', () => {
  let component: ContentStripNewMultipleComponent
  let fixture: ComponentFixture<ContentStripNewMultipleComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ContentStripNewMultipleComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ContentStripNewMultipleComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
