import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'

import { ChannelRoutingModule } from './channel-routing.module'
import { ChannelComponent } from './channel.component'

// custom modules
import { WidgetResolverModule } from '@ws-widget/resolver'

import {
  MatToolbarModule,
  MatIconModule,
  MatButtonModule,
  MatMenuModule,
  MatCardModule,
  MatTooltipModule,
  MatTabsModule,
  MatProgressBarModule,
  MatProgressSpinnerModule,
} from '@angular/material'
import { BtnPageBackModule } from '@ws-widget/collection'
import { TilesComponent } from './components/tiles/tiles.component'

@NgModule({
  declarations: [ChannelComponent, TilesComponent],
  imports: [
    CommonModule,
    ChannelRoutingModule,
    WidgetResolverModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatCardModule,
    MatTooltipModule,
    MatTabsModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    BtnPageBackModule,
  ],
})
export class ChannelModule { }
