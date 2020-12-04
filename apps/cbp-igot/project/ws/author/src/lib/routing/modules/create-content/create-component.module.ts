import { SharedModule } from '@ws/author/src/lib/modules/shared/shared.module'
import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { RouterModule } from '@angular/router'
import { CreateHomeComponent } from './components/create-home/create-home.component'
import { CreateComponentRoutingModule } from './create-component-routing.module'

@NgModule({
  declarations: [
    CreateHomeComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    RouterModule,
    CreateComponentRoutingModule,
  ],
  providers: [],
})

export class CreateContentModule { }
