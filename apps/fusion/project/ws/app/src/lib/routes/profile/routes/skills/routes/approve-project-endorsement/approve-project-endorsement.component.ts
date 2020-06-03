import { Component, OnInit } from '@angular/core'
import { SkillsService } from '../../services/skills.service'
import { TFetchStatus } from '@ws-widget/utils'
import { MatDialog } from '@angular/material'
import { RejectEndorsementRequestComponent } from '../../components/reject-endorsement-request/reject-endorsement-request.component'
import { ApproveEndorsementRequestComponent } from '../../components/approve-endorsement-request/approve-endorsement-request.component'
import { NsSkills } from '../../models/skills.model'
import { ICardSkill } from '../../components/card-skill/card-skill.component'
import { ActivatedRoute } from '@angular/router'

@Component({
  selector: 'ws-app-approve-project-endorsement',
  templateUrl: './approve-project-endorsement.component.html',
  styleUrls: ['./approve-project-endorsement.component.scss'],
})
export class ApproveProjectEndorsementComponent implements OnInit {
  apiFetchStatus: TFetchStatus = 'fetching'
  type = 'manager'
  pendingFilter = false
  approvedFilter = false
  allFilter = false
  endorsementList: any
  isClient = this.activatedRoute.snapshot.data.pageData.data.enabledTabs.skills.subTabs.allSkills.client
  constructor(
    private skillsSvc: SkillsService,
    public dialog: MatDialog,
    private activatedRoute: ActivatedRoute,
  ) { }

  ngOnInit() {
    this.getList()
  }
  getList() {
    this.apiFetchStatus = 'fetching'
    this.skillsSvc.endorsementList(this.type).subscribe(
      (list: any) => {
        this.endorsementList = (list.all_request || []).map((content: any) => this.convertToCardData(content))
        this.apiFetchStatus = this.endorsementList.length ? 'done' : 'none'
      },
      () => {
        this.apiFetchStatus = 'error'
      })
  }
  private convertToCardData(content: NsSkills.IRecommendedSkill | NsSkills.ISkill): ICardSkill {
    return {
      level: content.skill_level,
      levelId: content.skill_level_id,
      id: `${content.skill_id}`,
      imageUrl: content.image_url,
      navigationUrl: `/app/profile/skills/skill-details/${content.skill_id}`,
      title: content.skill_name,
      status: content.status,
      endorsementData: content,
    }
  }
  onFilter(type: string) {
    this.apiFetchStatus = 'fetching'
    if (type === 'pending') {
      this.skillsSvc.endorsementList(this.type).subscribe(
      (list: any) => {
          this.endorsementList = (list.pending_request || []).map((content: any) => this.convertToCardData(content))
        this.apiFetchStatus = this.endorsementList.length ? 'done' : 'none'
      },
      () => {
        this.apiFetchStatus = 'error'
      })
    } else if (type === 'approved') {
      this.skillsSvc.endorsementList(this.type).subscribe(
      (list: any) => {
          this.endorsementList = (list.approved_request || []).map((content: any) => this.convertToCardData(content))
        this.apiFetchStatus = this.endorsementList.length ? 'done' : 'none'
      },
      () => {
        this.apiFetchStatus = 'error'
      })

    } else {
        this.skillsSvc.endorsementList(this.type).subscribe(
        (list: any) => {
          this.endorsementList = (list.all_request || []).map((content: any) => this.convertToCardData(content))
          this.apiFetchStatus = this.endorsementList.length ? 'done' : 'none'
        },
        () => {
          this.apiFetchStatus = 'error'
        })
      }
  }
  callFilterEvent() {
    this.getList()
  }
  openRejectDialog(request: any) {
    const dialogRef = this.dialog.open(RejectEndorsementRequestComponent, {
      data: request,
    })
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result === 'rejected') {
        setTimeout(() => this.getList(), 1500)
      }
    })
  }
  openApproveDialog(request: any) {
    const dialogRef = this.dialog.open(ApproveEndorsementRequestComponent, {
      data: request,
    })
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result === 'approved') {
        setTimeout(() => this.getList(), 1500)
      }
    })
  }
}
