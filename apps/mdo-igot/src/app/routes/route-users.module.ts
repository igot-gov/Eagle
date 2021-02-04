import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { UsersModule } from '@ws/app'

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    UsersModule,
  ],
  exports: [
    UsersModule,
  ],
})
export class RouteUsersAppModule { }
