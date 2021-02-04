import { TestBed } from '@angular/core/testing'

import { SignupAutoService } from './signup-auto.service'

describe('SignupAutoService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: SignupAutoService = TestBed.get(SignupAutoService)
    expect(service).toBeTruthy()
  })
})
