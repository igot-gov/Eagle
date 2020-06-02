import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ConceptGraphComponent } from './concept-graph.component'

describe('ConceptGraphComponent', () => {
  let component: ConceptGraphComponent
  let fixture: ComponentFixture<ConceptGraphComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConceptGraphComponent],
    }).compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ConceptGraphComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
