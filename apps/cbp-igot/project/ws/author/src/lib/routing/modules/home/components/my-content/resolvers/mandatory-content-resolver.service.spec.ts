import { TestBed } from '@angular/core/testing'

import { MandatoryContentResolverService } from './mandatory-content-resolver.service'

describe('MandatoryContentResolverService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: MandatoryContentResolverService = TestBed.get(MandatoryContentResolverService)
    expect(service).toBeTruthy()
  })
})
