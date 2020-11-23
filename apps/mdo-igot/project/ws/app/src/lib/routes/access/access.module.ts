import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { AccessRoutingModule } from './access-routing.module'
import { HomeComponent } from './routes/home/home.component'
import { PrivilegesComponent } from './routes/privileges/privileges.component'
import { BtnPageBackModule, LeftMenuModule, GroupCheckboxModule } from '@ws-widget/collection'
import { WidgetResolverModule } from '@ws-widget/resolver'
import { HomeModule } from '../home/home.module'
import { RouterModule } from '@angular/router'

import {
  MatSidenavModule,
  MatIconModule,
} from '@angular/material'
@NgModule({
  declarations: [HomeComponent, PrivilegesComponent],
  imports: [CommonModule, AccessRoutingModule, BtnPageBackModule, LeftMenuModule, WidgetResolverModule,
    MatSidenavModule, MatIconModule, GroupCheckboxModule, HomeModule, RouterModule],
})
export class AccessModule { }
