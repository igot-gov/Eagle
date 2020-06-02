import { Component, OnInit } from '@angular/core'
import { ActivatedRoute, Router } from '@angular/router'
import { ChannelService } from '../../services/channel.service'
import { TFetchStatus, ConfigurationsService, NsPage } from '@ws-widget/utils'
import { ROOT_WIDGET_CONFIG, IGraphWidget, NsError, colorPalettes } from '@ws-widget/collection'
import { NsWidgetResolver } from '@ws-widget/resolver'
import { NsChannelAnalytics } from './models/channel.model'
@Component({
  selector: 'ws-analytics-channel',
  templateUrl: './channel.component.html',
  styleUrls: ['./channel.component.scss'],
})
export class ChannelComponent implements OnInit {
  channelId = ''
  channelAnalyticsData: NsChannelAnalytics.IChannelAnalyticsData | null = null
  fetchStatus: TFetchStatus = 'none'
  uniqueUsers = 0
  uniqueUsersDescription = 'How many unique users have visited the contents of this channel?'
  hits = 0
  dailyDate = ''
  hitsDescription = 'How many views were recorded for the contents of this channel? A view is recorded every time a user accesses a content'
  avgTimeSpent = 0.0
  timeSpentDescription = 'How much time was spent per user on the contents of this page in average in minutes?'
  isExpandTrue = false
  type = ''
  months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
  apiLinkAccess = this.route.snapshot.data.pageData.data.analytics.subFeatures.courseAnalytics.apiLink
  barChartDeptData: IGraphWidget = {} as IGraphWidget
  barChartDailyUsersData: IGraphWidget = {} as IGraphWidget
  barChartDailyHitsData: IGraphWidget = {} as IGraphWidget
  barChartCountryData: IGraphWidget = {} as IGraphWidget
  pieChartDeviceData: IGraphWidget = {} as IGraphWidget
  pieChartDeviceHitsData: IGraphWidget = {} as IGraphWidget
  barChartDeptHitsData: IGraphWidget = {} as IGraphWidget
  barChartCountryHitsData: IGraphWidget = {} as IGraphWidget
  barChartExpandClientData: IGraphWidget = {} as IGraphWidget
  pageNavbar: Partial<NsPage.INavBackground> = this.configSvc.pageNavBar
  errorWidget: NsWidgetResolver.IRenderConfigWithTypedData<NsError.IWidgetErrorResolver> = {
    widgetType: ROOT_WIDGET_CONFIG.errorResolver._type,
    widgetSubType: ROOT_WIDGET_CONFIG.errorResolver.errorResolver,
    widgetData: {
      errorType: 'internalServer',
    },
  }
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private channelSrv: ChannelService,
    private configSvc: ConfigurationsService,
  ) { }

  ngOnInit() {
    this.channelId = this.route.snapshot.params.id
    this.type = this.router.url.split('/')[2]
    this.fetchStatus = 'fetching'
    this.channelSrv.getContentAnalyticsClient(this.channelId, this.type).subscribe(
      (channelData: NsChannelAnalytics.IChannelAnalyticsData) => {
        this.channelAnalyticsData = channelData
        this.chartData()
        this.fetchStatus = 'done'
      },
      () => {
        this.fetchStatus = 'error'
      })
  }
  onClick(event: string) {
    const departments = document.getElementById('departments')
    const countries = document.getElementById('countries')
    const devices = document.getElementById('devices')
    if (departments && event === 'departments') {
      departments.scrollIntoView({
        behavior: 'smooth',
        block: 'start',
      })
    }
    if (countries && event === 'countries') {
      countries.scrollIntoView({
        behavior: 'smooth',
        block: 'start',
      })
    }
    if (devices && event === 'devices') {
      devices.scrollIntoView({
        behavior: 'smooth',
        block: 'start',
      })
    }
  }
  chartData() {
    if (this.channelAnalyticsData) {
      // unique users count
      this.uniqueUsers = this.channelAnalyticsData.userCount
      this.hits = this.channelAnalyticsData.hits
      this.avgTimeSpent = parseFloat((this.channelAnalyticsData.avg_time_spent / 60).toFixed(1))
      // users by Dept BarChart Data
      const barChartDeptLabel: string[] = []
      const deptData: number[] = []
      this.channelAnalyticsData.department.forEach((dept: any) => {
        if (barChartDeptLabel.length < 20) {
          barChartDeptLabel.push(dept.key)
        }
        if (deptData.length < 20) {
          deptData.push(dept.doc_count)
        }
      })
      this.barChartDeptData = {
        widgetType: ROOT_WIDGET_CONFIG.graph._type,
        widgetSubType: ROOT_WIDGET_CONFIG.graph.graphGeneral,
        widgetData: {
          graphId: 'deptChart',
          graphType: 'horizontalBar',
          graphHeight: '300px',
          graphWidth: '100%',
          graphLegend: false,
          graphLegendPosition: 'top',
          graphLegendFontSize: 11,
          graphTicksFontSize: 11,
          graphGridLinesDisplay: false,
          graphTicksXAxisDisplay: false,
          graphTicksYAxisDisplay: true,
          graphDefaultPalette: 'default',
          graphData: {
            labels: barChartDeptLabel,
            datasets: [
              {
                label: '',
                // tslint:disable-next-line:max-line-length
                backgroundColor: ['rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)'],
                borderWidth: 1,
                data: deptData,
              },
            ],
          },
        },
      }

      // users by Country BarChart Data
      const barChartCountryLabel: string[] = []
      const countryData: number[] = []
      this.channelAnalyticsData.country.forEach((country: NsChannelAnalytics.IChartData) => {
        if (barChartCountryLabel.length < 20) {
          barChartCountryLabel.push(country.key)
        }
        if (countryData.length < 20) {
          countryData.push(country.doc_count)
        }
      })
      this.barChartCountryData = {
        widgetType: ROOT_WIDGET_CONFIG.graph._type,
        widgetSubType: ROOT_WIDGET_CONFIG.graph.graphGeneral,
        widgetData: {
          graphId: 'countryChart',
          graphType: 'horizontalBar',
          graphHeight: '300px',
          graphWidth: '100%',
          graphLegend: false,
          graphLegendPosition: 'top',
          graphLegendFontSize: 11,
          graphTicksFontSize: 11,
          graphTicksXAxisDisplay: false,
          graphTicksYAxisDisplay: true,
          graphGridLinesDisplay: false,
          graphDefaultPalette: 'default',
          graphData: {
            labels: barChartCountryLabel,
            datasets: [
              {
                label: '',
                // tslint:disable-next-line:max-line-length
                backgroundColor: ['rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)'],
                borderWidth: 1,
                data: countryData,
              },
            ],
          },
        },
      }

      // daily users BarChart Data
      const barChartDailyUsersLabel: string[] = []
      const dailyUsersData: number[] = []
      this.channelAnalyticsData.day_wise_users.forEach((users: NsChannelAnalytics.IBarChartData) => {
        this.dailyDate = users.key_as_string
        this.dailyDate = this.dailyDate.split('T')[0]
        const month = this.months[new Date(this.dailyDate).getMonth()]
        const date = this.dailyDate.split('-')[2]
        this.dailyDate = `${date}-${month}`
        barChartDailyUsersLabel.push(this.dailyDate)
        dailyUsersData.push(users.doc_count)
      })
      this.barChartDailyUsersData = {
        widgetType: ROOT_WIDGET_CONFIG.graph._type,
        widgetSubType: ROOT_WIDGET_CONFIG.graph.graphGeneral,
        widgetData: {
          graphId: 'dailyUsersChart',
          graphType: 'bar',
          graphHeight: '250px',
          graphWidth: '100%',
          graphLegend: false,
          graphLegendPosition: 'top',
          graphLegendFontSize: 11,
          graphTicksFontSize: 11,
          graphTicksXAxisDisplay: true,
          graphTicksYAxisDisplay: true,
          graphGridLinesDisplay: false,
          graphXAxisMaxLimit: 20,
          graphIsXAxisAutoSkip: true,
          graphDefaultPalette: 'default',
          graphData: {
            labels: barChartDailyUsersLabel,
            datasets: [
              {
                label: '',
                // tslint:disable-next-line:max-line-length
                backgroundColor: 'rgb(219, 219, 141)' ,
                borderWidth: 1,
                data: dailyUsersData,
              },
            ],
          },
        },
      }

      // daily hits BarChart Data
      const barChartDailyHitsLabel: string[] = []
      const dailyHitsData: number[] = []
      this.channelAnalyticsData.day_wise_users.forEach((hits: NsChannelAnalytics.IBarChartData) => {
        this.dailyDate = hits.key_as_string
        this.dailyDate = this.dailyDate.split('T')[0]
        const month = this.months[new Date(this.dailyDate).getMonth()]
        const date = this.dailyDate.split('-')[2]
        this.dailyDate = `${date}-${month}`
        barChartDailyHitsLabel.push(this.dailyDate)
        dailyHitsData.push(hits.hits_count)
      })
      this.barChartDailyHitsData = {
        widgetType: ROOT_WIDGET_CONFIG.graph._type,
        widgetSubType: ROOT_WIDGET_CONFIG.graph.graphGeneral,
        widgetData: {
          graphId: 'dailyHitsChart',
          graphType: 'bar',
          graphHeight: '250px',
          graphWidth: '100%',
          graphLegend: false,
          graphLegendPosition: 'top',
          graphLegendFontSize: 11,
          graphTicksFontSize: 11,
          graphTicksXAxisDisplay: true,
          graphTicksYAxisDisplay: true,
          graphGridLinesDisplay: false,
          graphXAxisMaxLimit: 20,
          graphIsXAxisAutoSkip: true,
          graphDefaultPalette: 'default',
          graphData: {
            labels: barChartDailyHitsLabel,
            datasets: [
              {
                label: '',
                // tslint:disable-next-line:max-line-length
                backgroundColor: 'rgb(219, 219, 141)' ,
                borderWidth: 1,
                data: dailyHitsData,
              },
            ],
          },
        },
      }

      // users by device Data
      const deviceData: number[] = []
      const deviceLabel: string[] = []
      this.channelAnalyticsData.device.forEach(
        (device: NsChannelAnalytics.IChartData) => {
          if (deviceLabel.length < 20) {
            deviceLabel.push(device.key)
          }
          if (deviceData.length < 20) {
            deviceData.push(device.doc_count)
          }
          this.pieChartDeviceData = {
            widgetType: ROOT_WIDGET_CONFIG.graph._type,
            widgetSubType: ROOT_WIDGET_CONFIG.graph.graphGeneral,
            widgetData: {
              graphId: 'deviceChart',
              graphType: 'pie',
              graphHeight: '250px',
              graphWidth: '90%',
              graphLegend: true,
              graphLegendPosition: 'left',
              graphLegendFontSize: 11,
              graphTicksFontSize: 11,
              graphGridLinesDisplay: false,
              graphDefaultPalette: 'default',
              graphData: {
                labels: deviceLabel,
                datasets: [
                  {
                    label: '',
                    data: deviceData,
                    // tslint:disable-next-line:max-line-length
                    backgroundColor: colorPalettes.default,
                    borderWidth: 1,
                  },
                ],
              },
            },
          }
        },
      )

      // hits by device Data
      const deviceHitsData: number[] = []
      const deviceHitsLabel: string[] = []
      this.channelAnalyticsData.device = this.channelAnalyticsData.device.sort((a: any, b: any) =>
        a.total_hits > b.total_hits ? -1 : a.total_hits < b.total_hits ? 1 : 0,
      )
      this.channelAnalyticsData.device.forEach(
        (device: NsChannelAnalytics.IChartData) => {
          if (deviceHitsLabel.length < 20) {
            deviceHitsLabel.push(device.key)
          }
          if (deviceHitsData.length < 20) {
            deviceHitsData.push(device.total_hits)
          }
          this.pieChartDeviceHitsData = {
            widgetType: ROOT_WIDGET_CONFIG.graph._type,
            widgetSubType: ROOT_WIDGET_CONFIG.graph.graphGeneral,
            widgetData: {
              graphId: 'deviceHitsChart',
              graphType: 'pie',
              graphHeight: '250px',
              graphWidth: '90%',
              graphLegend: true,
              graphLegendPosition: 'left',
              graphLegendFontSize: 11,
              graphTicksFontSize: 11,
              graphGridLinesDisplay: false,
              graphDefaultPalette: 'default',
              graphData: {
                labels: deviceHitsLabel,
                datasets: [
                  {
                    label: '',
                    data: deviceHitsData,
                    // tslint:disable-next-line:max-line-length
                    backgroundColor: colorPalettes.default,
                    borderWidth: 1,
                  },
                ],
              },
            },
          }
        },
      )

      // hits by Dept BarChart Data
      const barChartDeptHitsLabel: string[] = []
      const deptHitsData: number[] = []
      this.channelAnalyticsData.department = this.channelAnalyticsData.department.sort(
        (a: any, b: any) =>
          a.total_hits > b.total_hits ? -1 : a.total_hits < b.total_hits ? 1 : 0,
      )
      this.channelAnalyticsData.department.forEach((deptHits: NsChannelAnalytics.IChartData) => {
        if (barChartDeptHitsLabel.length < 20) {
          barChartDeptHitsLabel.push(deptHits.key)
        }
        if (deptHitsData.length < 20) {
          deptHitsData.push(deptHits.total_hits)
        }
      })
      this.barChartDeptHitsData = {
        widgetType: ROOT_WIDGET_CONFIG.graph._type,
        widgetSubType: ROOT_WIDGET_CONFIG.graph.graphGeneral,
        widgetData: {
          graphId: 'deptHitsChart',
          graphType: 'horizontalBar',
          graphHeight: '300px',
          graphWidth: '100%',
          graphLegend: false,
          graphLegendPosition: 'top',
          graphLegendFontSize: 11,
          graphTicksFontSize: 11,
          graphGridLinesDisplay: false,
          graphTicksXAxisDisplay: false,
          graphTicksYAxisDisplay: true,
          graphDefaultPalette: 'default',
          graphData: {
            labels: barChartDeptHitsLabel,
            datasets: [
              {
                label: '',
                // tslint:disable-next-line:max-line-length
                backgroundColor: ['rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)'],
                borderWidth: 1,
                data: deptHitsData,
              },
            ],
          },
        },
      }

      // hits by Country BarChart Data
      const barChartCountryHitsLabel: string[] = []
      const countryHitsData: number[] = []
      this.channelAnalyticsData.country = this.channelAnalyticsData.country.sort((a: any, b: any) =>
        a.total_hits > b.total_hits ? -1 : a.total_hits < b.total_hits ? 1 : 0,
      )
      this.channelAnalyticsData.country.forEach((countryHits: any) => {
        if (barChartCountryHitsLabel.length < 20) {
          barChartCountryHitsLabel.push(countryHits.key)
        }
        if (countryHitsData.length < 20) {
          countryHitsData.push(countryHits.total_hits)
        }
      })
      this.barChartCountryHitsData = {
        widgetType: ROOT_WIDGET_CONFIG.graph._type,
        widgetSubType: ROOT_WIDGET_CONFIG.graph.graphGeneral,
        widgetData: {
          graphId: 'countryHitsChart',
          graphType: 'horizontalBar',
          graphHeight: '300px',
          graphWidth: '100%',
          graphLegend: false,
          graphLegendPosition: 'top',
          graphLegendFontSize: 11,
          graphTicksFontSize: 11,
          graphTicksXAxisDisplay: false,
          graphTicksYAxisDisplay: true,
          graphGridLinesDisplay: false,
          graphDefaultPalette: 'default',
          graphData: {
            labels: barChartCountryHitsLabel,
            datasets: [
              {
                label: '',
                // tslint:disable-next-line:max-line-length
                backgroundColor: ['rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)'],
                borderWidth: 1,
                data: countryHitsData,
              },
            ],
          },
        },
      }
    }
  }
  onClose() {
    this.isExpandTrue = false
    this.chartData()
  }

  onExpandClient(field: string, type: string) {
    if (this.channelAnalyticsData) {
      const barChartExpandLabel: string[] = []
      const expandData: number[] = []
      this.channelAnalyticsData[field].forEach((expand: any) => {
        if (barChartExpandLabel.length <= 100) {
          barChartExpandLabel.push(expand.key)
        }
        if (expandData.length <= 100 && type === 'users') {
          expandData.push(expand.doc_count)
        }
        if (expandData.length <= 100 && type === 'hits') {
          expandData.push(expand.total_hits)
        }
      })
      this.barChartExpandClientData = {
        widgetType: ROOT_WIDGET_CONFIG.graph._type,
        widgetSubType: ROOT_WIDGET_CONFIG.graph.graphGeneral,
        widgetData: {
          graphId: 'expandChart',
          graphType: 'horizontalBar',
          graphHeight: '1000px',
          graphWidth: '100%',
          graphLegend: false,
          graphLegendPosition: 'top',
          graphLegendFontSize: 11,
          graphTicksFontSize: 11,
          graphTicksXAxisDisplay: false,
          graphTicksYAxisDisplay: true,
          graphGridLinesDisplay: false,
          graphDefaultPalette: 'default',
          graphData: {
            labels: barChartExpandLabel,
            datasets: [
              {
                label: '',
                // tslint:disable-next-line:max-line-length
                backgroundColor: ['rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)', 'rgb(219, 219, 141)', 'rgb(188, 189, 34)', 'rgb(199, 199, 199)', 'rgb(127, 127, 127)', 'rgb(247, 182, 210)', 'rgb(174, 199, 232)', 'rgb(227, 119, 194)', 'rgb(196, 156, 148)', 'rgb(140, 86, 75)', 'rgb(197, 176, 213)', 'rgb(148, 103, 189)', 'rgb(255, 152, 150)', 'rgb(214, 39, 40)', 'rgb(152, 223, 138)', 'rgb(44, 160, 44)', 'rgb(255, 187, 120)', 'rgb(255, 127, 14)', 'rgb(174, 199, 232)', 'rgb(31, 119, 180)'],
                borderWidth: 1,
                data: expandData,
              },
            ],
          },
        },
      }
      this.isExpandTrue = true
    }
  }
}
