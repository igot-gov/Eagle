import { TestBed } from '@angular/core/testing'

import { UrlSerializerService } from './url-serializer.service'

describe('UrlSerializerService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: UrlSerializerService = TestBed.get(UrlSerializerService)
    expect(service).toBeTruthy()
  })
})
