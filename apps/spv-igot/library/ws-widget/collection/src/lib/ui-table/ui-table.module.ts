import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { UIUserTableComponent } from './user-list/ui-user-table.component'
import { UIUserTablePopUpComponent } from './user-list-popup/ui-user-table-pop-up.component'
import { UIDirectoryTableComponent } from './directory-list/directory-table.component'
import { MatTableModule } from '@angular/material/table'
import { MatTooltipModule } from '@angular/material/tooltip'
import { MatSortModule } from '@angular/material/sort'
import { MatIconModule } from '@angular/material/icon'
import { AppButtonComponent } from '../app-button/app-button.component'
import { MatMenuModule } from '@angular/material/menu'
import { DefaultThumbnailModule, PipeCountTransformModule, PipeDurationTransformModule, PipeHtmlTagRemovalModule, PipePartialContentModule } from '@ws-widget/utils'
import { BtnChannelAnalyticsModule } from '../btn-channel-analytics/btn-channel-analytics.module'
import { BtnContentFeedbackV2Module } from '../btn-content-feedback-v2/btn-content-feedback-v2.module'
import { BtnContentLikeModule } from '../btn-content-like/btn-content-like.module'
import { BtnContentMailMeModule } from '../btn-content-mail-me/btn-content-mail-me.module'
import { MatPaginatorModule } from '@angular/material/paginator'
import { UserPopupComponent } from './user-popup/user-popup'
import { MatDialogModule, MatButtonModule, MatCheckboxModule } from '@angular/material'
import { FormsModule } from '@angular/forms'
import { MatRadioModule } from '@angular/material/radio'
import { BtnPageBackModule } from '@ws-widget/collection'
@NgModule({
  declarations: [UIUserTableComponent, AppButtonComponent, UIDirectoryTableComponent, UserPopupComponent, UIUserTablePopUpComponent],
  imports: [
    CommonModule,
    MatTableModule,
    MatTooltipModule,
    MatSortModule,
    MatIconModule,
    MatMenuModule,
    DefaultThumbnailModule, PipeCountTransformModule,
    PipeDurationTransformModule, PipeHtmlTagRemovalModule,
    PipePartialContentModule,
    BtnChannelAnalyticsModule,
    BtnContentFeedbackV2Module,
    BtnContentMailMeModule,
    BtnContentLikeModule,
    MatPaginatorModule,
    MatDialogModule, MatButtonModule,
    MatCheckboxModule,
    FormsModule,
    MatRadioModule,
    BtnPageBackModule,

  ],
  entryComponents: [UserPopupComponent],
  exports: [UIUserTableComponent, UIDirectoryTableComponent, UIUserTablePopUpComponent],
})
export class UITableModule { }
