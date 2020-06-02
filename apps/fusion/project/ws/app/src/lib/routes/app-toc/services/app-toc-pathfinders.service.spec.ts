import { TestBed } from '@angular/core/testing'

import { AppTocPathfindersService } from './app-toc-pathfinders.service'

describe('AppTocPathfindersService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: AppTocPathfindersService = TestBed.get(AppTocPathfindersService)
    expect(service).toBeTruthy()
  })
})
