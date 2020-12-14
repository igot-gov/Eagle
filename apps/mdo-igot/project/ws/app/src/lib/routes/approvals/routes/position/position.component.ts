import { Component, OnInit } from '@angular/core'
import { NeedApprovalsService } from '../../services/need-approvals.service'

@Component({
  selector: 'ws-app-position',
  templateUrl: './position.component.html',
  styleUrls: ['./position.component.scss'],
})
export class PositionComponent implements OnInit {
  positionDetails: any
  constructor(private needApprService: NeedApprovalsService) { }

  ngOnInit() {
    this.fetchProfileDetails()
  }

  fetchProfileDetails() {
    this.needApprService.fetchProfileDeatils().subscribe(res => {
      this.positionDetails = res.result.UserProfile[0].professionalDetails[0]
    })
  }

}
