import { TestBed } from '@angular/core/testing'

import { ConceptGraphApiService } from './concept-graph-api.service'

describe('ConceptGraphApiService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: ConceptGraphApiService = TestBed.get(ConceptGraphApiService)
    expect(service).toBeTruthy()
  })
})
