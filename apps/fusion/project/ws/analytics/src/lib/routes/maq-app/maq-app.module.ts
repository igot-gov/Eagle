import { CommonModule } from '@angular/common'
import { NgModule } from '@angular/core'
import { MatButtonModule, MatCardModule, MatIconModule, MatInputModule, MatListModule, MatProgressSpinnerModule, MatSnackBarModule, MatToolbarModule, MatTooltipModule } from '@angular/material'
import { BtnPageBackModule } from '@ws-widget/collection'
import { WidgetResolverModule } from '@ws-widget/resolver'
import { MaqAppRoutingModule } from './maq-app-routing.module'
import { MaqAppComponent } from './maq-app.component'
@NgModule({
  declarations: [MaqAppComponent],
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    MatListModule,
    MatSnackBarModule,
    MatInputModule,
    MatTooltipModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatToolbarModule,
    WidgetResolverModule,
    BtnPageBackModule,
    MaqAppRoutingModule,
  ],
})
export class MaqAppModule { }
