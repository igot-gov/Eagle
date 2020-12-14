import { Component, OnInit } from '@angular/core'
import { NeedApprovalsService } from '../../services/need-approvals.service'

@Component({
  selector: 'ws-app-education',
  templateUrl: './education.component.html',
  styleUrls: ['./education.component.scss'],
})
export class EducationComponent implements OnInit {
  profileDetails: any
  constructor(private needApprService: NeedApprovalsService) { }

  ngOnInit() {
    this.fetchProfileDetails()
  }

  fetchProfileDetails() {
    this.needApprService.fetchProfileDeatils().subscribe(res => {
      this.profileDetails = res.result.UserProfile[0]
    })
  }
}
