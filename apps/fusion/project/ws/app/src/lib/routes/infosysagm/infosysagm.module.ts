import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'

import { InfosysagmRoutingModule } from './infosysagm-routing.module'
import { InfosysagmComponent } from './infosysagm/infosysagm.component'

@NgModule({
  declarations: [InfosysagmComponent],
  imports: [CommonModule, InfosysagmRoutingModule],
})
export class InfosysagmModule {}
