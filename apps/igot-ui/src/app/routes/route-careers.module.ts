import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { CareerHubModule } from '@ws/app'

@NgModule({
  imports: [CommonModule, CareerHubModule],
  exports: [CareerHubModule],
})
export class RouteCareerHubModule {

}
