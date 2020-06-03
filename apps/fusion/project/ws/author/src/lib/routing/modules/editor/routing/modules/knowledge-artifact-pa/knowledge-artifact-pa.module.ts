import { EditorKartifactComponent } from './components/editor-kartifact/editor-kartifact.component'
import { KnowledgeArtifactPaRoutingModule } from './knowledge-artifact-pa.routing.module'
import { KnowledgeArtifactPaComponent } from './components/knowledge-artifact-pa/knowledge-artifact-pa.component'
import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { EditorSharedModule } from '../../../shared/shared.module'
import { SharedModule } from '../../../../../../modules/shared/shared.module'
import { UploadKartifactComponent } from './components/upload-kartifact/upload-kartifact.component'

@NgModule({
  declarations: [
    KnowledgeArtifactPaComponent,
    EditorKartifactComponent,
    UploadKartifactComponent,
  ],
  exports: [
    KnowledgeArtifactPaComponent,
  ],
  imports: [
    CommonModule,
    EditorSharedModule,
    SharedModule,
    KnowledgeArtifactPaRoutingModule,
  ],
})
export class KnowledgeArtifactPaModule { }
