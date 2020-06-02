import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { LayoutEmbedComponent } from './layout-embed.component'
import { WidgetResolverModule } from '@ws-widget/resolver'

@NgModule({
  declarations: [LayoutEmbedComponent],
  imports: [
    CommonModule,
    WidgetResolverModule,
  ],
  entryComponents: [LayoutEmbedComponent],
  exports: [LayoutEmbedComponent],
})
export class LayoutEmbedModule { }
