import { Component, OnInit } from '@angular/core'
import { ActivatedRoute, Router } from '@angular/router'
import { IGraphData, IGraphDataSets, ROOT_WIDGET_CONFIG } from '@ws-widget/collection'
import { ConfigurationsService, TFetchStatus } from '@ws-widget/utils'
// import { ICardSkill } from '../../components/card-skill/card-skill.component'
import {
  IAssessment, IAuthResponse,
  IGraphDataCourse, IGraphWidget, ISkillQuotient, IWidgetData,
} from '../../models/competency-model'
// import { NsSkills } from '../../models/skills.model'
import { SkillsService } from '../../services/skills.service'
import { MatTabChangeEvent } from '@angular/material'
@Component({
  selector: 'ws-app-skill-details',
  templateUrl: './skill-details.component.html',
  styleUrls: ['./skill-details.component.scss'],
})
export class SkillDetailsComponent implements OnInit {
  apiFetchStatus: TFetchStatus = 'fetching'
  skillId = ''
  skillDetails: ISkillQuotient = {} as ISkillQuotient
  trainedGraphData: IGraphWidget = {} as IGraphWidget
  courseGraphData: IGraphData = {} as IGraphData
  certifiedGraphData: IGraphWidget = {} as IGraphWidget
  courseDataSets: IGraphDataSets[] = []
  certificationDataSets: IGraphDataSets[] = []
  courseCompletionData: number[] = []
  certificationCompletionData: number[] = []
  certificationGraphData: IGraphData = {} as IGraphData
  displayedColumns: string[] = ['date', 'content_name', 'score', 'percentile']
  assessmentDetails: IAssessment[] = []
  widgetHorizontalBar: IGraphWidget[] = []
  graphLabels: string[] = []
  availableCourseOrgPieData: IGraphDataCourse[] = []
  availableCertificationOrgPieData: IGraphDataCourse[] = []
  loader = true
  skillQuotientData: any
  learningPath = ''
  defaultThumbnail = ''
  skillGroup = ''
  skillCategory = ''
  skillRelevance = ''
  skillManager = ''
  skillName = ''
  fetchImage = false
  mandatoryCourse = false
  preRequisiteCourse = false
  desirableCourse = false
  mandatoryCertificate = false
  preRequisiteCertificate = false
  desirableCertificate = false
  mandatoryCourses: IWidgetData | null = null
  preRequisiteCourses: IWidgetData | null = null
  desirableCourses: IWidgetData | null = null
  mandatoryCertificates: IWidgetData | null = null
  preRequisiteCertificates: IWidgetData | null = null
  desirableCertificates: IWidgetData | null = null
  authData: IAuthResponse | null = null
  isSkillManager = false
  isClient = this.activatedRoute.snapshot.data.pageData.data.enabledTabs.skills.subTabs.skills.client
  enabledTabs = this.activatedRoute.snapshot.data.pageData.data.enabledTabs.skills.subTabs
  assessmentQuotient = 0
  certificationQuotient = 0
  skillClientData: any
  preRequisites: any
  selectedTabIndex = 0
  constructor(
    private skillSrv: SkillsService,
    private configSvc: ConfigurationsService,
    private router: Router,
    private activatedRoute: ActivatedRoute,

  ) { }

  ngOnInit() {
    this.apiFetchStatus = 'fetching'
    this.skillId = this.activatedRoute.snapshot.params.id
    const instanceConfig = this.configSvc.instanceConfig
    if (instanceConfig) {
      this.defaultThumbnail = instanceConfig.logos.defaultContent
    }
    this.fetchImage = true
    // if (this.isClient) {
    //   this.skillSrv.isApprover().subscribe((access: IAuthResponse) => {
    //     this.authData = access
    //     this.authData.roles.forEach((role: IApprover) => {
    //       if (role.role === 'skill_manager') {
    //         this.isSkillManager = true
    //       }
    //     })
    //   })
    // }
    this.skillQuotientDetails(this.skillId)
  }
  clickEvent(event: any) {
    this.apiFetchStatus = 'fetching'
    this.skillId = event
    const instanceConfig = this.configSvc.instanceConfig
    if (instanceConfig) {
      this.defaultThumbnail = instanceConfig.logos.defaultContent
    }
    this.fetchImage = true
    this.skillQuotientDetails(this.skillId)
  }
  onEditSkill(type: string) {
    if (type === 'edit') {
      this.router.navigate(['/app/profile/skills/edit-skill', this.skillId])
    } else if (type === 'endorse') {
      this.router.navigate(['/app/profile/skills/create-endorsement'], { queryParams: { id: this.skillId } })
    } else {
      this.router.navigate(['/app/profile/skills/copy-skill', this.skillId])
    }
  }
  skillQuotientDetails(skillId: any) {
    this.skillSrv.skillQuotient(skillId).subscribe(response => {
      this.skillClientData = response.skill_quotient
      this.skillName = this.skillClientData.skill_name
      this.skillQuotientData = []
      this.skillGroup = this.skillClientData.skill_group
      this.skillCategory = this.skillClientData.skill_category
      this.skillManager = this.skillClientData.skill_manager
      this.skillRelevance = this.skillClientData.skill_relevance
      this.learningPath = this.skillClientData.skill_levels[0].linked_program_name
      this.skillClientData.skill_levels.forEach((level: any) => {
        // tslint:disable-next-line:max-line-length
        if (level.skill_level === 'Starter') {
          this.skillQuotientData.push(level)
          if (level.skill_level === 'Starter') {
            if (level.courses) {
              this.mandatoryCourse = level.courses.mandatory.length > 0 ? true : false
              this.desirableCourse = level.courses.desirable.length > 0 ? true : false
              this.preRequisiteCourse = level.courses.pre_requisite.length > 0 ? true : false
              this.mandatoryCourses = {
                widgetData: {
                  strips: [
                    {
                      key: 'mandatory_starter_courses',
                      title: 'Courses',
                      request: {
                        ids: level.courses.mandatory,
                      },
                    },
                  ],
                  loader: true,
                },
                widgetSubType: 'contentStripMultiple',
                widgetType: 'contentStrip',
                widgetHostClass: '',
              }
              this.preRequisiteCourses = {
                widgetData: {
                  strips: [
                    {
                      key: 'pre_requisite_starter_courses',
                      title: 'Pre-requisite Courses',
                      request: {
                        ids: level.courses.pre_requisite,
                      },
                    },
                  ],
                  loader: true,
                },
                widgetSubType: 'contentStripMultiple',
                widgetType: 'contentStrip',
                widgetHostClass: '',
              }
              this.desirableCourses = {
                widgetData: {
                  strips: [
                    {
                      key: 'desirable_starter_courses',
                      title: 'Desirable Courses',
                      request: {
                        ids: level.courses.desirable,
                      },
                    },
                  ],
                  loader: true,
                },
                widgetSubType: 'contentStripMultiple',
                widgetType: 'contentStrip',
                widgetHostClass: '',
              }
            }
            if (level.certifications) {
              this.mandatoryCertificate = level.certifications.mandatory.length > 0 ? true : false
              this.desirableCertificate = level.certifications.desirable.length > 0 ? true : false
              this.preRequisiteCertificate = level.certifications.pre_requisite.length > 0 ? true : false
              this.mandatoryCertificates = {
                widgetData: {
                  strips: [
                    {
                      key: 'mandatory_starter_certifications',
                      title: 'Certifications',
                      request: {
                        ids: level.certifications.mandatory,
                      },
                    },
                  ],
                  loader: true,
                },
                widgetSubType: 'contentStripMultiple',
                widgetType: 'contentStrip',
                widgetHostClass: '',
              }
              this.preRequisiteCertificates = {
                widgetData: {
                  strips: [
                    {
                      key: 'pre_requisite_starter_certifications',
                      title: 'Pre-requisite Certifications',
                      request: {
                        ids: level.certifications.pre_requisite,
                      },
                    },
                  ],
                  loader: true,
                },
                widgetSubType: 'contentStripMultiple',
                widgetType: 'contentStrip',
                widgetHostClass: '',
              }
              this.desirableCertificates = {
                widgetData: {
                  strips: [
                    {
                      key: 'desirable_starter_certifications',
                      title: 'Desirable Certifications',
                      request: {
                        ids: level.certifications.desirable,
                      },
                    },
                  ],
                  loader: true,
                },
                widgetSubType: 'contentStripMultiple',
                widgetType: 'contentStrip',
                widgetHostClass: '',
              }
            }
          }
        }
      })
      this.skillClientData.skill_levels.forEach((level: any) => {
        // tslint:disable-next-line:max-line-length
        if (level.skill_level === 'Basic') {
          this.skillQuotientData.push(level)
        }

      })
      this.skillClientData.skill_levels.forEach((level: any) => {
        // tslint:disable-next-line:max-line-length
        if (level.skill_level === 'Advanced') {
          this.skillQuotientData.push(level)
        }
      })
      this.skillClientData.skill_levels.forEach((level: any) => {
        // tslint:disable-next-line:max-line-length
        if (level.skill_level === 'Expert') {
          this.skillQuotientData.push(level)
        }
      })
      // this.apiFetchStatus = 'done'
      // this.skillDetails = response
      // this.skillName = this.skillDetails.skill_quotient.skill_name
      // if (this.skillDetails.prerequisites) {
      //   this.preRequisites = (this.skillDetails.prerequisites || []).map((content: any) => this.convertToCardData(content))
      // }
      if (!this.isClient) {
        this.graphData()
      }
      this.apiFetchStatus = 'done'
    })
  }

  tabChange(event: MatTabChangeEvent) {
    if (event.index === 0) {
      this.skillClientData.skill_quotient.skill_levels.forEach((level: any) => {
        // tslint:disable-next-line:max-line-length
        if (level.skill_level === 'Starter') {
          if (level.courses) {
            this.mandatoryCourse = level.courses.mandatory.length > 0 ? true : false
            this.desirableCourse = level.courses.desirable.length > 0 ? true : false
            this.preRequisiteCourse = level.courses.pre_requisite.length > 0 ? true : false
            this.mandatoryCourses = {
              widgetData: {
                strips: [
                  {
                    key: 'mandatory_starter_courses',
                    title: 'Courses',
                    request: {
                      ids: level.courses.mandatory,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
            this.preRequisiteCourses = {
              widgetData: {
                strips: [
                  {
                    key: 'pre_requisite_starter_courses',
                    title: 'Pre-requisite Courses',
                    request: {
                      ids: level.courses.pre_requisite,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
            this.desirableCourses = {
              widgetData: {
                strips: [
                  {
                    key: 'desirable_starter_courses',
                    title: 'Desirable Courses',
                    request: {
                      ids: level.courses.desirable,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
          }
          if (level.certifications) {
            this.mandatoryCertificate = level.certifications.mandatory.length > 0 ? true : false
            this.desirableCertificate = level.certifications.desirable.length > 0 ? true : false
            this.preRequisiteCertificate = level.certifications.pre_requisite.length > 0 ? true : false
            this.mandatoryCertificates = {
              widgetData: {
                strips: [
                  {
                    key: 'mandatory_starter_certifications',
                    title: 'Certifications',
                    request: {
                      ids: level.certifications.mandatory,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
            this.preRequisiteCertificates = {
              widgetData: {
                strips: [
                  {
                    key: 'pre_requisite_starter_certifications',
                    title: 'Pre-requisite Certifications',
                    request: {
                      ids: level.certifications.pre_requisite,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
            this.desirableCertificates = {
              widgetData: {
                strips: [
                  {
                    key: 'desirable_starter_certifications',
                    title: 'Desirable Certifications',
                    request: {
                      ids: level.certifications.desirable,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
          }
        }

      })
    } else if (event.index === 1) {
      this.skillClientData.skill_quotient.skill_levels.forEach((level: any) => {
        // tslint:disable-next-line:max-line-length
        if (level.skill_level === 'Basic') {
          if (level.courses) {
            this.mandatoryCourse = level.courses.mandatory.length > 0 ? true : false
            this.desirableCourse = level.courses.desirable.length > 0 ? true : false
            this.preRequisiteCourse = level.courses.pre_requisite.length > 0 ? true : false
            this.mandatoryCourses = {
              widgetData: {
                strips: [
                  {
                    key: 'mandatory_basic_courses',
                    title: 'Courses',
                    request: {
                      ids: level.courses.mandatory,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
            this.preRequisiteCourses = {
              widgetData: {
                strips: [
                  {
                    key: 'pre_requisite_basic_courses',
                    title: 'Pre-requisite Courses',
                    request: {
                      ids: level.courses.pre_requisite,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
            this.desirableCourses = {
              widgetData: {
                strips: [
                  {
                    key: 'desirable_basic_courses',
                    title: 'Desirable Courses',
                    request: {
                      ids: level.courses.desirable,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
          }
          if (level.certifications) {
            this.mandatoryCertificate = level.certifications.mandatory.length > 0 ? true : false
            this.desirableCertificate = level.certifications.desirable.length > 0 ? true : false
            this.preRequisiteCertificate = level.certifications.pre_requisite.length > 0 ? true : false
            this.mandatoryCertificates = {
              widgetData: {
                strips: [
                  {
                    key: 'mandatory_basic_certifications',
                    title: 'Certifications',
                    request: {
                      ids: level.certifications.mandatory,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
            this.preRequisiteCertificates = {
              widgetData: {
                strips: [
                  {
                    key: 'pre_requisite_basic_certifications',
                    title: 'Pre-requisite Certifications',
                    request: {
                      ids: level.certifications.pre_requisite,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
            this.desirableCertificates = {
              widgetData: {
                strips: [
                  {
                    key: 'desirable_basic_certifications',
                    title: 'Desirable Certifications',
                    request: {
                      ids: level.certifications.desirable,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
          }
        }

      })
    } else if (event.index === 2) {
      this.skillClientData.skill_quotient.skill_levels.forEach((level: any) => {
        // tslint:disable-next-line:max-line-length
        if (level.skill_level === 'Advanced') {
          if (level.courses) {
            this.mandatoryCourse = level.courses.mandatory.length > 0 ? true : false
            this.desirableCourse = level.courses.desirable.length > 0 ? true : false
            this.preRequisiteCourse = level.courses.pre_requisite.length > 0 ? true : false
            this.mandatoryCourses = {
              widgetData: {
                strips: [
                  {
                    key: 'mandatory_advanced_courses',
                    title: 'Courses',
                    request: {
                      ids: level.courses.mandatory,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
            this.preRequisiteCourses = {
              widgetData: {
                strips: [
                  {
                    key: 'pre_requisite_advanced_courses',
                    title: 'Pre-requisite Courses',
                    request: {
                      ids: level.courses.pre_requisite,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
            this.desirableCourses = {
              widgetData: {
                strips: [
                  {
                    key: 'desirable_advanced_courses',
                    title: 'Desirable Courses',
                    request: {
                      ids: level.courses.desirable,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
          }
          if (level.certifications) {
            this.mandatoryCertificate = level.certifications.mandatory.length > 0 ? true : false
            this.desirableCertificate = level.certifications.desirable.length > 0 ? true : false
            this.preRequisiteCertificate = level.certifications.pre_requisite.length > 0 ? true : false
            this.mandatoryCertificates = {
              widgetData: {
                strips: [
                  {
                    key: 'mandatory_advanced_certifications',
                    title: 'Certifications',
                    request: {
                      ids: level.certifications.mandatory,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
            this.preRequisiteCertificates = {
              widgetData: {
                strips: [
                  {
                    key: 'pre_requisite_advanced_certifications',
                    title: 'Pre-requisite Certifications',
                    request: {
                      ids: level.certifications.pre_requisite,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
            this.desirableCertificates = {
              widgetData: {
                strips: [
                  {
                    key: 'desirable_advanced_certifications',
                    title: 'Desirable Certifications',
                    request: {
                      ids: level.certifications.desirable,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
          }
        }

      })
    } else {
      this.skillClientData.skill_quotient.skill_levels.forEach((level: any) => {
        // tslint:disable-next-line:max-line-length
        if (level.skill_level === 'Expert') {
          if (level.courses) {
            this.mandatoryCourse = level.courses.mandatory.length > 0 ? true : false
            this.desirableCourse = level.courses.desirable.length > 0 ? true : false
            this.preRequisiteCourse = level.courses.pre_requisite.length > 0 ? true : false
            this.mandatoryCourses = {
              widgetData: {
                strips: [
                  {
                    key: 'mandatory_expert_courses',
                    title: 'Courses',
                    request: {
                      ids: level.courses.mandatory,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
            this.preRequisiteCourses = {
              widgetData: {
                strips: [
                  {
                    key: 'pre_requisite_expert_courses',
                    title: 'Pre-requisite Courses',
                    request: {
                      ids: level.courses.pre_requisite,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
            this.desirableCourses = {
              widgetData: {
                strips: [
                  {
                    key: 'desirable_expert_courses',
                    title: 'Desirable Courses',
                    request: {
                      ids: level.courses.desirable,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
          }
          if (level.certifications) {
            this.mandatoryCertificate = level.certifications.mandatory.length > 0 ? true : false
            this.desirableCertificate = level.certifications.desirable.length > 0 ? true : false
            this.preRequisiteCertificate = level.certifications.pre_requisite.length > 0 ? true : false
            this.mandatoryCertificates = {
              widgetData: {
                strips: [
                  {
                    key: 'mandatory_expert_certifications',
                    title: 'Certifications',
                    request: {
                      ids: level.certifications.mandatory,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
            this.preRequisiteCertificates = {
              widgetData: {
                strips: [
                  {
                    key: 'pre_requisite_expert_certifications',
                    title: 'Pre-requisite Certifications',
                    request: {
                      ids: level.certifications.pre_requisite,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
            this.desirableCertificates = {
              widgetData: {
                strips: [
                  {
                    key: 'desirable_expert_certifications',
                    title: 'Desirable Certifications',
                    request: {
                      ids: level.certifications.desirable,
                    },
                  },
                ],
                loader: true,
              },
              widgetSubType: 'contentStripMultiple',
              widgetType: 'contentStrip',
              widgetHostClass: '',
            }
          }
        }
      })
    }
  }
  graphData() {
    this.assessmentQuotient = parseFloat((this.skillClientData.skill_course_comp).toFixed(2))
    this.certificationQuotient = parseFloat((this.skillClientData.skill_certification_comp).toFixed(2))
    this.courseCompletionData = []
    this.certificationCompletionData = []
    this.courseCompletionData.push.apply(this.courseCompletionData, [
      this.skillClientData.skill_course_comp,
      100 - this.skillClientData.skill_course_comp,
    ])
    const certificationCompletion = this.skillClientData.skill_certification_comp
      ? this.skillClientData.skill_certification_comp
      : 0
    this.certificationCompletionData.push.apply(this.certificationCompletionData, [
      certificationCompletion,
      100 - certificationCompletion,
    ])
    this.courseDataSets = [
      {
        data: this.courseCompletionData,
        backgroundColor: ['rgb(211, 84, 0)', 'rgb(241, 196, 15)', 'rgb(39, 174, 96)'],
        borderWidth: 1,
      },
    ]
    this.certificationDataSets = [
      {
        data: this.certificationCompletionData,
        backgroundColor: ['rgb(211, 84, 0)', 'rgb(241, 196, 15)', 'rgb(39, 174, 96)'],
        borderWidth: 1,
      },
    ]
    this.courseGraphData = {
      labels: ['Trained', 'Not Trained'],
      datasets: this.courseDataSets,
    }
    this.certificationGraphData = {
      labels: ['Certified', 'Not certified'],
      datasets: this.certificationDataSets,
    }
    this.trainedGraphData = {
      widgetType: ROOT_WIDGET_CONFIG.graph._type,
      widgetSubType: ROOT_WIDGET_CONFIG.graph.graphGeneral,
      widgetData: {
        graphId: 'pieChart',
        graphType: 'pie',
        graphHeight: '120px',
        graphWidth: '100%',
        graphLegend: true,
        graphLegendPosition: 'top',
        graphLegendFontSize: 11,
        graphTicksFontSize: 11,
        graphGridLinesDisplay: false,
        graphDefaultPalette: 'default',
        graphData: this.courseGraphData,
      },
    }
    this.certifiedGraphData = {
      widgetType: ROOT_WIDGET_CONFIG.graph._type,
      widgetSubType: ROOT_WIDGET_CONFIG.graph.graphGeneral,
      widgetData: {
        graphId: 'pieChart1',
        graphType: 'pie',
        graphHeight: '120px',
        graphWidth: '100%',
        graphLegend: true,
        graphLegendPosition: 'top',
        graphLegendFontSize: 11,
        graphTicksFontSize: 11,
        graphGridLinesDisplay: false,
        graphDefaultPalette: 'default',
        graphData: this.certificationGraphData,
      },
    }
    // // org wide course data
    // this.skillClientData.skill_levels[0].courses.map((cur: IAvailableCourseCertification, i: any) => {
    //   const others: IOrgData[] = this.skillClientData.skill_levels[0].course_org_data[cur.lex_id]
    //   if (others === undefined) {
    //     return
    //   }
    //   const obj: any = {
    //     // name: cur.content_name,
    //     id: cur.lex_id,
    //     // materialUrl: cur.material_url,
    //     totalUsers: others[3].doc_count || 0,
    //     legend: i === 0 ? true : false,
    //     data: [
    //       {
    //         y: others[0].doc_count || 0,
    //       },
    //       {
    //         y: others[1].doc_count || 0,
    //       },
    //       {
    //         y: others[2].doc_count || 0,
    //       },
    //       {
    //         y: others[3].doc_count + others[4].doc_count || 0,
    //       },
    //     ],
    //   }
    //   this.availableCourseOrgPieData.push(obj)
    // })
    // // org wide certification data
    // this.skillClientData.skill_levels[0].certifications.map((cur: IAvailableCourseCertification, i: any) => {
    //   const others: ICount[] = this.skillClientData.skill_levels[0].course_org_data[cur.lex_id]
    //   if (others === undefined) {
    //     return
    //   }
    //   const obj: any = {
    //     // name: cur.content_name,
    //     id: cur.lex_id,
    //     // materialUrl: cur.material_url,
    //     totalUsers: others[1].doc_count || 0,
    //     legend: i === 0 ? true : false,
    //     data: [
    //       {
    //         y: others[0].doc_count || 0,
    //       },
    //       {
    //         y: others[1].doc_count || 0,
    //       },
    //     ],
    //   }
    //   this.availableCertificationOrgPieData.push(obj)
    // })
  }
  // private convertToCardData(content: NsSkills.IRecommendedSkill | NsSkills.ISkill): ICardSkill {
  //   return {
  //     category: content.category,
  //     certificationCount: content.certification_count || 0,
  //     courseCount: content.course_count || 0,
  //     horizon: content.horizon,
  //     group: content.skill_group,
  //     level: content.skill_level,
  //     id: `${content.skill_id}`,
  //     imageUrl: content.image_url,
  //     navigationUrl: `/app/profile/skills/skill-details/${content.skill_id}`,
  //     title: content.skill_name,
  //   }
  // }
}
