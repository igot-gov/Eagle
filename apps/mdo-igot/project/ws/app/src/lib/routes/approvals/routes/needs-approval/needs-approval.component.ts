import { Component, OnInit } from '@angular/core'
import { NeedApprovalsService } from '../../services/need-approvals.service'
import { ActivatedRoute } from '@angular/router'
@Component({
  selector: 'ws-app-needs-approval',
  templateUrl: './needs-approval.component.html',
  styleUrls: ['./needs-approval.component.scss'],
})
export class NeedsApprovalComponent implements OnInit {
  approvalData!: any[]
  updatedFileds!: any[]
  approvalList: any[] = []
  constructor(private needApprService: NeedApprovalsService,
              private route: ActivatedRoute) {
    this.route.queryParams.subscribe(
      params => {
        this.updatedFileds = JSON.parse(params['updatedFileds'])
      }
    )
    this.updatedFileds.forEach(field => this.approvalList.push(
      Object.assign({
        label: Object.keys(field.toValue)[0], value: field.toValue[Object.keys(field.toValue)[0]],
      })
    ))
  }

  ngOnInit() {
    this.fetchNeedApprovals()
  }

  fetchNeedApprovals() {
    const req = {
      serviceName: 'profile',
      applicationStatus: 'SEND_FOR_APPROVAL',
      offset: 0,
    }
    this.needApprService.fetchNeedApprovals(req).subscribe(res => {
      this.approvalData = res.result.data
      const el = document.getElementById('nav-count-needsapproval')
      if (el != null) {
        const span = document.createElement('span')
        span.innerHTML = `${this.approvalList.length}`
        span.setAttribute('class', 'count')
        el.append(span)
      }
    })
  }
}
