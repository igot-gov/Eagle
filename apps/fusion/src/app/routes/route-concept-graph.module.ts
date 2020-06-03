import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { ConceptGraphModule } from '@ws/app'

@NgModule({
  declarations: [],
  imports: [CommonModule, ConceptGraphModule],
  exports: [ConceptGraphModule],
})
export class RouteConceptGraphModule {}
