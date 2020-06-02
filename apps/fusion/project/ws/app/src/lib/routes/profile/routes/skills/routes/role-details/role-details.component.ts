import { Component, OnInit, ViewChild } from '@angular/core'
import { SkillsService } from '../../services/skills.service'
import { TFetchStatus, ConfigurationsService } from '@ws-widget/utils'
import {
  IGraphWidget,
  IAssessment,
  IGraphDataCourse,
  IAuthResponse,
} from '../../models/competency-model'
import { IGraphData, IGraphDataSets, ROOT_WIDGET_CONFIG } from '@ws-widget/collection'
import { ActivatedRoute, Router } from '@angular/router'
import { NsSkills } from '../../models/skills.model'
import { ICardSkill } from '../../components/card-skill/card-skill.component'
import { DeleteRoleComponent } from '../../routes/delete-role/delete-role.component'
import { MatPaginator, MatTableDataSource, MatDialog } from '@angular/material'
@Component({
  selector: 'ws-app-role-details',
  templateUrl: './role-details.component.html',
  styleUrls: ['./role-details.component.scss'],
})
export class RoleDetailsComponent implements OnInit {
  displayedColumnsSkill: string[] = ['skillName', 'requiredLevel', 'acquiredLevel', 'status']
  displayedColumnsRoles: string[] = ['skillName', 'status']
  dataSource: any
  apiFetchStatus: TFetchStatus = 'fetching'
  roleId = ''
  roleName = ''
  roleDetails: any
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
  defaultThumbnail = ''
  fetchImage = false
  skillsData: any = []
  authData: IAuthResponse | null = null
  isSkillManager = false
  roleResponsibilities = []
  isClient = this.route.snapshot.data.pageData.data.enabledTabs.skills.subTabs.skills.client
  enabledTabs = this.route.snapshot.data.pageData.data.enabledTabs.skills.subTabs
  assessmentQuotient = 0
  certificationQuotient = 0
  userWid = ''
  creator = false
  @ViewChild(MatPaginator, { static: true }) paginator: any
  constructor(
    private skillSrv: SkillsService,
    private configSvc: ConfigurationsService,
    private router: Router,
    private route: ActivatedRoute,
    public dialog: MatDialog,
  ) { }

  ngOnInit() {
    this.roleId = this.route.snapshot.params.id
    this.apiFetchStatus = 'fetching'
    const instanceConfig = this.configSvc.instanceConfig
    if (instanceConfig) {
      this.defaultThumbnail = instanceConfig.logos.defaultContent
    }
    if (this.configSvc.userProfile) {
      this.userWid = this.configSvc.userProfile.userId || ''
    }
    this.fetchImage = true
    // this.skillSrv.isApprover().subscribe((access: IAuthResponse) => {
    //   this.authData = access
    //   this.authData.roles.forEach((role: IApprover) => {
    //     if (role.role === 'skill_manager') {
    //       this.isSkillManager = true
    //     }
    //   })
    // })
    this.skillQuotientDetails()
  }
  skillQuotientDetails() {
    this.skillSrv.roleQuotient(this.roleId).subscribe(response => {
      this.roleDetails = response
      if (this.isClient) {
        this.skillsData = this.roleDetails.skills
        this.roleName = this.roleDetails.role_name
        if (this.userWid === this.roleDetails.created_by) {
          this.creator = true
        }
        this.roleResponsibilities = this.roleDetails.role_responsibilities.replace(/'*'/g, ' ')
        this.skillsData = (this.skillsData || []).map((content: any) => this.convertToCardData(content))
        this.dataSource = new MatTableDataSource<ICardSkill>(this.skillsData)
        this.dataSource.paginator = this.paginator
        this.apiFetchStatus = this.skillsData.length ? 'done' : 'none'
      } else {
        this.skillsData = this.roleDetails.role_quotient_details.skills
        this.roleName = this.roleDetails.role_quotient_details.role_name
        this.skillsData = (this.skillsData || []).map((content: any) => this.convertToCardData(content))
        this.dataSource = new MatTableDataSource<ICardSkill>(this.skillsData)
        this.dataSource.paginator = this.paginator
        this.apiFetchStatus = this.skillsData.length ? 'done' : 'none'
        this.graphData()
      }
      setTimeout(
        () => {
          const el = document.getElementsByClassName('replaceSpace')
          for (let index = 0; index < el.length; index = index + 1) {
            el[index].innerHTML = el[index].innerHTML.replace(/&nbsp;/g, ' ')
          }
        },
        10,
      )
      this.apiFetchStatus = 'done'
    })
  }
  onIconClick(action: string) {
    if (action === 'edit') {
      this.router.navigate(['/app/profile/skills/edit-role', this.roleId])
    } else if (action === 'assign') {
      this.router.navigate(['/app/profile/skills/assign-role', this.roleId])
    } else {
      this.router.navigate(['/app/profile/skills/copy-role', this.roleId])
    }
  }
  openDialog(): void {
    const dialogRef = this.dialog.open(DeleteRoleComponent, { data: this.roleId })
    dialogRef.afterClosed().subscribe(() => {
      this.router.navigate(['/app/profile/skills/roles', this.roleId])
    })
  }
  addToShortList(skillId: any, skillName: string) {
    const obj = {
      skill_id: skillId,
      skill_name: skillName,
    }
    this.skillSrv.shortListedSkills(obj).subscribe(() => {
      this.router.navigate(['/app/profile/skills/my-skills/pending-skills'])
    })
  }
  onClickIcon(skillId: any, skillName: string) {
    this.addToShortList(skillId, skillName)
  }
  graphData() {
    this.assessmentQuotient = parseFloat((this.roleDetails.assessment_quotient).toFixed(2))
    this.certificationQuotient = parseFloat((this.roleDetails.certification_quotient).toFixed(2))
    this.courseCompletionData = []
    this.certificationCompletionData = []
    this.courseCompletionData.push.apply(this.courseCompletionData, [
      this.roleDetails.course_comp,
      100 - this.roleDetails.course_comp,
    ])
    const certificationCompletion = this.roleDetails.certification_comp
      ? this.roleDetails.certification_comp
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
    // org wide course data
    this.roleDetails.role_quotient_details.skills.forEach((role: any) => {
      role.available_course.map((cur: any, i: any) => {
        const others: any = role.available_course_org_data[cur.lex_id]
        if (others === undefined) {
          return
        }
        const obj: IGraphDataCourse = {
          name: cur.content_name,
          id: cur.lex_id,
          materialUrl: cur.material_url,
          totalUsers: others[3].doc_count || 0,
          legend: i === 0 ? true : false,
          data: [
            {
              y: others[0].doc_count || 0,
            },
            {
              y: others[1].doc_count || 0,
            },
            {
              y: others[2].doc_count || 0,
            },
            {
              y: others[3].doc_count || 0,
            },
          ],
        }
        this.availableCourseOrgPieData.push(obj)
      })

      // org wide certification data
      role.available_certification.map((cur: any, i: any) => {
        const others: any = role.available_certification_org_data[cur.lex_id]
        if (others === undefined) {
          return
        }
        const obj: IGraphDataCourse = {
          name: cur.content_name,
          id: cur.lex_id,
          materialUrl: cur.material_url,
          totalUsers: others[1].doc_count || 0,
          legend: i === 0 ? true : false,
          data: [
            {
              y: others[0].doc_count || 0,
            },
            {
              y: others[1].doc_count || 0,
            },
          ],
        }
        this.availableCertificationOrgPieData.push(obj)
      })
    })
  }
  onClick(skillId: any) {
    this.router.navigate(['/app/profile/skills/skill-details', skillId])
  }

  private convertToCardData(content: NsSkills.IRecommendedSkill | NsSkills.ISkill): ICardSkill {
    return {
      category: content.category,
      certificationCount: content.certification_count || 0,
      courseCount: content.course_count || 0,
      horizon: content.horizon,
      group: content.skill_group,
      level: content.skill_level,
      user_level: content.user_level,
      progress: content.progress,
      id: `${content.skill_id}`,
      imageUrl: content.image_url,
      navigationUrl: `/app/profile/skills/skill-details/${content.skill_id}`,
      title: content.skill_name,
    }
  }
}
