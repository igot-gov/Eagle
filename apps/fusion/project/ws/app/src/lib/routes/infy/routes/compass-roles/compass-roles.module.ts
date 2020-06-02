import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { CompassRoleComponent } from './routes/compass-role/compass-role.component'
import { LearningPathComponent } from './routes/learning-path/learning-path.component'
import { RoleCardComponent } from './components/role-card/role-card.component'
import { CompassRolesRoutingModule } from './compass-role-routing.module'

import { FormsModule, ReactiveFormsModule } from '@angular/forms'
import {
  MatButtonModule,
  MatCardModule,
  MatIconModule,
  MatInputModule,
  MatListModule,
  MatProgressSpinnerModule,
  MatRippleModule,
  MatToolbarModule,
  MatTooltipModule,
  MatGridListModule,
  MatFormFieldModule,
  MatSnackBarModule,
} from '@angular/material'

import {
  HorizontalScrollerModule,
  DefaultThumbnailModule,
} from '@ws-widget/utils'
import { BtnPageBackModule, ErrorResolverModule } from '@ws-widget/collection'
import { WidgetResolverModule } from '@ws-widget/resolver'

@NgModule({
  declarations: [CompassRoleComponent, LearningPathComponent, RoleCardComponent],
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    MatProgressSpinnerModule,
    MatRippleModule,
    MatToolbarModule,
    MatTooltipModule,
    MatGridListModule,
    MatFormFieldModule,
    MatSnackBarModule,
    FormsModule,
    ReactiveFormsModule,
    CompassRolesRoutingModule,
    HorizontalScrollerModule,
    DefaultThumbnailModule,
    BtnPageBackModule,
    ErrorResolverModule,
    WidgetResolverModule,
  ],
})
export class CompassRolesModule { }
