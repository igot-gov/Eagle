import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { ExecuteScriptDirective } from './execute-script.directive'

@NgModule({
  declarations: [ExecuteScriptDirective],
  imports: [CommonModule],
  exports: [ExecuteScriptDirective],
})
export class ExecuteScriptModule {}
