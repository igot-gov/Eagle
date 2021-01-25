
import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { AccessRoutingModule } from './access-routing.module'
import { BtnPageBackModule, LeftMenuModule, GroupCheckboxModule, UITableModule } from '@ws-widget/collection'
import { WidgetResolverModule } from '@ws-widget/resolver'
import { HomeModule } from '../home/home.module'
import { RouterModule } from '@angular/router'
import { UsersComponent } from './routes/users/users.component'
import { UsersService } from './services/users.service'
import {
  MatSidenavModule,
  MatIconModule,
} from '@angular/material'
import { MatCardModule } from '@angular/material/card'

@NgModule({
  declarations: [UsersComponent],
  imports: [CommonModule, AccessRoutingModule, BtnPageBackModule, LeftMenuModule, WidgetResolverModule,
    MatSidenavModule, MatIconModule, GroupCheckboxModule, HomeModule, RouterModule, UITableModule, MatCardModule],
  exports: [UsersComponent],
  providers: [UsersService],
})
export class AccessModule { }
