import { TestBed } from '@angular/core/testing'

import { ClassDiagramStoreService } from './store.service'

describe('ClassDiagramStoreService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: ClassDiagramStoreService = TestBed.get(ClassDiagramStoreService)
    expect(service).toBeTruthy()
  })
})
