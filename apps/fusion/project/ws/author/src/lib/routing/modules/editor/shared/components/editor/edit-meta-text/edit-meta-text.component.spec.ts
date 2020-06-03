/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing'
import { EditMetaTextComponent } from './edit-meta-text.component'

describe('EditMetaTextComponent', () => {
  let component: EditMetaTextComponent
  let fixture: ComponentFixture<EditMetaTextComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EditMetaTextComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(EditMetaTextComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
