import { TestBed } from '@angular/core/testing'

import { CompetenceUtilsService } from './competence-utils.service'

describe('CompetenceUtilsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: CompetenceUtilsService = TestBed.get(CompetenceUtilsService)
    expect(service).toBeTruthy()
  })
})
