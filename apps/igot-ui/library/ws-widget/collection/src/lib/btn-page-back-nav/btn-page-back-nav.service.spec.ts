import { TestBed } from '@angular/core/testing'

import { BtnPageBackNavService } from './btn-page-back-nav.service'

describe('BtnPageBackNavService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: BtnPageBackNavService = TestBed.get(BtnPageBackNavService)
    expect(service).toBeTruthy()
  })
})
