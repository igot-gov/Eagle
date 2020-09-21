import { NgModule } from '@angular/core'
import { CardHomeNetworkComponent } from './card-home-network.component'
import { MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule, MatExpansionModule, MatIconModule, MatProgressSpinnerModule } from '@angular/material'

@NgModule({
  declarations: [CardHomeNetworkComponent],
  imports: [MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule, MatExpansionModule, MatIconModule, MatProgressSpinnerModule],
  entryComponents: [CardHomeNetworkComponent],
})
export class CardHomeNetworkModule {

}
