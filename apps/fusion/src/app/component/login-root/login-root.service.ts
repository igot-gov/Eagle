import { Injectable, Type } from '@angular/core'
import { ConfigurationsService, EInstance } from '@ws-widget/utils'
import { FordLoginComponent } from '../ford-login/ford-login.component'
import { LoginComponent } from '../login/login.component'
// commercial_begin
import { PathfindersLoginComponent } from '../pathfinders-login/pathfinders-login.component'
import { LoginSiemensComponent } from '../login-siemsns/login-siemsns.component'
import { EpochLoginComponent } from '../epoch-login/epoch-login.component'
import { AcademyLoginComponent } from '../academy-login/academy-login.component'
import { AssistedgeLoginComponent } from '../assistedge-login/assistedge-login.component'
// import { EpochLoginV2Component } from '../epoch-login-v2/epoch-login-v2.component'
// commercial_end

@Injectable({
  providedIn: 'root',
})
export class LoginRootService {
  constructor(private configSvc: ConfigurationsService) {}

  getComponent(): Type<any> {
    // commercial_begin
    switch (this.configSvc.rootOrg) {
      case EInstance.PATHFINDERS:
        return PathfindersLoginComponent
      case EInstance.FORD:
        return FordLoginComponent
      case EInstance.SIEMENS:
        return LoginSiemensComponent
      case EInstance.EPOCH:
        return EpochLoginComponent
      case EInstance.ACADEMY:
        return AcademyLoginComponent
      case EInstance.ASSISTEDGE:
        return AssistedgeLoginComponent
      default:
        return LoginComponent
    }
    // commercial_end

    // opensource_begin
    // return LoginComponent
    // opensource_end
  }
}
