import { Component, OnInit, OnDestroy } from '@angular/core'
import { NsEmbeddedPage } from '../../../../../../../../../library/ws-widget/collection/src/lib/embedded-page/embedded-page.model'
import { NsWidgetResolver } from '@ws-widget/resolver'
import { ROOT_WIDGET_CONFIG } from '@ws-widget/collection'
import { SubapplicationRespondService } from '@ws-widget/utils'
import { fromEvent } from 'rxjs'
import { filter } from 'rxjs/operators'

@Component({
  selector: 'ws-analytics-iframe-analytics',
  templateUrl: './iframe-analytics.component.html',
  styleUrls: ['./iframe-analytics.component.scss'],
  providers: [SubapplicationRespondService],
})
export class IframeAnalyticsComponent implements OnInit, OnDestroy {
  responseSubscription: any
  analyticsData: NsWidgetResolver.IRenderConfigWithTypedData<NsEmbeddedPage.IEmbeddedPage> | null = null
  constructor(private respondSvc: SubapplicationRespondService,
) { }

  async ngOnInit() {
    this.responseSubscription = fromEvent<MessageEvent>(window, 'message')
      .pipe(
        filter(
          (event: MessageEvent) =>
            Boolean(event) &&
            Boolean(event.data) &&
            Boolean(event.source && typeof event.source.postMessage === 'function'),
        ),
      )
      .subscribe(async (event: MessageEvent) => {
        const contentWindow = event.source as Window
        if (event.data.requestId) {
          switch (event.data.requestId) {
            case 'LOADED':
              this.respondSvc.loadedRespond(contentWindow, event.data.subApplicationName)
              break
            default:
              break
          }
        }
      })
    this.analyticsData = {
      widgetType: ROOT_WIDGET_CONFIG.page._type,
      widgetSubType: ROOT_WIDGET_CONFIG.page.embedded,
      widgetData: {
        title: 'Learning Analytics',
        iframeSrc: '/apis/proxies/v8/LA/',
      },
    }
  }
  ngOnDestroy() {
    if (this.responseSubscription) {
      this.responseSubscription.unsubscribe()
    }
  }
}
