import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ConceptRootComponent } from './concept-root.component'

describe('ConceptRootComponent', () => {
  let component: ConceptRootComponent
  let fixture: ComponentFixture<ConceptRootComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConceptRootComponent],
    }).compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ConceptRootComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
