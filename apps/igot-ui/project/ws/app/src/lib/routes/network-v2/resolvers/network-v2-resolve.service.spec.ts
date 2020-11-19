import { TestBed } from '@angular/core/testing'

import { NetworkV2ResolveService } from './network-v2-resolve.service'

describe('NetworkV2ResolveService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: NetworkV2ResolveService = TestBed.get(NetworkV2ResolveService)
    expect(service).toBeTruthy()
  })
})
