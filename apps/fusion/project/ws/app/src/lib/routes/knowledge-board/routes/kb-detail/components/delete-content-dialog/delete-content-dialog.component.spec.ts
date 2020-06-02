import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { DeleteContentDialogComponent } from './delete-content-dialog.component'

describe('DeleteContentDialogComponent', () => {
  let component: DeleteContentDialogComponent
  let fixture: ComponentFixture<DeleteContentDialogComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DeleteContentDialogComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(DeleteContentDialogComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
