import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { AtGlanceComponent } from './at-glance.component'
import { WidgetResolverModule } from '@ws-widget/resolver'
import { RouterModule } from '@angular/router'
import { MatCardModule, MatDividerModule, MatIconModule } from '@angular/material'
import { PipeDurationTransformModule } from '@ws-widget/utils'

@NgModule({
  declarations: [AtGlanceComponent],
  imports: [CommonModule, WidgetResolverModule, RouterModule, MatCardModule, MatDividerModule, MatIconModule, PipeDurationTransformModule],
  exports: [AtGlanceComponent],
  entryComponents: [AtGlanceComponent],
})
export class AtGlanceModule { }
