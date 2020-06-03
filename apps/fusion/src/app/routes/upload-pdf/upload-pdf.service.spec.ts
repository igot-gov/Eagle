import { TestBed } from '@angular/core/testing'

import { UploadPdfService } from './upload-pdf.service'

describe('UploadPdfService', () => {
  beforeEach(() => TestBed.configureTestingModule({}))

  it('should be created', () => {
    const service: UploadPdfService = TestBed.get(UploadPdfService)
    expect(service).toBeTruthy()
  })
})
