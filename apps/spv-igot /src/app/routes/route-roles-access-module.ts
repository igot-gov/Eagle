import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { CreateMDOModule } from '@ws/app'

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    CreateMDOModule,
  ],
  exports: [
    CreateMDOModule,
  ],
})
export class RouteAccessAppModule { }
