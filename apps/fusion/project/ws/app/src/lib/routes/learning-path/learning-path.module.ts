import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { LearningPathRoutingModule } from './learning-path-routing.module'
import { DynamicNetworkModule } from './dynamic-network/dynamic-network.module'
import { CareerDevelopmentPathModule } from './career-development-path/career-development-path.module'
import { BtnPageBackModule, PageModule } from '@ws-widget/collection'
@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    LearningPathRoutingModule,
    DynamicNetworkModule,
    CareerDevelopmentPathModule,
    BtnPageBackModule,
    PageModule,
  ],
})
export class LearningPathModule { }
