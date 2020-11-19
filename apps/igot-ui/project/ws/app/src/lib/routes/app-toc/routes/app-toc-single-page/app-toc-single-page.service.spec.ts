import { TestBed } from '@angular/core/testing'

import { AppTocSinglePageService } from './app-toc-single-page.service'

describe('AppTocSinglePageService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: AppTocSinglePageService = TestBed.get(AppTocSinglePageService)
    expect(service).toBeTruthy()
  })
})
