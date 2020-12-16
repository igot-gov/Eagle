import { TestBed } from '@angular/core/testing'

import { SelfCurationService } from './self-curation.service'

describe('SelfCurationService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: SelfCurationService = TestBed.get(SelfCurationService)
    expect(service).toBeTruthy()
  })
})
