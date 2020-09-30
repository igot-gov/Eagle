import { NgModule } from '@angular/core'
import { CardCarrierComponent } from './card-carrier.component'
import { MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule, MatExpansionModule, MatIconModule, MatProgressSpinnerModule } from '@angular/material'
import { BrowserModule } from '@angular/platform-browser'
import { DiscussCardComponent } from '@ws/app/src/lib/routes/discuss/components/discuss-card/discuss-card.component'

@NgModule({
  declarations: [CardCarrierComponent, DiscussCardComponent],
  imports: [BrowserModule, MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule,
    MatExpansionModule, MatIconModule, MatProgressSpinnerModule],
  entryComponents: [CardCarrierComponent],
})
export class CardDiscussModule {

}
