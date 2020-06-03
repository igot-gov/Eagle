import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { KnowledgeArtifactPaComponent } from './knowledge-artifact-pa.component'

describe('KnowledgeArtifactPaComponent', () => {
  let component: KnowledgeArtifactPaComponent
  let fixture: ComponentFixture<KnowledgeArtifactPaComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [KnowledgeArtifactPaComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(KnowledgeArtifactPaComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
