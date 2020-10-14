import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { PipeFilterModule, PipeHtmlTagRemovalModule, PipeOrderByModule, PipeRelativeTimeModule } from '@ws-widget/utils'
import { LeftMenuComponent } from './components/left-menu/left-menu.component'
import { RightMenuComponent } from './components/right-menu/right-menu.component'
// import { BasicCKEditorComponent } from './components/basic-ckeditor/basic-ckeditor.component'
import { MatGridListModule } from '@angular/material/grid-list'
import { MatExpansionModule } from '@angular/material/expansion'
import { MatDividerModule } from '@angular/material/divider'
import { WidgetResolverModule } from '@ws-widget/resolver'
import {
  MatIconModule,
  MatListModule,
  MatFormFieldModule,
  MatDialogModule,
  MatSelectModule,
  MatInputModule,
  MatButtonModule,
  MatSidenavModule,
  MatChipsModule,
  MatProgressSpinnerModule,
  MatProgressBarModule,
} from '@angular/material'
import { MatCardModule } from '@angular/material/card'
import { ReactiveFormsModule, FormsModule } from '@angular/forms'
import { AvatarPhotoModule, BtnPageBackModule } from '@ws-widget/collection'
import { EditorSharedModule } from '@ws/author/src/lib/routing/modules/editor/shared/shared.module'
import { CkEditorModule } from 'library/ws-widget/collection/src/lib/_common/ck-editor/ck-editor.module'
import { LoaderService } from '@ws/author/src/lib/services/loader.service'
import { InitResolver } from './resolvers/init-resolve.service'
import { CKEditorService } from 'library/ws-widget/collection/src/lib/_common/ck-editor/ck-editor.service'
import { RouterModule } from '@angular/router'
import { ProfileV2RoutingModule } from './profile-v2.rounting.module'
import { ProfileComponent } from './routes/profile/profile.component'
import { ProfileViewComponent } from './routes/profile-view/profile-view.component'
import { DiscussModule } from '../discuss/discuss.module'

@NgModule({
  declarations: [
    ProfileComponent,
    ProfileViewComponent,
    LeftMenuComponent,
    RightMenuComponent,
  ],
  imports: [
    CommonModule,
    WidgetResolverModule,
    ReactiveFormsModule,
    ProfileV2RoutingModule,
    DiscussModule,
    FormsModule,
    RouterModule,
    MatGridListModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatDividerModule,
    MatIconModule,
    MatCardModule,
    MatChipsModule,
    MatListModule,
    MatSelectModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatDialogModule,
    MatButtonModule,
    MatSidenavModule,
    MatProgressSpinnerModule,
    MatProgressBarModule,
    PipeFilterModule,
    PipeHtmlTagRemovalModule,
    PipeRelativeTimeModule,
    AvatarPhotoModule,
    EditorSharedModule,
    CkEditorModule,
    PipeOrderByModule,
    BtnPageBackModule,
    WidgetResolverModule,
  ],
  entryComponents: [
  ],
  providers: [
    CKEditorService,
    LoaderService,
    InitResolver,
  ],
})
export class ProfileV2Module {

}
