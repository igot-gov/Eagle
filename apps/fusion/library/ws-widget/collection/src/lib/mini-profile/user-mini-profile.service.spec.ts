import { TestBed } from '@angular/core/testing'

import { UserMiniProfileService } from './user-mini-profile.service'

describe('UserMiniProfileService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: UserMiniProfileService = TestBed.get(UserMiniProfileService)
    expect(service).toBeTruthy()
  })
})
