import { Injectable } from '@angular/core'
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router'
import { Observable, of } from 'rxjs'
import { map, catchError } from 'rxjs/operators'
import { } from '@ws-widget/collection'
import { IResolveResponse } from '@ws-widget/utils'
import { NetworkV2Service } from '../services/network-v2.service'
import { NSNetworkDataV2 } from '../models/network-v2.model'

@Injectable({
  providedIn: 'root',
})
export class MyConnectionResolveService implements
  Resolve<Observable<IResolveResponse<NSNetworkDataV2.IEstablishedConnectResopnse>> |
  IResolveResponse<NSNetworkDataV2.IEstablishedConnectResopnse>> {
  constructor(private networkV2Service: NetworkV2Service) { }

  resolve(
    _route: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot,
  ): Observable<IResolveResponse<NSNetworkDataV2.IEstablishedConnectResopnse>> {
    return this.networkV2Service.fetchAllConnectionEstablished().pipe(
      map((data: any) => ({ data, error: null })),
      catchError(error => of({ error, data: null })),
    )
  }
}
