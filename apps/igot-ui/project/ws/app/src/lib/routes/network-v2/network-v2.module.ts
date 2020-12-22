import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { PipeFilterModule, PipeHtmlTagRemovalModule, PipeOrderByModule, PipeRelativeTimeModule } from '@ws-widget/utils'
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
import { ReactiveFormsModule, FormsModule } from '@angular/forms'
import { AvatarPhotoModule, BtnPageBackModule } from '@ws-widget/collection'
import { EditorSharedModule } from '@ws/author/src/lib/routing/modules/editor/shared/shared.module'
import { CkEditorModule } from 'library/ws-widget/collection/src/lib/_common/ck-editor/ck-editor.module'
import { LoaderService } from '@ws/author/src/lib/services/loader.service'
import { InitResolveService } from './resolvers/init-resolve.service'
import { RouterModule } from '@angular/router'

import { NetworkV2RoutingModule } from './network-v2-routing.module'
import { NetworkComponent } from './routes/network/network.component'
import { NetworkHomeComponent } from './routes/network-home/network-home.component'
import { LeftMenuComponent } from './components/left-menu/left-menu.component'
import { ConnectionRequestCardComponent } from './components/connection-request-card/connection-request-card.component'
import { ConnectionPeopleCardComponent } from './components/connection-people-card/connection-people-card.component'
import { RecommendedResolveService } from './resolvers/recommended-resolve.service'
import { NetworkMyConnectionComponent } from './routes/network-my-connection/network-my-connection.component'
import { MyConnectionResolveService } from './resolvers/my-connection-resolve.service'
import { MyConnectionCardComponent } from './components/my-connection-card/my-connection-card.component'
import { PipeFilterSearchModule } from '@ws-widget/utils/src/lib/pipes/pipe-filter-search/pipe-filter-search.module'
import { NetworkMyMdoComponent } from './routes/network-my-mdo/network-my-mdo.component'
import { NetworkRecommendedComponent } from './routes/network-recommended/network-recommended.component'
import { NetworkConnectionRequestsComponent } from './routes/network-connection-requests/network-connection-requests.component'
import { ConnectionRequestResolveService } from './resolvers/connection-request-resolve.service'
import { ConnectionSearchCardComponent } from './components/connection-search-card/connection-search-card.component'
import { ConnectionRecommendedCardComponent } from './components/connection-recommended-card/connection-recommended-card.component'

@NgModule({
  declarations: [
    NetworkComponent,
    NetworkHomeComponent,
    LeftMenuComponent,
    ConnectionRequestCardComponent,
    ConnectionPeopleCardComponent,
    NetworkMyConnectionComponent,
    MyConnectionCardComponent,
    NetworkMyMdoComponent,
    NetworkRecommendedComponent,
    NetworkConnectionRequestsComponent,
    ConnectionSearchCardComponent,
    ConnectionRecommendedCardComponent,
  ],
  imports: [
    CommonModule,
    NetworkV2RoutingModule,
    WidgetResolverModule,
    ReactiveFormsModule,
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
    PipeFilterModule,
    PipeHtmlTagRemovalModule,
    PipeRelativeTimeModule,
    PipeFilterSearchModule,
    AvatarPhotoModule,
    EditorSharedModule,
    CkEditorModule,
    PipeOrderByModule,
    BtnPageBackModule,
    WidgetResolverModule,
  ],
  providers: [
    LoaderService,
    InitResolveService,
    RecommendedResolveService,
    ConnectionRequestResolveService,
    MyConnectionResolveService,
  ],
})
export class NetworkV2Module { }
