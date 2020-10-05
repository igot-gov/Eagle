import { TestBed } from '@angular/core/testing';

import { DiscussUtilsService } from './discuss-utils.service';

describe('DiscussUtilsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: DiscussUtilsService = TestBed.get(DiscussUtilsService);
    expect(service).toBeTruthy();
  });
});
