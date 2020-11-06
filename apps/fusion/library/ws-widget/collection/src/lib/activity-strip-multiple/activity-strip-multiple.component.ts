import { Component, OnInit, Input, OnDestroy, HostBinding, ViewChild, TemplateRef } from '@angular/core'
import { NsWidgetResolver, WidgetBaseComponent } from '@ws-widget/resolver'
import { NsNetworkStripNewMultiple } from './activity-strip-multiple.model'
import { ActivityStripNewMultipleService } from './activity-strip-multiple.service'
import { WidgetContentService } from '../_services/widget-content.service'
import { NsContent } from '../_services/widget-content.model'
import { saveAs } from 'file-saver'
import {
  TFetchStatus,
  LoggerService,
  // EventService,
  // ConfigurationsService,
  UtilityService,
} from '@ws-widget/utils'
import { Subscription } from 'rxjs'
import { MatSnackBar } from '@angular/material'
// import { filter } from 'rxjs/operators'
// import { SearchServService } from '@ws/app/src/lib/routes/search/services/search-serv.service'

interface IStripUnitContentData {
  key: string
  canHideStrip: boolean
  mode?: string
  showStrip: boolean
  widgets?: NsWidgetResolver.IRenderConfigWithAnyData[]
  stripTitle: string
  stripName?: string
  stripInfo?: NsNetworkStripNewMultiple.IStripInfo
  noDataWidget?: NsWidgetResolver.IRenderConfigWithAnyData
  errorWidget?: NsWidgetResolver.IRenderConfigWithAnyData
  showOnNoData: boolean
  showOnLoader: boolean
  showOnError: boolean
  stripBackground?: string
  viewMoreUrl: {
    path: string
    queryParams: any
  } | null
}
@Component({
  selector: 'ws-widget-activity-strip-multiple',
  templateUrl: './activity-strip-multiple.component.html',
  styleUrls: ['./activity-strip-multiple.component.scss'],
})
export class ActivityStripMultipleComponent extends WidgetBaseComponent
  implements
  OnInit,
  OnDestroy,
  NsWidgetResolver.IWidgetData<NsNetworkStripNewMultiple.INetworkStripMultiple> {
  @Input() widgetData!: NsNetworkStripNewMultiple.INetworkStripMultiple
  @HostBinding('id')
  public id = `activity-multiple_${Math.random()}`
  @ViewChild('userManual', { static: true }) userManual!: TemplateRef<any>
  stripsResultDataMap: { [key: string]: IStripUnitContentData } = {}
  stripsKeyOrder: string[] = []
  showAccordionData = true
  showParentLoader = false
  showParentError = false
  showParentNoData = false
  errorDataCount = 0
  noDataCount = 0
  successDataCount = 0
  searchArray = ['preview', 'channel', 'author']
  contentAvailable = true
  isFromAuthoring = false
  tempItem: any

  changeEventSubscription: Subscription | null = null

  constructor(
    private contentStripSvc: ActivityStripNewMultipleService,
    private contentSvc: WidgetContentService,
    private loggerSvc: LoggerService,
    // private eventSvc: EventService,
    // private configSvc: ConfigurationsService,
    private snackBar: MatSnackBar,
    protected utilitySvc: UtilityService,
    // private searchServSvc: SearchServService,
  ) {
    super()
  }

  ngOnInit() {
    this.openUserManualDialogue()
    this.initData()
  }

  ngOnDestroy() {
    if (this.changeEventSubscription) {
      this.changeEventSubscription.unsubscribe()
    }
  }
  download() {
    const filename = 'Igot User Manual.pdf'
    const serverFilename = 'manual.pdf'
    this.closeSnackBar()
    const oReq = new XMLHttpRequest()
    // The Endpoint of your server
    const uRLToPdf = `/assets/common/user-manual/${serverFilename}`

    // Configure XMLHttpRequest
    oReq.open('GET', uRLToPdf, true)

    // Important to use the blob response type
    oReq.responseType = 'blob'

    // When the file request finishes
    // Is up to you, the configuration for error events etc.
    oReq.onload = function () {
      // Once the file is downloaded, open a new window with the PDF
      // Remember to allow the POP-UPS in your browser
      const file = new Blob([oReq.response], {
        type: 'application/pdf',
      })
      // Generate file download directly in the browser !
      saveAs(file, filename)
    }

    oReq.send()
  }
  closeSnackBar() {
    this.snackBar.dismiss()
  }
  openUserManualDialogue() {
    this.snackBar.openFromTemplate(this.userManual, { duration: 20000, verticalPosition: 'bottom', horizontalPosition: 'left' })
  }

  private initData() {
    this.stripsKeyOrder = this.widgetData.strips.map(strip => strip.key) || []
    if (this.widgetData.loader && this.widgetData.strips.length) {
      this.showParentLoader = true
    }
    // Fetch the data
    for (const strip of this.widgetData.strips) {
      if (this.checkForEmptyWidget(strip)) {
        this.fetchStripFromRequestData(strip)
      } else {
        this.processStrip(strip, [], 'done', true, null)
      }
    }
  }

  // private fetchStripFromKey(key: string, calculateParentStatus = true) {
  //   const stripData = this.widgetData.strips.find(strip => strip.key === key)
  //   if (stripData) {
  //     this.fetchStripFromRequestData(stripData, calculateParentStatus)
  //   }
  // }

  private fetchStripFromRequestData(
    strip: NsNetworkStripNewMultiple.INetworkStripUnit,
    calculateParentStatus = true,
  ) {
    this.processStrip(strip, [], 'fetching', false, null)
    this.fetchNetworkUsers(strip, calculateParentStatus)
  }
  fetchNetworkUsers(strip: NsNetworkStripNewMultiple.INetworkStripUnit, calculateParentStatus: boolean) {
    // this.tempItem = [{ totalDuration: { unit: 'HOUR', value: 4064 } },
    // { karmaPoints: { unit: 'NUMBER', value: 0 } },
    // { dailyTimeSpent: { unit: 'MINUTE', value: 0 } },
    // { coins: { unit: 'NUMBER', value: 0 } },
    // { certificateCount: { unit: 'NUMBER', value: 1 } },
    // { contentCount: { unit: 'NUMBER', value: 70 } }]
    if (strip.request && strip.request.api && Object.keys(strip.request.api).length) {
      this.contentStripSvc.fetchNetworkUsers(strip.request.api.queryParams, strip.request.api.path).subscribe(
        _results => {
          this.processStrip(
            strip,
            this.transformContentsToWidgets(_results, strip),
            'done',
            calculateParentStatus,
            null,
          )
        }
      )
    }
  }

  private transformContentsToWidgets(
    contents: NsContent.IContent[],
    strip: NsNetworkStripNewMultiple.INetworkStripUnit,
  ) {
    return (contents || []).map((content, idx) => ({
      widgetType: 'card',
      widgetSubType: 'cardActivity',
      widgetHostClass: 'mb-2',
      widgetData: {
        content,
        cardSubType: strip.stripConfig && strip.stripConfig.cardSubType,
        context: { pageSection: strip.key, position: idx },
        intranetMode: strip.stripConfig && strip.stripConfig.intranetMode,
        deletedMode: strip.stripConfig && strip.stripConfig.deletedMode,
        // contentTags: strip.stripConfig && strip.stripConfig.contentTags,
      },
    }))
  }

  showAccordion(key: string) {
    if (this.utilitySvc.isMobile && this.stripsResultDataMap[key].mode === 'accordion') {
      return this.showAccordionData
    }
    return true
  }

  setHiddenForStrip(key: string) {
    this.stripsResultDataMap[key].showStrip = false
    sessionStorage.setItem(`cstrip_${key}`, '1')
  }
  private getIfStripHidden(key: string): boolean {
    const storageItem = sessionStorage.getItem(`cstrip_${key}`)
    return Boolean(storageItem !== '1')
  }

  private async processStrip(
    strip: NsNetworkStripNewMultiple.INetworkStripUnit,
    results: NsWidgetResolver.IRenderConfigWithAnyData[] = [],
    fetchStatus: TFetchStatus,
    calculateParentStatus = true,
    viewMoreUrl: any,
    // calculateParentStatus is used so that parents' status is not re-calculated if the API is called again coz of filters, etc.
  ) {
    // this.stripsResultDataMap[strip.key]
    if (results.length && strip.fetchLikes) {
      await this.processContentLikes(results)
    }
    const stripData = {
      viewMoreUrl,
      key: strip.key,
      canHideStrip: Boolean(strip.canHideStrip),
      showStrip: this.getIfStripHidden(strip.key),
      noDataWidget: strip.noDataWidget,
      errorWidget: strip.errorWidget,
      stripInfo: strip.info,
      stripTitle: strip.title,
      description: strip.titleDescription,
      stripLogo: strip.logo,

      stripName: strip.name,
      mode: strip.mode,
      stripBackground: strip.stripBackground,
      widgets:
        fetchStatus === 'done'
          ? [
            ...(strip.preWidgets || []).map(w => ({
              ...w,
              widgetHostClass: `mb-2 ${w.widgetHostClass}`,
            })),
            ...results,
            ...(strip.postWidgets || []).map(w => ({
              ...w,
              widgetHostClass: `mb-2 ${w.widgetHostClass}`,
            })),
          ]
          : [],
      showOnNoData: Boolean(
        strip.noDataWidget &&
        !((strip.preWidgets || []).length + results.length + (strip.postWidgets || []).length) &&
        fetchStatus === 'done',
      ),
      showOnLoader: Boolean(strip.loader && fetchStatus === 'fetching'),
      showOnError: Boolean(strip.errorWidget && fetchStatus === 'error'),
    }
    // const stripData = this.stripsResultDataMap[strip.key]
    this.stripsResultDataMap = {
      ...this.stripsResultDataMap,
      [strip.key]: stripData,
    }
    if (
      calculateParentStatus &&
      (fetchStatus === 'done' || fetchStatus === 'error') &&
      stripData.widgets
    ) {
      this.checkParentStatus(fetchStatus, stripData.widgets.length)
    }
    if (calculateParentStatus && !(results && results.length > 0)) {
      this.contentAvailable = false
    } else if (results && results.length > 0) {
      this.contentAvailable = true
    }
  }
  private checkParentStatus(fetchStatus: TFetchStatus, stripWidgetsCount: number): void {
    if (fetchStatus === 'done' && !stripWidgetsCount) {
      this.noDataCount += 1
    } else if (fetchStatus === 'done' && stripWidgetsCount) {
      this.successDataCount += 1
    } else if (fetchStatus === 'error') {
      this.errorDataCount += 1
    }
    const settledCount = this.noDataCount + this.successDataCount + this.errorDataCount
    const totalCount = this.widgetData.strips.length
    if (this.successDataCount > 0 && settledCount < totalCount) {
      return
    }
    this.showParentLoader = settledCount !== totalCount
    this.showParentNoData =
      this.noDataCount > 0 && this.noDataCount + this.errorDataCount === totalCount
    this.showParentError = this.errorDataCount === totalCount
  }

  toggleInfo(data: IStripUnitContentData) {
    const stripInfo = this.stripsResultDataMap[data.key].stripInfo
    if (stripInfo) {
      if (stripInfo.mode !== 'below') {
        this.loggerSvc.warn(`strip info mode: ${stripInfo.mode} not implemented yet`)
        stripInfo.mode = 'below'
      }
      if (stripInfo.mode === 'below') {
        this.stripsResultDataMap[data.key].stripInfo = {
          ...stripInfo,
          visibilityMode: stripInfo.visibilityMode === 'hidden' ? 'visible' : 'hidden',
        }
      }
    }
  }

  checkForEmptyWidget(strip: NsNetworkStripNewMultiple.INetworkStripUnit): boolean {
    if (
      strip.request &&
      (strip.request.api && Object.keys(strip.request.api).length
      )) {
      return true
    }
    return false
  }

  processContentLikes(results: NsWidgetResolver.IRenderConfigWithAnyData[]): Promise<any> {
    const contentIds = {
      content_id:
        results.map(result => result.widgetData && result.widgetData.content.identifier) || [],
    }
    return this.contentSvc
      .fetchContentLikes(contentIds)
      .then(likeHash => {
        const likes = likeHash
        results.forEach(result => {
          result.widgetData.likes = likes[result.widgetData.content.identifier] || 0
        })
      })
      .catch(_err => { })
      .finally(() => Promise.resolve())
  }
}
