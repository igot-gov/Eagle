import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { BadgesShareDialogComponent } from './badges-share-dialog.component'

describe('BadgesShareDialogComponent', () => {
  let component: BadgesShareDialogComponent
  let fixture: ComponentFixture<BadgesShareDialogComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [BadgesShareDialogComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(BadgesShareDialogComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
