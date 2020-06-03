import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { DynamicNetworkComponent } from './routes/dynamic-network/dynamic-network.component'
import {
  MatToolbarModule,
  MatCardModule,
  MatIconModule,
  MatProgressSpinnerModule,
  MatButtonModule,
  MatDividerModule,
} from '@angular/material'
import { BtnPageBackModule } from '@ws-widget/collection'
import { CareerDevelopmentPathModule } from '../career-development-path/career-development-path.module'
import { HorizontalScrollerModule, DefaultThumbnailModule } from '@ws-widget/utils'
import { WidgetResolverModule } from '@ws-widget/resolver'
import { RouterModule } from '@angular/router'

@NgModule({
  declarations: [DynamicNetworkComponent],
  imports: [
    CommonModule,
    MatToolbarModule,
    BtnPageBackModule,
    CareerDevelopmentPathModule,
    HorizontalScrollerModule,
    DefaultThumbnailModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatButtonModule,
    MatDividerModule,
    WidgetResolverModule,
    RouterModule,
  ],
  exports: [DynamicNetworkComponent],
})
export class DynamicNetworkModule {}
