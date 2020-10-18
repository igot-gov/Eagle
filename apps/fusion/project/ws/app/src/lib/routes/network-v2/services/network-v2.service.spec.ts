import { TestBed } from '@angular/core/testing'

import { NetworkV2Service } from './network-v2.service'

describe('NetworkV2Service', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: NetworkV2Service = TestBed.get(NetworkV2Service)
    expect(service).toBeTruthy()
  })
})
