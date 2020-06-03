import { TestBed } from '@angular/core/testing'

import { DynamicLearningSrvService } from './dynamic-learning-srv.service'

describe('DynamicLearningSrvService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: DynamicLearningSrvService = TestBed.get(DynamicLearningSrvService)
    expect(service).toBeTruthy()
  })
})
