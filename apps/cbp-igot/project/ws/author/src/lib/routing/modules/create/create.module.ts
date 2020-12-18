import { SharedModule } from '@ws/author/src/lib/modules/shared/shared.module'
import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { CreateComponent } from './components/create/create.component'
import { EntityCardComponent } from './components/entity-card/entity-card.component'
import { RouterModule } from '@angular/router'
import { CreateService } from './components/create/create.service'
import { ConfirmationComponent } from './components/confirmation/confirmation.component'

@NgModule({
  declarations: [
    CreateComponent,
    EntityCardComponent,
    ConfirmationComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    RouterModule,
  ],
  exports: [CreateComponent],
  providers: [CreateService],
})

export class CreateModule { }
