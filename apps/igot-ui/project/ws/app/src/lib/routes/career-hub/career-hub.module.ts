import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { MatGridListModule } from '@angular/material/grid-list'
import { MatExpansionModule } from '@angular/material/expansion'
import { MatDividerModule } from '@angular/material/divider'
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
import { CareerHubRoutingModule } from './career-hub-routing.module'
import { CareersHomeComponent } from './routes/careers-home/careers-home.component'
import { CareersComponent } from './routes/careers/careers.component'
import { LoaderService } from '@ws/author/src/public-api'
import { InitResolver } from '@ws/author/src/lib/services/init-resolve.service'
import { ReactiveFormsModule, FormsModule } from '@angular/forms'
import { BtnPageBackModule } from '@ws-widget/collection/src/public-api'
import { PipeOrderByModule } from '@ws-widget/utils/src/lib/pipes/pipe-order-by/pipe-order-by.module'
import { AvatarPhotoModule } from '@ws-widget/collection/src/lib/_common/avatar-photo/avatar-photo.module'
import { PipeHtmlTagRemovalModule } from '@ws-widget/utils/src/public-api'
import { PipeRelativeTimeModule } from '@ws-widget/utils/src/lib/pipes/pipe-relative-time/pipe-relative-time.module'
import { PipeFilterSearchModule } from '@ws-widget/utils/src/lib/pipes/pipe-filter-search/pipe-filter-search.module'
import { PipeFilterModule } from '@ws-widget/utils/src/lib/pipes/pipe-filter/pipe-filter.module'
import { CareersCardComponent } from './components/careers-card/careers-card.component'
import { CareerDetailComponent } from './routes/career-detail/career-detail.component'
import { RelatedPostsComponent } from './components/related-posts/related-posts.component'
import { WidgetResolverModule } from '@ws-widget/resolver'
import { CareersPaginationComponent } from './components/careers-pagination/careers-pagination.component'

@NgModule({
  declarations: [
    CareersHomeComponent,
    CareersComponent,
    CareersCardComponent,
    CareerDetailComponent,
    RelatedPostsComponent,
    CareersPaginationComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    CareerHubRoutingModule,
    MatGridListModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatDividerModule,
    MatIconModule,
    MatCardModule,
    MatChipsModule,
    MatListModule,
    MatSelectModule,
    MatInputModule,
    MatDialogModule,
    MatButtonModule,
    MatSidenavModule,
    MatProgressSpinnerModule,
    PipeFilterModule,
    PipeHtmlTagRemovalModule,
    PipeRelativeTimeModule,
    AvatarPhotoModule,
    PipeOrderByModule,
    PipeFilterSearchModule,
    BtnPageBackModule,
    WidgetResolverModule,
  ],
  providers: [
    LoaderService,
    InitResolver,
  ],
})
export class CareerHubModule { }
