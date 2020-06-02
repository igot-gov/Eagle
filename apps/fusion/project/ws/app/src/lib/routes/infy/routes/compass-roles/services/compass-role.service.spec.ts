import { TestBed } from '@angular/core/testing'

import { CompassRoleService } from './compass-role.service'

describe('CompassRoleService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: CompassRoleService = TestBed.get(CompassRoleService)
    expect(service).toBeTruthy()
  })
})
