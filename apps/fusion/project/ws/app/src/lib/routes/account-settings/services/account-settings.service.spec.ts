import { TestBed } from '@angular/core/testing'

import { AccountSettingsService } from './account-settings.service'

describe('AccountSettingsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: AccountSettingsService = TestBed.get(AccountSettingsService)
    expect(service).toBeTruthy()
  })
})
