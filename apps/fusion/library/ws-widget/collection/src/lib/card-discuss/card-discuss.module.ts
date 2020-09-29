import { NgModule } from '@angular/core'
import { CardDiscussComponent } from './card-discuss.component'
import { MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule, MatExpansionModule, MatIconModule, MatProgressSpinnerModule } from '@angular/material'
import { BrowserModule } from '@angular/platform-browser'
import { DiscussCardComponent } from '@ws/app/src/lib/routes/discuss/components/discuss-card/discuss-card.component'

@NgModule({
  declarations: [CardDiscussComponent, DiscussCardComponent],
  imports: [BrowserModule, MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule,
    MatExpansionModule, MatIconModule, MatProgressSpinnerModule],
  entryComponents: [CardDiscussComponent],
})
export class CardDiscussModule {

}
