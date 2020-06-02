import { Component, OnInit, Input, OnDestroy, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core'
import { ValueService, ConfigurationsService } from '@ws-widget/utils'
import { Subscription } from 'rxjs'
import { NSSearch } from '../../_services/widget-search.model'
import { SearchApiService } from '@ws/app/src/lib/routes/search/apis/search-api.service'
import { SearchServService } from '@ws/app/src/lib/routes/search/services/search-serv.service'
import { IWidgetData, IAppliedFilters } from './content-picker-v2.model'
import { NsContent } from '../../_services/widget-content.model'
import { ContentPickerV2Service } from './content-picker-v2.service'

@Component({
  selector: 'ws-widget-content-picker-v2',
  templateUrl: './content-picker-v2.component.html',
  styleUrls: ['./content-picker-v2.component.scss'],
})
export class ContentPickerV2Component implements OnInit, OnDestroy, OnChanges {

  @Input()
  widgetData!: IWidgetData
  @Output()
  change = new EventEmitter<{
    checked: boolean
    content: Partial<NsContent.IContent>
  }>()
  @Output()
  searchQueryEvent = new EventEmitter<NSSearch.ISearchV6Request>()
  isLtMedium = false
  filtersExpanded = false
  isLtMediumSubscription: Subscription | null = null
  triggerSearchSubscription: Subscription | null = null
  searchReq: NSSearch.ISearchV6Request
  searchResults!: NSSearch.ISearchV6ApiResult
  defaultThumbnail = ''
  fetchStatus: 'none' | 'fetching' | 'done' | 'error'
  searchConfig: any = null
  objKey = Object.keys

  constructor(
    private valueSvc: ValueService,
    private searchApiSvc: SearchApiService,
    private configSvc: ConfigurationsService,
    private contentPickerSvc: ContentPickerV2Service,
    private searchServSvc: SearchServService,
  ) {
    this.fetchStatus = 'none'
    const instanceConfig = this.configSvc.instanceConfig
    if (instanceConfig) {
      this.defaultThumbnail = instanceConfig.logos.defaultContent
    }
    this.searchReq = {
      query: '',
      didYouMean: false,
      locale: [this.searchServSvc.getLanguageSearchIndex(
        this.configSvc.activeLocale && this.configSvc.activeLocale.locals[0] || 'en'
      )],
      visibleFilters: {},
    }
  }

  initSearchResults() {
    this.searchResults = {
      filters: [],
      filtersUsed: [],
      notVisibleFilters: [],
      result: [],
      totalHits: 0,
    }
  }

  changeSelectedContent(checked: boolean, content: NsContent.IContent) {
    this.change.emit({ checked, content })
  }

  appliedFilters(filters: IAppliedFilters) {
    this.searchReq.filters = [{
      andFilters: Object.keys(filters).map(key => {
        return {
          [key]: Array.from(filters[key]).map(val => val),
        }
      }),
    }]
    this.triggerSearch()
  }

  searchRequest(req: { lang: string, query: string }) {
    this.searchReq.locale = [req.lang]
    this.searchReq.query = req.query
    this.triggerSearch()
  }

  sort(key: string) {
    if (this.widgetData.sortableFields) {
      if (key === 'relevance') {
        this.searchReq.sort = undefined
      } else {
        this.searchReq.sort = [{ [key]: this.widgetData.sortableFields[key].order }]
      }
      this.triggerSearch()
    }
  }

  async triggerSearch() {
    if (!this.searchConfig) {
      this.searchConfig = {}
      try {
        this.searchConfig = await this.contentPickerSvc.getData(`${this.configSvc.sitePath}/feature/search.json`).toPromise()
      } catch (_err) { }
    }
    const isStandAlone = this.searchConfig.search.tabs[0].isStandAlone
    let applyIsStandAlone = false
    if (isStandAlone || isStandAlone === undefined) {
      applyIsStandAlone = true
    }
    this.searchReq.isStandAlone = applyIsStandAlone ? applyIsStandAlone : undefined
    this.searchQueryEvent.emit(this.searchReq)
    this.fetchStatus = 'fetching'
    this.initSearchResults()
    this.triggerSearchSubscription = this.searchApiSvc.getSearchV6Results(this.searchReq).subscribe(
      results => {
        this.fetchStatus = 'done'
        this.searchResults = JSON.parse(JSON.stringify(results))
      },
      _err => {
        this.fetchStatus = 'error'
      })
  }

  ngOnInit() {
    this.isLtMediumSubscription = this.valueSvc.isLtMedium$.subscribe(isLtMedium => {
      this.isLtMedium = isLtMedium
    })
  }
  ngOnDestroy() {
    if (this.isLtMediumSubscription) {
      this.isLtMediumSubscription.unsubscribe()
    }
    if (this.triggerSearchSubscription) {
      this.triggerSearchSubscription.unsubscribe()
    }
  }

  ngOnChanges(props: SimpleChanges) {
    for (const prop in props) {
      if (prop === 'widgetData') {
        if (this.widgetData && this.widgetData.includedFilters && this.widgetData.includedFilters.hasOwnProperty('contentType') &&
          this.searchReq.hasOwnProperty('visibleFilters') && this.searchReq.visibleFilters) {
          this.searchReq.visibleFilters['contentType'] = {
            scope: 'semi_global',
            filters: [{
              andFilters: [{
                ['contentType']: this.widgetData.includedFilters['contentType'].values,
              }],
            }],
            displayName: this.widgetData.includedFilters['contentType'].displayName,
          }
        }
        if (this.widgetData && this.widgetData.includedFilters && Object.keys(this.widgetData.includedFilters || {}).length) {
          Object.keys(this.widgetData.includedFilters).forEach(key => {
            if (key !== 'contentType' && this.searchReq.hasOwnProperty('visibleFilters') && this.searchReq.visibleFilters &&
              this.widgetData && this.widgetData.includedFilters) {
              this.searchReq.visibleFilters[key] = this.widgetData.includedFilters[key]
            }
          })
        }
        this.initSearchResults()
      }
    }
  }

}
