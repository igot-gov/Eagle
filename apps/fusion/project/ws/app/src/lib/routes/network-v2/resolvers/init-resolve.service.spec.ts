import { TestBed } from '@angular/core/testing'

import { InitResolveService } from './init-resolve.service'

describe('InitResolveService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: InitResolveService = TestBed.get(InitResolveService)
    expect(service).toBeTruthy()
  })
})
