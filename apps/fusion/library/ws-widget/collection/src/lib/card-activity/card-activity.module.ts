import { NgModule } from '@angular/core'
import { CardActivityComponent } from './card-activity.component'
import { MatProgressBarModule } from '@angular/material/progress-bar'
import { BrowserModule } from '@angular/platform-browser'

import { MatTooltipModule } from '@angular/material/tooltip'
import { MatGridListModule } from '@angular/material/grid-list'
import { HorizontalScrollerModule } from '@ws-widget/utils'
import {
  MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule, MatExpansionModule,
  MatIconModule, MatProgressSpinnerModule, MatFormFieldModule,
} from '@angular/material'

@NgModule({
  declarations: [CardActivityComponent],
  imports: [BrowserModule, MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule, MatGridListModule,
    MatExpansionModule, MatIconModule, MatProgressSpinnerModule, MatProgressBarModule, MatFormFieldModule,
    MatTooltipModule, HorizontalScrollerModule],
  entryComponents: [CardActivityComponent],
})
export class CardActivityModule {

}
