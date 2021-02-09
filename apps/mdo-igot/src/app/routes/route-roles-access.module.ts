import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { AccessModule } from '@ws/app'

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    AccessModule,
  ],
  exports: [
    AccessModule,
  ],
})
export class RouteAccessAppModule { }
