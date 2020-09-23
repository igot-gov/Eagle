import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { DiscussModule } from '@ws/app'

@NgModule({
  imports: [CommonModule, DiscussModule],
  exports: [DiscussModule],
})
export class RouteDiscussModule {

}
