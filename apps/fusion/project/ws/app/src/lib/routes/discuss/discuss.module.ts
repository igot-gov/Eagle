import { NgModule } from '@angular/core'

import { CommonModule } from '@angular/common'
import { DiscussComponent } from './components/discuss.component'
import { DiscussRoutingModule } from './dicuss.rounting.module'
import { DiscussCardComponent } from './components/discuss-card/discuss-card.component'
import { MatGridListModule } from '@angular/material/grid-list'
import { MatExpansionModule } from '@angular/material/expansion'
import { MatDividerModule } from '@angular/material/divider'
import { MatIconModule } from '@angular/material'

@NgModule({
  declarations: [DiscussComponent, DiscussCardComponent],
  imports: [CommonModule, DiscussRoutingModule, MatGridListModule, MatExpansionModule, MatDividerModule, MatIconModule],
})
export class DiscussModule {

}
