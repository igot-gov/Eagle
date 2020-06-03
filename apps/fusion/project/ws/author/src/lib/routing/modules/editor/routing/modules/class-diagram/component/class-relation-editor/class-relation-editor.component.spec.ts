import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ClassRelationEditorComponent } from './class-relation-editor.component'

describe('ClassRelationEditorComponent', () => {
  let component: ClassRelationEditorComponent
  let fixture: ComponentFixture<ClassRelationEditorComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ClassRelationEditorComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ClassRelationEditorComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
