import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { ApprovalsModule } from '@ws/app'

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    ApprovalsModule,
  ],
  exports: [
    ApprovalsModule,
  ],
})
export class RouteApprovalsAppModule { }
