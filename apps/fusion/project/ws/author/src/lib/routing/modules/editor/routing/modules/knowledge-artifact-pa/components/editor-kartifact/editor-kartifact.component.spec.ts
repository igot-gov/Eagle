import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { EditorKartifactComponent } from './editor-kartifact.component'

describe('EditorKartifactComponent', () => {
  let component: EditorKartifactComponent
  let fixture: ComponentFixture<EditorKartifactComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EditorKartifactComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(EditorKartifactComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
