import { TestBed } from '@angular/core/testing'

import { ConceptGraphService } from './concept-graph.service'

describe('ConceptGraphService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: ConceptGraphService = TestBed.get(ConceptGraphService)
    expect(service).toBeTruthy()
  })
})
