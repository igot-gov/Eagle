import { Component, EventEmitter, Input, OnInit, Output, ViewChild, ElementRef } from '@angular/core'
import { Router } from '@angular/router'
import { ConfigurationsService, TFetchStatus } from '@ws-widget/utils'
import { SkillsService } from '../../services/skills.service'
import { MatSnackBar, MatDialog } from '@angular/material'
import { RejectEndorsementRequestComponent } from '../reject-endorsement-request/reject-endorsement-request.component'
import { ApproveEndorsementRequestComponent } from '../approve-endorsement-request/approve-endorsement-request.component'

export interface ICardSkill {
  category?: string
  certificationCount?: number
  courseCount?: number
  horizon?: string
  group?: string
  level?: string
  user_level?: string
  id: string
  imageUrl?: string
  navigationUrl?: string
  title: string
  progress?: string
  skills?: string[]
  status?: string
  relevance?: string
  manager?: string
  description?: string
  levelId?: string
  endorsementData?: any
}

@Component({
  selector: 'ws-app-card-skill',
  templateUrl: './card-skill.component.html',
  styleUrls: ['./card-skill.component.scss'],
})
export class CardSkillComponent implements OnInit {
  @Input() cardData!: ICardSkill
  @Input() type!: string
  @Input() skillManager!: boolean
  @Input() owner!: boolean
  @Input() isClient!: boolean
  @Input() enableEndorsement!: boolean
  @Input() approveEndorsement!: boolean
  @Input() roleCard!: boolean
  @Input() mySkills!: boolean
  @Input() allSkills!: boolean
  @Output() clickEvent = new EventEmitter()
  apiFetchStatus: TFetchStatus = 'fetching'
  skillClientData: any
  skillQuotientData: any
  skillDetails: any
  fetchImage = false
  defaultThumbnail = ''
  showApprovedDetails = false
  showAddDetails = false
  showPendingDetails = false
  showApprovedForDetails = false
  showPendingForApprovalDetails = false
  showEndorseDetails = false
  selectedLevel = ''
  skillLevel = ''
  @ViewChild('existsContent', { static: true })
  existsContentMessage!: ElementRef<any>
  skillLevelList = ['Basic', 'Advanced', 'Expert']
  constructor(
    private configSvc: ConfigurationsService,
    private router: Router,
    private skillSrv: SkillsService,
    private snackBar: MatSnackBar,
    public dialog: MatDialog,
  ) { }

  ngOnInit() {
    const instanceConfig = this.configSvc.instanceConfig
    if (instanceConfig) {
      this.defaultThumbnail = instanceConfig.logos.defaultContent
      // //console.log('instanceConfig logos >', instanceConfig.logos.defaultContent)
    }
    this.fetchImage = true
    if (this.cardData.level) {
      this.skillLevel = this.cardData.level
    } else {
      this.skillLevel = 'Basic'
    }
  }
  onCardClick(id: any) {
    this.clickEvent.emit(id)
    this.router.navigate(['/app/profile/skills/skill-details', id])
  }
  onClickRole(action: string, cardId: any) {
    if (action === 'edit') {
      this.router.navigate(['/app/profile/skills/edit-skill', cardId])
    } else if (action === 'endorse') {
      this.router.navigate(['/app/profile/skills/create-endorsement'], {
        queryParams: { id: cardId },
      })
    } else {
      this.router.navigate(['/app/profile/skills/copy-skill', cardId])
    }
  }
  clickIcon(skillId: any, skillName: string, type: string) {
    if (type === 'add') {
      this.addToShortList(skillId, skillName)
      this.showAddDetails = true
    } else if (type === 'endorse') {
      this.skillQuotientDetails(skillId)
      this.showEndorseDetails = true
    } else if (type === 'pending' && this.enableEndorsement) {
      this.showPendingDetails = true
    } else if (type === 'pending' && this.approveEndorsement) {
      this.showPendingForApprovalDetails = true
    } else if (type === 'approved' && this.enableEndorsement) {
      this.showApprovedDetails = true
    } else if (type === 'approved' && this.approveEndorsement) {
      this.showApprovedForDetails = true
    }
  }
  onClose(type: string) {
    if (type === 'add') {
      this.showAddDetails = false
    } else if (type === 'endorse') {
      this.showEndorseDetails = false
    } else if (type === 'pending' && this.enableEndorsement) {
      this.showPendingDetails = false
    } else if (type === 'pending' && this.approveEndorsement) {
      this.showPendingForApprovalDetails = false
    } else if (type === 'approved' && this.enableEndorsement) {
      this.showApprovedDetails = false
    } else if (type === 'approved' && this.approveEndorsement) {
      this.showApprovedForDetails = false
    }
  }
  requestEndorsement(skillId: any) {
    if (this.selectedLevel !== undefined && this.selectedLevel !== '') {
      this.router.navigate(['/app/profile/skills/create-endorsement', skillId, this.selectedLevel])

    } else {
      this.snackBar.open(this.existsContentMessage.nativeElement.value)
    }
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

  openRejectDialog(request: any) {
    const dialogRef = this.dialog.open(RejectEndorsementRequestComponent, {
      data: request,
    })
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result === 'rejected') {
        setTimeout(() => this.clickEvent.emit(), 1500)
      }
    })
  }
  openApproveDialog(request: any) {
    const dialogRef = this.dialog.open(ApproveEndorsementRequestComponent, {
      data: request,
    })
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result === 'approved') {
        setTimeout(() => this.clickEvent.emit(), 1500)
      }
    })
  }
  skillQuotientDetails(skillId: any) {
    this.apiFetchStatus = 'fetching'
    this.skillSrv.skillQuotient(skillId).subscribe(response => {
      if (this.isClient) {
        this.skillClientData = response
        this.skillQuotientData = []
        this.skillClientData.skill_quotient.skill_levels.forEach((level: any) => {
          // tslint:disable-next-line:max-line-length
          if (level.skill_level === 'Basic') {
            this.skillQuotientData.push(level)
          }
        })
        this.skillClientData.skill_quotient.skill_levels.forEach((level: any) => {
          // tslint:disable-next-line:max-line-length
          if (level.skill_level === 'Advanced') {
            this.skillQuotientData.push(level)
          }
        })
        this.skillClientData.skill_quotient.skill_levels.forEach((level: any) => {
          // tslint:disable-next-line:max-line-length
          if (level.skill_level === 'Expert') {
            this.skillQuotientData.push(level)
          }
        })
        this.apiFetchStatus = 'done'
      } else {
        this.skillDetails = response
        // if (this.skillDetails.prerequisites) {
        //   this.preRequisites = (this.skillDetails.prerequisites || []).map((content: any) =>
        //     this.convertToCardData(content),
        //   )
        // }
      }
      this.apiFetchStatus = 'done'
    })
  }
}
