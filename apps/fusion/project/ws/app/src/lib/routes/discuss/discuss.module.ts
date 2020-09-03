import { NgModule } from '@angular/core'

import { CommonModule } from '@angular/common'
import { DiscussComponent } from './components/discuss.component'
import { DiscussCommetsComponent } from './components/discuss-comments/discuss-comments.component'
import { DiscussRoutingModule } from './dicuss.rounting.module'
import { DiscussCardComponent } from './components/discuss-card/discuss-card.component'
import { MatGridListModule } from '@angular/material/grid-list'
import { MatExpansionModule } from '@angular/material/expansion'
import { MatDividerModule } from '@angular/material/divider'
import { MatIconModule } from '@angular/material'
import { MatCardModule } from '@angular/material/card'

@NgModule({
  declarations: [DiscussComponent, DiscussCardComponent, DiscussCommetsComponent],
  imports: [CommonModule, DiscussRoutingModule, MatGridListModule, MatExpansionModule, MatDividerModule, MatIconModule, MatCardModule],
})
export class DiscussModule {

}
