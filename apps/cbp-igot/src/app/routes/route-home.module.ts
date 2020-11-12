import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { HomeModule } from '@ws/app'

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    HomeModule,
  ],
  exports: [
    HomeModule,
  ],
})
export class RouteHomeAppModule { }
