import { Component, OnInit, Input, AfterViewInit, OnDestroy, OnChanges, SimpleChanges } from '@angular/core'
import { SafeResourceUrl, DomSanitizer } from '@angular/platform-browser'
import { fromEvent, Subscription } from 'rxjs'
import { WidgetContentService } from '../_services/widget-content.service'
import { ActivatedRoute } from '@angular/router'
import { EventService } from '@ws-widget/utils'
import { NsContent } from '../_services/widget-content.model'
import { ViewerUtilService } from '../../../../../../project/ws/viewer/src/lib/viewer-util.service'

const pdfPluginPath = '/assets/common/plugins/pdf/web/viewer.html'
@Component({
  selector: 'ws-widget-player-pdf-v2',
  templateUrl: './player-pdf-v2.component.html',
  styleUrls: ['./player-pdf-v2.component.scss'],
})
export class PlayerPdfV2Component implements OnInit, AfterViewInit, OnChanges, OnDestroy {

  @Input() widgetResolverPdfData!: any
  iframeUrl: SafeResourceUrl | null = null
  totalPages = 0
  currentPage = 1
  oldIdentifier = ''
  pageViewed: string[] = []
  pdfContainerSubscription: Subscription | null = null
  pdfDocPageChangeSubscription: Subscription | null = null
  pdfDocLoadSubscription: Subscription | null = null
  pdfHeight = 500
  constructor(
    private domSanitizer: DomSanitizer,
    private contentSvc: WidgetContentService,
    private activatedRoute: ActivatedRoute,
    private eventSvc: EventService,
    private viewerSvc: ViewerUtilService,
  ) { }

  getUrl() {
    this.pageViewed.push(this.currentPage.toString())
    // tslint:disable-next-line: max-line-length
    return `${pdfPluginPath}?file=${encodeURIComponent(this.widgetResolverPdfData.widgetData.pdfUrl)}#zoom=page-width&page=${this.currentPage}`
  }

  ngOnInit() {
  }

  ngAfterViewInit() {
    const pdfContainer = document.getElementById('pdfIframeContainer') as HTMLIFrameElement | null
    if (pdfContainer) {
      this.pdfContainerSubscription = fromEvent(pdfContainer, 'load').subscribe(() => {
        const window = pdfContainer.contentWindow as { [key: string]: any }
        for (const key in window) {
          if (key === 'PDFViewerApplicationOptions') {
            window[key].set('eventBusDispatchToDOM', true)
            window[key].set('externalLinkTarget', 2)
          }
        }
        const pdfDoc = pdfContainer.contentDocument || (pdfContainer.contentWindow && pdfContainer.contentWindow.document)
        if (pdfDoc) {
          this.pdfDocPageChangeSubscription = fromEvent(pdfDoc, 'pagechanging').subscribe((event: any) => {
            const pageNumStr = event.detail.pageNumber.toString()
            if (!this.pageViewed.includes(pageNumStr)) {
              this.pageViewed.push(pageNumStr)
            }
            this.currentPage = event.detail.pageNumber
            this.raiseTelemetry('pageChange', this.widgetResolverPdfData.widgetData.identifier)
          })
          this.pdfDocLoadSubscription = fromEvent(pdfDoc, 'documentloaded').subscribe((_event: any) => {
            this.totalPages = window['PDFViewerApplication'].pagesCount || 0
            const nodeList = window['PDFViewerApplication'].pdfViewer.viewer.childNodes as HTMLElement[]
            this.pdfHeight = nodeList[0].clientHeight
          })
        }
      })
    }
  }

  ngOnChanges(props: SimpleChanges) {
    for (const prop in props) {
      if (prop === 'widgetResolverPdfData') {
        if (this.widgetResolverPdfData.widgetData.identifier !== this.oldIdentifier) {
          if (this.pageViewed.length > 0) {
            this.saveContinueLearning(this.oldIdentifier)
            this.fireRealTimeProgress(this.oldIdentifier)
          }
        }
        this.oldIdentifier = this.widgetResolverPdfData.widgetData.identifier
        this.pageViewed = []
        if (this.widgetResolverPdfData.widgetData.resumePage) {
          this.currentPage = this.widgetResolverPdfData.widgetData.resumePage
        } else {
          this.currentPage = 1
        }
        this.iframeUrl = this.domSanitizer.bypassSecurityTrustResourceUrl(this.getUrl())
      }
    }
  }

  ngOnDestroy() {
    this.saveContinueLearning(this.widgetResolverPdfData.widgetData.identifier)
    this.fireRealTimeProgress(this.widgetResolverPdfData.widgetData.identifier)
    if (this.pdfContainerSubscription) {
      this.pdfContainerSubscription.unsubscribe()
    }
    if (this.pdfDocPageChangeSubscription) {
      this.pdfDocPageChangeSubscription.unsubscribe()
    }
    if (this.pdfDocLoadSubscription) {
      this.pdfDocLoadSubscription.unsubscribe()
    }
  }

  fireRealTimeProgress(id: string) {
    if (this.totalPages > 0 && this.pageViewed.length > 0) {
      const realTimeProgressRequest = {
        content_type: 'Resource',
        mime_type: NsContent.EMimeTypes.PDF,
        user_id_type: 'uuid',
        max_size: this.totalPages,
        current: this.pageViewed,
      }
      this.viewerSvc.realTimeProgressUpdate(id, realTimeProgressRequest)
    }
    return
  }

  raiseTelemetry(action: string, id: string) {
    this.eventSvc.raiseInteractTelemetry(action, 'click', {
      contentId: id,
    })
  }
  saveContinueLearning(id: string) {
    if (this.activatedRoute.snapshot.queryParams.collectionType &&
      this.activatedRoute.snapshot.queryParams.collectionType.toLowerCase() === 'playlist') {
      const reqBody = {
        contextPathId: this.activatedRoute.snapshot.queryParams.collectionId
          ? this.activatedRoute.snapshot.queryParams.collectionId
          : id,
        resourceId: id,
        contextType: 'playlist',
        dateAccessed: Date.now(),
        data: JSON.stringify({
          progress: this.currentPage,
          timestamp: Date.now(),
          contextFullPath: [this.activatedRoute.snapshot.queryParams.collectionId, id],
        }),
      }
      this.contentSvc
        .saveContinueLearning(reqBody)
        .toPromise()
        .catch()
    } else {
      const reqBody = {
        contextPathId: this.activatedRoute.snapshot.queryParams.collectionId
          ? this.activatedRoute.snapshot.queryParams.collectionId
          : id,
        resourceId: id,
        dateAccessed: Date.now(),
        data: JSON.stringify({
          progress: this.currentPage,
          timestamp: Date.now(),
        }),
      }
      this.contentSvc
        .saveContinueLearning(reqBody)
        .toPromise()
        .catch()
    }
  }
}
