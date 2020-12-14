import { Component, OnInit } from '@angular/core'
import { NeedApprovalsService } from '../../services/need-approvals.service'

@Component({
  selector: 'ws-app-basic-info',
  templateUrl: './basic-info.component.html',
  styleUrls: ['./basic-info.component.scss'],
})
export class BasicInfoComponent implements OnInit {
  basicDetails: any
  constructor(private needApprService: NeedApprovalsService) { }

  ngOnInit() {
    this.fetchProfileDetails()
  }

  fetchProfileDetails() {
    this.needApprService.fetchProfileDeatils().subscribe(res => {
      this.basicDetails = res.result.UserProfile[0].personalDetails
    })
  }
}
