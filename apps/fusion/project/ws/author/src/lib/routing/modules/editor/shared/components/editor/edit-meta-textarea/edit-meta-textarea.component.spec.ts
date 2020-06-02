/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing'
import { EditMetaTextareaComponent } from './edit-meta-textarea.component'

describe('EditMetaTextareaComponent', () => {
  let component: EditMetaTextareaComponent
  let fixture: ComponentFixture<EditMetaTextareaComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EditMetaTextareaComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(EditMetaTextareaComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
