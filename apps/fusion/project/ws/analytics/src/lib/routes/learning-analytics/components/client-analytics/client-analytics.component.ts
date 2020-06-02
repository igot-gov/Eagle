import { Component, OnDestroy, OnInit, ViewChild, ElementRef } from '@angular/core'
import { FormBuilder, FormControl, FormGroup } from '@angular/forms'
import { ActivatedRoute, ParamMap, Router } from '@angular/router'
import { GraphGeneralService } from '@ws-widget/collection'
import { ConfigurationsService, NsPage, ValueService, TFetchStatus } from '@ws-widget/utils'
import { Subscription } from 'rxjs'
import { map } from 'rxjs/operators'
import { NsAnalytics } from '../../models/learning-analytics.model'
import { AnalyticsResolver } from '../../resolvers/learning-analytics-filters.resolver'
import { LearningAnalyticsService } from '../../services/learning-analytics.service'
import { MatSnackBar } from '@angular/material'

@Component({
  selector: 'ws-analytics-client-analytics',
  templateUrl: './client-analytics.component.html',
  styleUrls: ['./client-analytics.component.scss'],
})
export class ClientAnalyticsComponent implements OnInit, OnDestroy {
  errorMessageCode: 'API_FAILURE' | 'NO_DATA' | 'INVALID_DATA' | 'NONE' = 'NONE'
  sideNavBarOpened = true
  isMarketingFeature = true
  isLtMedium$ = this.valueSvc.isLtMedium$
  pageNavbar: Partial<NsPage.INavBackground> = this.configSvc.pageNavBar
  private defaultSideNavBarOpenedSubscription: Subscription | null = null
  mode$ = this.isLtMedium$.pipe(map(isMedium => (isMedium ? 'over' : 'side')))
  screenSizeIsLtMedium = false
  currentTab = ''
  maxDate: Date = new Date()
  startDate = '2020-01-01'
  showDateFilter = false
  endDate = `${new Date().getFullYear()}-${`0${new Date().getMonth() + 1}`.slice(-2)}-${`0${new Date().getDate()}`.slice(-2)}`
  tabs = ['home', 'content', 'pathway', 'reports']
  dates = {
    start: '',
    end: '',
    count: 0,
  }
  searchQuery = ''
  currentTabName = ''
  contentType = 'Course'
  contentData: any
  searchForm: FormGroup | null = null
  filterControl = new FormControl()
  showFilter = false
  visible = true
  selectable = true
  removable = true
  addOnBlur = true
  showHome = false
  showContent = false
  showReport = false
  selected = ''
  filterList = [
    {
      name: 'All',
      key: 'search',
    },
    {
      name: 'Dealer Code',
      key: 'dealer_code',
    },
    {
      name: 'Stars Id',
      key: 'stars_id',
    },
    {
      name: 'Content Id',
      key: 'content_id',
    },
  ]
  title = 'Manager\'s Dashboard'
  fetchStatus: TFetchStatus = 'none'
  filterArray: NsAnalytics.IFilterObj[] = []
  filterData: NsAnalytics.IFilter[] = []
  searchControl = new FormControl()
  analytics = this.route.snapshot.data.pageData.data.analytics.subFeatures.learningAnalytics
  private filterEventSubscription: Subscription | null = null
  private filterDataEventSubscription: Subscription | null = null
  private searchEventSubscription: Subscription | null = null
  private defaultSearchSubscription: Subscription | null = null
  paramSubscription: Subscription | null = null
  @ViewChild('searchContent', { static: true })
  searchContentMessage!: ElementRef<any>
  @ViewChild('filterContent', { static: true })
  filterContentMessage!: ElementRef<any>
  @ViewChild('emptyContent', { static: true })
  emptyContentMessage!: ElementRef<any>
  @ViewChild('errorContent', { static: true })
  errorContentMessage!: ElementRef<any>
  @ViewChild('dateContent', { static: true })
  dateContentMessage!: ElementRef<any>
  showPathwayTab = false
  showFilters = false
  constructor(
    private route: ActivatedRoute,
    private valueSvc: ValueService,
    private configSvc: ConfigurationsService,
    private resolver: AnalyticsResolver,
    private form: FormBuilder,
    private graphGeneralSvc: GraphGeneralService,
    private router: Router,
    private snackBar: MatSnackBar,
    private analyticsSvc: LearningAnalyticsService,
  ) {
    this.searchForm = this.form.group({
      filter: new FormControl(null),
      searchControl: new FormControl(null),
      dateStart: new FormControl(null),
      dateEnd: new FormControl(null),
    })
  }
  ngOnInit() {
    this.maxDate.setTime(new Date().getTime() - 86400000)
    this.analyticsSvc.getTabs().subscribe((tabs: any) => {
      this.showPathwayTab = tabs.showPathway
    })
    this.defaultSideNavBarOpenedSubscription = this.isLtMedium$.subscribe((isLtMedium: boolean) => {
      this.sideNavBarOpened = !isLtMedium
      this.screenSizeIsLtMedium = isLtMedium
    })
    const tabName = this.router.url.split('/')[2]
    this.currentTabName = tabName
    this.paramSubscription = this.route.paramMap.subscribe((params: ParamMap) => {
      let tab = params.get('tab')
      if (tab) {
        if (this.tabs.indexOf(tab) === -1) {
          tab = 'home'
        }
        this.currentTab = tab
      }
    })

    this.filterEventSubscription = this.graphGeneralSvc.filterEventChangeSubject.subscribe((filterEvent: any) => {
      const filter: NsAnalytics.IFilter = {
        filterName: filterEvent.filterName,
        filterType: filterEvent.filterType,
      }
      this.add(filter)
      this.showFilter = true

    })
    this.filterDataEventSubscription = this.resolver.dataFilterEventChangeSubject.subscribe((filterEvent: any) => {
      const filter: NsAnalytics.IFilter = {
        filterName: filterEvent.filterName,
        filterType: filterEvent.filterType,
      }
      this.add(filter)
      this.showFilter = true

    })
    this.searchEventSubscription = this.resolver.searchEventChangeSubject.subscribe((searchEvent: any) => {
      const filter: NsAnalytics.IFilter = {
        filterName: searchEvent.searchQuery,
        filterType: 'searchQuery',
      }
      this.add(filter)
      this.showFilter = true
    })
    this.getData(this.endDate, this.startDate, this.contentType, this.searchQuery, this.filterArray)
  }
  submitFilterDetails() {
    if (this.searchForm) {
      if (this.searchForm.controls.dateStart.value !== null && this.searchForm.controls.dateEnd.value !== null) {
        if ((new Date(this.searchForm.controls.dateStart.value).getTime() < new Date(this.searchForm.controls.dateEnd.value).getTime())) {
          // tslint:disable-next-line: max-line-length
          // if ((new Date(this.searchForm.controls.dateEnd.value).getTime() - new Date(this.searchForm.controls.dateStart.value).getTime()) < 1382400000) {
          // tslint:disable-next-line: max-line-length
          this.startDate = `${new Date(this.searchForm.controls.dateStart.value).getFullYear()}-${`0${new Date(this.searchForm.controls.dateStart.value).getMonth() + 1}`.slice(-2)}-${`0${new Date(this.searchForm.controls.dateStart.value).getDate()}`.slice(-2)}`
          // tslint:disable-next-line: max-line-length
          this.endDate = `${new Date(this.searchForm.controls.dateEnd.value).getFullYear()}-${`0${new Date(this.searchForm.controls.dateEnd.value).getMonth() + 1}`.slice(-2)}-${`0${new Date(this.searchForm.controls.dateEnd.value).getDate()}`.slice(-2)}`
          this.showDateFilter = true
          this.resolver.setDateFilterEvent({ startDate: this.startDate, endDate: this.endDate })
          // }
          // else {
          //   this.snackBar.open(this.errorContentMessage.nativeElement.value)
          // }
        } else {
          this.snackBar.open(this.dateContentMessage.nativeElement.value)
        }
      }
      if (this.searchForm.controls.searchControl.value !== null && this.searchForm.controls.filter.value !== null) {
        if (this.searchForm.controls.filter.value.key === 'search') {
          this.resolver.setSearchFilterEvent({ searchQuery: this.searchForm.controls.searchControl.value })
        } else {
          const filter: NsAnalytics.IFilter = {
            filterName: this.searchForm.controls.searchControl.value,
            filterType: this.searchForm.controls.filter.value.key,
          }
          this.resolver.dataFilterEvent(filter)
        }
      }
    }
    if (this.searchForm) {
      this.searchForm.controls.dateStart.setValue(null)
      this.searchForm.controls.dateEnd.setValue(null)
      this.searchForm.controls.searchControl.setValue(null)
      this.searchForm.controls.filter.setValue(null)
    }
    this.closeFilters()
  }
  onPress(event: any) {
    if (event.keyCode === 13) {
      this.submitFilterDetails()
    }
  }
  applyFilters() {
    this.showFilters = true
  }

  closeFilters() {
    this.showFilters = false
  }
  search(search: string) {
    this.resolver.setSearchFilterEvent({ searchQuery: search })
  }

  add(filter: NsAnalytics.IFilter): void {
    if (this.filterData.findIndex(v => v.filterName.toLowerCase() === filter.filterName.toLowerCase()) === -1) {
      this.filterData.push(filter)
    }
  }
  remove(filter: NsAnalytics.IFilter): void {
    if (this.filterData.length === 0) {
      this.showFilter = false
    } else {
      const index = this.filterData.indexOf(filter)
      if (index >= 0) {
        this.filterData.splice(index, 1)
      }
      this.resolver.removeFilterEvent(this.filterData)
    }
  }
  ngOnDestroy() {
    if (this.defaultSideNavBarOpenedSubscription) {
      this.defaultSideNavBarOpenedSubscription.unsubscribe()
    }
    if (this.defaultSearchSubscription) {
      this.defaultSearchSubscription.unsubscribe()
    }
    if (this.filterEventSubscription) {
      this.filterEventSubscription.unsubscribe()
    }

    if (this.filterDataEventSubscription) {
      this.filterDataEventSubscription.unsubscribe()
    }

    if (this.searchEventSubscription) {
      this.searchEventSubscription.unsubscribe()
    }
  }

  sideNavOnClick(tab: string) {
    this.currentTabName = tab
    if (this.screenSizeIsLtMedium) {
      this.sideNavBarOpened = !this.sideNavBarOpened
    }
    this.showFilter = false
  }
  toggleInput() {
    this.startDate = '2020-01-01'
    this.endDate = `${new Date().getFullYear()}-${`0${new Date().getMonth() + 1}`.slice(-2)}-${`0${new Date().getDate()}`.slice(-2)}`
    if (this.searchForm) {
      this.searchForm.controls.dateStart.setValue(null)
      this.searchForm.controls.dateEnd.setValue(null)
    }
    this.showDateFilter = false
    this.resolver.setDateFilterEvent({ startDate: this.startDate, endDate: this.endDate })
  }

  callFilteredGet(event: string) {
    const date = JSON.parse(event)
    this.dates.start = date.from.replace(/' '/g, '')
    this.dates.end = date.to.replace(/' '/g, '')
    this.dates.count = date.count
    this.endDate = this.dates.end
    this.startDate = this.dates.start
    this.resolver.setDateFilterEvent({ startDate: this.startDate, endDate: this.endDate })
    this.getData(this.endDate, this.startDate, this.contentType, this.searchQuery, this.filterArray)
  }
  getData(endDate: string, startDate: string, contentType: string, searchQuery: string, filterArray: NsAnalytics.IFilterObj[]) {
    this.fetchStatus = 'fetching'
    this.analyticsSvc.timeSpent(endDate, startDate, contentType, filterArray, searchQuery).subscribe(
      (timeSpentData: any) => {
        this.contentData = timeSpentData
        this.fetchStatus = 'done'
      },
      // tslint:disable-next-line:align
      () => {
        this.contentData = null
        this.fetchStatus = 'error'
      },
    )
  }
}
