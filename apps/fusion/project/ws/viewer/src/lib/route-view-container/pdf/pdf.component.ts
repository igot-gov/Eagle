import { Component, Input, OnInit } from '@angular/core'
import { NsContent, NsDiscussionForum } from '@ws-widget/collection'
import { NsWidgetResolver } from '@ws-widget/resolver'
import { ActivatedRoute } from '@angular/router'
import { ConfigurationsService } from '../../../../../../../library/ws-widget/utils/src/public-api'

@Component({
  selector: 'viewer-pdf-container',
  templateUrl: './pdf.component.html',
  styleUrls: ['./pdf.component.scss'],
})
export class PdfComponent implements OnInit {
  @Input() isFetchingDataComplete = false
  @Input() pdfData: NsContent.IContent | null = null
  @Input() forPreview = false
  @Input() widgetResolverPdfData: any = {
    widgetType: 'player',
    widgetSubType: 'playerPDF',
    widgetData: {
      pdfUrl: '',
      identifier: '',
      disableTelemetry: false,
      hideControls: true,
    },
  }
  @Input() isPreviewMode = false
  @Input() discussionForumWidget: NsWidgetResolver.IRenderConfigWithTypedData<
    NsDiscussionForum.IDiscussionForumInput
  > | null = null
  isTypeOfCollection = false
  isRestricted = false
  constructor(private activatedRoute: ActivatedRoute, private configSvc: ConfigurationsService) { }

  ngOnInit() {
    if (this.configSvc.restrictedFeatures) {
      this.isRestricted =
        !this.configSvc.restrictedFeatures.has('disscussionForum')
    }
    this.isTypeOfCollection = this.activatedRoute.snapshot.queryParams.collectionType ? true : false
  }
}
