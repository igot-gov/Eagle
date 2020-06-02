import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { AccountSettingsModule } from '@ws/app'

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    AccountSettingsModule,
  ],
  exports: [
    AccountSettingsModule,
  ],
})
export class RouteAccountSettingsModule { }
