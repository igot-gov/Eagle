import { NgModule } from '@angular/core'
import { CardCarrierHomeComponent } from './card-carrier-home.component'
import { CardCarrierComponent } from '../card-carrier/card-carrier.component'
import { MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule, MatExpansionModule, MatIconModule, MatProgressSpinnerModule } from '@angular/material'
import { BrowserModule } from '@angular/platform-browser'
import { PipeRelativeTimeModule } from '@ws-widget/utils/src/public-api'
@NgModule({
  declarations: [CardCarrierHomeComponent, CardCarrierComponent],
  imports: [
    BrowserModule,
    MatButtonModule,
    MatCardModule,
    MatChipsModule,
    MatDividerModule,
    MatExpansionModule,
    MatIconModule,
    MatProgressSpinnerModule,
    PipeRelativeTimeModule,
  ],
  entryComponents: [CardCarrierHomeComponent],
})
export class CardCarrierHomeModule {

}
