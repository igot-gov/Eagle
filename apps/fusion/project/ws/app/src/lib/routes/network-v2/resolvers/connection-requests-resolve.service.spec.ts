import { TestBed } from '@angular/core/testing';

import { ConnectionRequestsResolveService } from './connection-requests-resolve.service';

describe('ConnectionRequestsResolveService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ConnectionRequestsResolveService = TestBed.get(ConnectionRequestsResolveService);
    expect(service).toBeTruthy();
  });
});
