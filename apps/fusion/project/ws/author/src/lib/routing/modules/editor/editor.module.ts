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
import { ContentQualityCheckPopupComponent } from './shared/components/content-quality-popup/content-quality-popup'
import { ContentQualityCheckModule } from './shared/components/content-quality-check/content-quality-check.module'
import { ContentQualityService } from './shared/components/content-quality-check/content-quality-check.service'
@NgModule({
  declarations: [
    EditorComponent,
    ContentQualityCheckPopupComponent,
  ],
  imports: [
    AuthViewerModule,
    CommonModule,
    EditorRoutingModule,
    EditorSharedModule,
    SharedModule,
    WebPageModule,
    ContentQualityCheckModule,
  ],
  providers: [
    EditorService,
    CreateContentResolverService,
    ContentQualityService,
  ],
  entryComponents: [ContentQualityCheckPopupComponent]
})
export class EditorModule { }
