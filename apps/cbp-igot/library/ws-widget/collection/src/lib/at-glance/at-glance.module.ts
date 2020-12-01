import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { AtGlanceComponent } from './at-glance.component'
import { WidgetResolverModule } from '@ws-widget/resolver'

@NgModule({
  declarations: [AtGlanceComponent],
  imports: [CommonModule, WidgetResolverModule],
  exports: [AtGlanceComponent],
  entryComponents: [AtGlanceComponent],
})
export class AtGlanceModule { }
