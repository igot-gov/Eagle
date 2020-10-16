import { TestBed } from '@angular/core/testing'

import { ConnectionRequestResolveService } from './connection-request-resolve.service'

describe('ConnectionRequestResolveService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: ConnectionRequestResolveService = TestBed.get(ConnectionRequestResolveService)
    expect(service).toBeTruthy()
  })
})
