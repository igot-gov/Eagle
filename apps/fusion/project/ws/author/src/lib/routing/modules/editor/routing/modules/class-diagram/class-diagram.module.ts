import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'

import { ClassDiagramRoutingModule } from './class-diagram-routing.module'
import { ClassDiagramComponent } from './component/class-diagram/class-diagram.component'
import { ClassDiagramStoreService } from './service/store.service'
import { ClassRelationEditorComponent } from './component/class-relation-editor/class-relation-editor.component'

import { SharedModule } from '@ws/author/src/lib/modules/shared/shared.module'
import { EditorSharedModule } from '@ws/author/src/lib/routing/modules/editor/shared/shared.module'
import { AuthViewerModule } from '@ws/author/src/lib/modules/viewer/viewer.module'

@NgModule({
  declarations: [
    ClassDiagramComponent,
    ClassRelationEditorComponent,

  ],
  imports: [
    CommonModule,
    SharedModule,
    EditorSharedModule,
    ClassDiagramRoutingModule,
    AuthViewerModule,
  ],
  providers: [ClassDiagramStoreService],
  entryComponents: [ClassRelationEditorComponent],
})

export class ClassDiagramModule { }
