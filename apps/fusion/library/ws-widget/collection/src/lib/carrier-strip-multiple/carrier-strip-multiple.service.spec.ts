import { TestBed } from '@angular/core/testing'

import { ContentStripNewMultipleService } from './carrier-strip-multiple.service'

describe('ContentStripNewMultipleService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: ContentStripNewMultipleService = TestBed.get(ContentStripNewMultipleService)
    expect(service).toBeTruthy()
  })
})
