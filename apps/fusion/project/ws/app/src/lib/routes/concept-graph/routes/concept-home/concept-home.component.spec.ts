import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ConceptHomeComponent } from './concept-home.component'

describe('ConceptHomeComponent', () => {
  let component: ConceptHomeComponent
  let fixture: ComponentFixture<ConceptHomeComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConceptHomeComponent],
    }).compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ConceptHomeComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
