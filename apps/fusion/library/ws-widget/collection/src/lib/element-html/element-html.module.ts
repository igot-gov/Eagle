import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { ElementHtmlComponent } from '../element-html/element-html.component'
import { PipeSafeSanitizerModule, ExecuteScriptModule } from '@ws-widget/utils'

@NgModule({
  declarations: [ElementHtmlComponent],
  imports: [CommonModule, PipeSafeSanitizerModule, ExecuteScriptModule],
  entryComponents: [ElementHtmlComponent],
})
export class ElementHtmlModule {}
