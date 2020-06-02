import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { RefactoringComponent } from './refactoring.component'

describe('RefactoringComponent', () => {
  let component: RefactoringComponent
  let fixture: ComponentFixture<RefactoringComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RefactoringComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(RefactoringComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
