import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ContentSelfCurationComponent } from './content-self-curation.component'

describe('ContentSelfCurationComponent', () => {
  let component: ContentSelfCurationComponent
  let fixture: ComponentFixture<ContentSelfCurationComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ContentSelfCurationComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ContentSelfCurationComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
