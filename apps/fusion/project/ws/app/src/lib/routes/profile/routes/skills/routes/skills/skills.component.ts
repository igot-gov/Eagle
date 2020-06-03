import { Component, OnInit } from '@angular/core'
import { SkillsService } from '../../services/skills.service'
import { NsSkills } from '../../models/skills.model'
import { TFetchStatus, ConfigurationsService } from '@ws-widget/utils'
import { ICardSkill } from '../../components/card-skill/card-skill.component'
import { IAuthResponse } from '../../models/competency-model'
import { ROOT_WIDGET_CONFIG, colorPalettes } from '@ws-widget/collection'
import { NSProfileData } from '../../../../models/profile.model'
import { Chart } from 'chart.js'
import { ActivatedRoute } from '@angular/router'
@Component({
  selector: 'ws-app-skills',
  templateUrl: './skills.component.html',
  styleUrls: ['./skills.component.scss'],
})
export class SkillsComponent implements OnInit {
  lineChart: Chart | null = null
  requiredSkillsData: ICardSkill[] = []
  acquiredSkillsData: ICardSkill[] = []
  recommendedSkillsData: ICardSkill[] = []
  createdSkillsData: ICardSkill[] = []
  authData: IAuthResponse | null = null
  isSkillManager = false
  userRole = ''
  userName = ''
  skillGroup = ''
  status = ''
  skillsLimit = 15
  acquiredSkillsFetchStatus: TFetchStatus = 'fetching'
  recommendedSkillsFetchStatus: TFetchStatus = 'fetching'
  requiredSkillsFetchStatus: TFetchStatus = 'fetching'
  createdSkillsFetchStatus: TFetchStatus = 'fetching'
  authFetchStatus: TFetchStatus = 'fetching'
  donutChartData: NSProfileData.IGraphWidget = {} as NSProfileData.IGraphWidget
  lineChartData: NSProfileData.IGraphWidget = {} as NSProfileData.IGraphWidget
  isClient = this.activatedRoute.snapshot.data.pageData.data.enabledTabs.skills.subTabs.skills
    .client
  enabledTabs = this.activatedRoute.snapshot.data.pageData.data.enabledTabs.skills.subTabs
  lineData = [
    {
      key: 'Completed All Courses',
      y: 1,
    },
    {
      key: 'Completed NodeJS',
      y: 2.4,
    },
    {
      key: 'Completed Angular',
      y: 3.6,
    },
    {
      key: 'Goal to complete Mongo DB',
      y: 4.5,
    },
  ]
  constructor(
    private skillsSvc: SkillsService,
    private activatedRoute: ActivatedRoute,
    private configSvc: ConfigurationsService,
  ) {}

  ngOnInit() {
    // if (this.isClient) {
    //   this.skillsSvc.isApprover().subscribe(
    //     (access: IAuthResponse) => {
    //       this.authData = access
    //       this.authData.roles.forEach((role: IApprover) => {
    //         this.skillGroup = role.skill_group
    //         this.userRole = role.role_name
    //         if (role.role === 'skill_manager') {
    //           this.isSkillManager = true
    //           // this.fetchCreatedSkills()
    //         }
    //       })
    //       this.authFetchStatus = 'done'
    //     },
    //     // tslint:disable-next-line:align
    //     () => {
    //       this.authFetchStatus = 'error'
    //     },
    //   )
    // }
    if (this.configSvc.userProfile) {
      this.userName = this.configSvc.userProfile.givenName || ''
    }
    // this.fetchAcquiredSkills()
    this.fetchRequiredSkills()
    // this.fetchRecommendedSkills()
    const monthList = [
      'Jan',
      'Feb',
      'Mar',
      'Apr',
      'May',
      'Jun',
      'Jul',
      'Aug',
      'Sep',
      'Oct',
      'Nov',
      'Dec',
    ]
    const currentMonth = new Date().getMonth() + 1
    const month = monthList.slice(currentMonth - 4, currentMonth)
    this.donutChartData = {
      widgetType: ROOT_WIDGET_CONFIG.graph._type,
      widgetSubType: ROOT_WIDGET_CONFIG.graph.graphGeneral,
      widgetData: {
        graphId: 'roleChart',
        graphType: 'doughnut',
        graphHeight: '200px',
        graphWidth: '100%',
        graphLegend: false,
        graphLegendPosition: 'top',
        graphLegendFontSize: 11,
        graphTicksFontSize: 11,
        graphGridLinesDisplay: false,
        graphDefaultPalette: 'default',
        graphData: {
          labels: ['Default Score', 'Certification Score', 'Not Scored'],
          datasets: [
            {
              label: '',
              data: [2, 0.82, 2.18],
              backgroundColor: colorPalettes['default'],
              borderWidth: 1,
            },
          ],
        },
      },
    }
    const lineData = this.lineData
    this.lineChart = new Chart('canvas', {
      type: 'line',
      data: {
        labels: month,
        datasets: [
          {
            data: [1, 2.4, 3.6],
            label: 'Role Quotient',
            backgroundColor: 'rgba(255, 99, 132, 0.2)',
            borderColor: 'rgba(255, 99, 132, 1)',
            borderWidth: 3,
            lineTension: 0,
            fill: true,
            pointRadius: [-90, -90, -90, 5],
            pointBorderColor: 'rgba(255, 99, 132, 0.2)',
            pointBackgroundColor: 'rgba(255, 99, 132, 0.2)',
            pointHoverBackgroundColor: 'rgba(255, 99, 132, 0.2)',
            pointHoverBorderColor: 'rgba(255, 99, 132, 0.2)',
            pointBorderWidth: 10,
            pointHoverRadius: 10,
            pointHoverBorderWidth: 1,
          },
          {
            data: [1, 2.4, 3.6, 4.5],
            backgroundColor: 'rgba(54, 162, 235, 0.2)',
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 3,
            label: 'Role Quotient',
            lineTension: 0,
            fill: true,
            borderDash: [5, 5],
            pointRadius: [-90, -90, -90, 5],
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        animation: {
          easing: 'easeInExpo',
        },
        tooltips: {
          callbacks: {
            label(tooltipItem: any) {
              const label = `${tooltipItem.xLabel} :  ${lineData[tooltipItem.index].key}`
              return label
            },
          },
        },
        scales: {
          xAxes: [
            {
              gridLines: {
                display: false,
              },
              scaleLabel: {
                display: true,
                labelString: 'Months',
              },
            },
          ],
          yAxes: [
            {
              ticks: {
                beginAtZero: true,
                max: 5,
                stepSize: 1,
                suggestedMin: 5,
              },
              gridLines: {
                display: false,
              },
              scaleLabel: {
                display: true,
                labelString: 'Role Quotient',
              },
            },
          ],
        },
        legend: {
          display: false,
        },
      },
    })

    this.lineChartData = {
      widgetType: ROOT_WIDGET_CONFIG.graph._type,
      widgetSubType: ROOT_WIDGET_CONFIG.graph.graphGeneral,
      widgetData: {
        graphId: 'lineChart',
        graphType: 'line',
        graphHeight: '300px',
        graphWidth: '100%',
        graphLegend: false,
        graphLegendPosition: 'top',
        graphLegendFontSize: 11,
        graphTicksFontSize: 11,
        graphXAxisLabel: 'Month',
        graphYAxisLabel: 'Role Quotient',
        graphIsXAxisLabel: true,
        graphIsYAxisLabel: true,
        graphGridLinesDisplay: false,
        graphTicksXAxisDisplay: true,
        graphTicksYAxisDisplay: true,
        graphDefaultPalette: 'default',
        graphYAxisMax: 5,
        graphYAxisStepSize: 1,
        graphData: {
          labels: month,
          datasets: [
            {
              label: 'Role Quotient',
              data: [1, 2.4, 3.6],
              borderWidth: 4,
              fill: false,
              lineTension: 0,
              borderColor: ['#2d98da'],
              pointRadius: [-90, -90, -90, 5],
              pointBorderColor: '#2d98da',
              pointBackgroundColor: '#2d98da',
              pointHoverBackgroundColor: '#2d98da',
              pointHoverBorderColor: '#2d98da',
              pointBorderWidth: 10,
              pointHoverRadius: 10,
              pointHoverBorderWidth: 1,
            },
            {
              label: 'Role Quotient',
              data: [1, 2.4, 3.6, 4.5],
              fill: false,
              lineTension: 0,
              borderColor: ['#eb3b5a'],
              borderDash: [5, 5],
              pointRadius: [-90, -90, -90, 5],
            },
          ],
        },
      },
    }
  }

  // private fetchAcquiredSkills() {
  //   this.skillsSvc.mySkills().subscribe(
  //     data => {
  //       this.acquiredSkillsData = (data || []).map(content => this.convertToCardData(content))
  //       this.acquiredSkillsFetchStatus = this.acquiredSkillsData.length ? 'done' : 'none'
  //     },
  //     () => {
  //       this.acquiredSkillsFetchStatus = 'error'
  //     },
  //   )
  // }
  private fetchRequiredSkills() {
    this.skillsSvc.requiredSKills(this.status).subscribe(
      data => {
        this.requiredSkillsData = (data || []).map(content => this.convertToCardData(content))
        this.requiredSkillsFetchStatus = this.requiredSkillsData.length ? 'done' : 'none'
      },
      () => {
        this.requiredSkillsFetchStatus = 'error'
      },
    )
  }

  // private fetchCreatedSkills() {
  //   this.skillsSvc.createdSkills().subscribe(
  //     data => {
  //       this.createdSkillsData = (data || []).map(content => this.convertToCardData(content))
  //       this.createdSkillsFetchStatus = this.createdSkillsData.length ? 'done' : 'none'
  //     },
  //     () => {
  //       this.createdSkillsFetchStatus = 'error'
  //     },
  //   )
  // }
  // private fetchRecommendedSkills() {
  //   this.skillsSvc.recommendedSkills().subscribe(
  //     data => {
  //       this.recommendedSkillsData = (data || []).map(content => this.convertToCardData(content))
  //       this.recommendedSkillsFetchStatus = this.recommendedSkillsData.length ? 'done' : 'none'
  //     },
  //     () => {
  //       this.recommendedSkillsFetchStatus = 'error'
  //     },
  //   )
  // }
  private convertToCardData(content: NsSkills.IRecommendedSkill | NsSkills.ISkill): ICardSkill {
    return {
      category: content.category,
      certificationCount: content.certification_count || 0,
      courseCount: content.course_count || 0,
      horizon: content.horizon,
      group: content.skill_group,
      level: content.skill_level,
      id: `${content.skill_id}`,
      imageUrl: content.image_url,
      navigationUrl: `/app/profile/skills/skill-details/${content.skill_id}`,
      title: content.skill_name,
      description: content.skill_description,
      manager: content.skill_manager,
      relevance: content.skill_relevance,
      status: content.status,
      endorsementData: content,
    }
  }
}
