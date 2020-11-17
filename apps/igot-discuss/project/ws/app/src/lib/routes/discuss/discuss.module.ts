import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { PipeFilterModule, PipeHtmlTagRemovalModule, PipeOrderByModule, PipeRelativeTimeModule, PipeListFilterModule } from '@ws-widget/utils'
import { DiscussComponent } from './routes/discuss-home/discuss.component'
import { DiscussCommetsComponent } from './components/discuss-comments/discuss-comments.component'
import { DiscussCategoriesComponent } from './routes/discuss-categories/discuss-categories.component'
import { DiscussGroupsComponent } from './routes/discuss-groups/discuss-groups.component'
import { DiscussLeaderboardComponent } from './routes/discuss-leaderboard/discuss-leaderboard.component'
import { DiscussMyDiscussionsComponent } from './routes/discuss-my-discussions/discuss-my-discussions.component'
import { DiscussTagsComponent } from './routes/discuss-tags/discuss-tags.component'
import { DiscussRoutingModule } from './dicuss.rounting.module'
import { DiscussCardComponent } from './components/discuss-card/discuss-card.component'
import { CategoryCardComponent } from './components/category-card/category-card.component'
import { LeftMenuComponent } from './components/left-menu/left-menu.component'
import { PostCardComponent } from './components/post-card/post-card.component'
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
} from '@angular/material'
import { MatCardModule } from '@angular/material/card'
import { DiscussAllComponent } from './routes/discuss-all/discuss-all.component'
import { DiscussStartComponent } from './components/discuss-start/discuss-start.component'
import { ReactiveFormsModule, FormsModule } from '@angular/forms'
import { TrendingTagsComponent } from './components/trending-tags/trending-tags.component'
import { DiscussionComponent } from './routes/discussion/discussion.component'
import { RelatedDiscussionComponent } from './components/related-discussion/related-discussion.component'
import { AvatarPhotoModule, BtnPageBackModule } from '@ws-widget/collection'
// import { EditorSharedModule } from '@ws/author/src/lib/routing/modules/editor/shared/shared.module'
// import { CkEditorModule } from 'library/ws-widget/collection/src/lib/_common/ck-editor/ck-editor.module'
// import { LoaderService } from '@ws/author/src/lib/services/loader.service'
import { InitResolver } from './resolvers/init-resolve.service'
// import { CKEditorService } from 'library/ws-widget/collection/src/lib/_common/ck-editor/ck-editor.service'
import { PaginationComponent } from './components/pagination/pagination.component'

@NgModule({
  declarations: [
    CategoryCardComponent,
    DiscussComponent,
    DiscussionComponent,
    DiscussAllComponent,
    DiscussCardComponent,
    DiscussCommetsComponent,
    DiscussCategoriesComponent,
    DiscussGroupsComponent,
    DiscussLeaderboardComponent,
    DiscussMyDiscussionsComponent,
    DiscussStartComponent,
    DiscussTagsComponent,
    LeftMenuComponent,
    PostCardComponent,
    RightMenuComponent,
    RelatedDiscussionComponent,
    TrendingTagsComponent,
    PaginationComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    DiscussRoutingModule,
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
    PipeFilterModule,
    PipeHtmlTagRemovalModule,
    PipeRelativeTimeModule,
    AvatarPhotoModule,
    // EditorSharedModule,
    // CkEditorModule,
    PipeOrderByModule,
    PipeListFilterModule,
    BtnPageBackModule,
    WidgetResolverModule,
  ],
  entryComponents: [
    DiscussStartComponent,
  ],
  providers: [
    // CKEditorService,
    // LoaderService,
    InitResolver,
  ],
  exports: [
    PostCardComponent,
  ],
})
export class DiscussModule {

}
