import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { KnowledgeBoardComponent } from './knowledge-board.component'

describe('KnowledgeBoardComponent', () => {
  let component: KnowledgeBoardComponent
  let fixture: ComponentFixture<KnowledgeBoardComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [KnowledgeBoardComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(KnowledgeBoardComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
