import { CreateContentResolverService } from './services/create-content-resolver.service'
import { AuthViewerModule } from '@ws/author/src/lib/modules/viewer/viewer.module'
import { CommonModule } from '@angular/common'
import { NgModule } from '@angular/core'
import { EditorService } from './services/editor.service'
import { EditorComponent } from './components/editor/editor.component'
import { EditorRoutingModule } from './editor-routing.module'
import { SharedModule } from '@ws/author/src/lib/modules/shared/shared.module'
import { EditorSharedModule } from './shared/shared.module'
import { WebPageModule } from './routing/modules/web-page/web-page.module'
import { CompetenceService } from './shared/services/competence.service'

@NgModule({
  declarations: [
    EditorComponent,
  ],
  imports: [
    AuthViewerModule,
    CommonModule,
    EditorRoutingModule,
    EditorSharedModule,
    SharedModule,
    WebPageModule,
  ],
  providers: [
    CompetenceService,
    CreateContentResolverService,
    EditorService,
  ],
})
export class EditorModule { }
