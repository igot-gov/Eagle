import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'

import { SocialRoutingModule } from './social-routing.module'
import { BlogsModule } from './routes/blogs/blogs.module'
import { QnaModule } from './routes/qna/qna.module'
import { PostFetchResolverService } from './resolvers/post-fetch-resolver.service'
import { SocialTimelineResolverService } from './resolvers/social-timeline-resolver.service'
import { ConfirmPublishComponent } from './widgets/confirm-publish/confirm-publish.component'
import { MatDialogModule, MatButtonModule } from '@angular/material'

@NgModule({
  declarations: [ConfirmPublishComponent],
  imports: [
    CommonModule,
    SocialRoutingModule,
    BlogsModule,
    QnaModule,
    MatDialogModule,
    MatButtonModule,
  ],
  providers: [PostFetchResolverService, SocialTimelineResolverService],
  entryComponents: [ConfirmPublishComponent],
})
export class SocialModule {}
