import { TestBed } from '@angular/core/testing'

import { MyConnectionResolveService } from './my-connection-resolve.service'

describe('MyConnectionResolveService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: MyConnectionResolveService = TestBed.get(MyConnectionResolveService)
    expect(service).toBeTruthy()
  })
})
