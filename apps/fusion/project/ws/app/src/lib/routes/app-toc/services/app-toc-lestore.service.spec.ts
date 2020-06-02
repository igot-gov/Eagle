import { TestBed } from '@angular/core/testing'

import { AppTocLestoreService } from './app-toc-lestore.service'

describe('AppTocLestoreService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: AppTocLestoreService = TestBed.get(AppTocLestoreService)
    expect(service).toBeTruthy()
  })
})
