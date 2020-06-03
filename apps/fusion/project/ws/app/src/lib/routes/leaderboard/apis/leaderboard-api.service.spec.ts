import { TestBed } from '@angular/core/testing'

import { LeaderboardApiService } from './leaderboard-api.service'

describe('LeaderboardApiService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: LeaderboardApiService = TestBed.get(LeaderboardApiService)
    expect(service).toBeTruthy()
  })
})
