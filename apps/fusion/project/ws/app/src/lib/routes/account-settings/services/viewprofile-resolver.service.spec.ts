import { TestBed } from '@angular/core/testing'

import { ViewprofileResolverService } from './viewprofile-resolver.service'

describe('ViewprofileResolverService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: ViewprofileResolverService = TestBed.get(ViewprofileResolverService)
    expect(service).toBeTruthy()
  })
})
