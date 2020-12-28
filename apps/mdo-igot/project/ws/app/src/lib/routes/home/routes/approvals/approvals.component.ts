
import { Component, OnDestroy, OnInit } from '@angular/core'
import { Router } from '@angular/router'
import { ApprovalsService } from '../../services/approvals.service'
import moment from 'moment'
import { ITableData } from '@ws-widget/collection'
@Component({
  selector: 'ws-app-approvals',
  templateUrl: './approvals.component.html',
  styleUrls: ['./approvals.component.scss'],
})
export class ApprovalsComponent implements OnInit, OnDestroy {
  data: any[] = []
  currentFilter = 'toapprove'
  discussionList!: any
  discussProfileData!: any

  tabledata: ITableData = {
    actions: [{ name: 'Approve', label: 'Approve', icon: 'remove_red_eye', type: 'Approve' },
    { name: 'Reject', label: 'Reject', icon: 'remove_red_eye', type: 'Reject' }],
    columns: [
      { displayName: 'Full Name', key: 'fullname' },
      { displayName: 'Requested on', key: 'requestedon' },
      { displayName: 'Fields', key: 'fields', isList: true },
    ],
    needCheckBox: false,
    needHash: false,
    sortColumn: 'fullname',
    sortState: 'asc',
  }

  constructor(private router: Router, private apprService: ApprovalsService) {
    this.fetchApprovals()
  }

  ngOnInit() {
  }

  filter(key: string | 'timestamp' | 'best' | 'saved') {
    if (key) {
      this.currentFilter = key
      switch (key) {
        case 'toapprove':
          this.fetchApprovals()
          break
        case 'userflags':
          this.data = [{
            fullname: 'Nancy Jimenez',
            requestedon: new Date(),
            fields: 'Period,Position',
          }]
          break

        default:
          break
      }
    }
  }

  onApprovalClick(approval: any) {
    if (approval) {
      this.router.navigate([`/app/approvals/${approval.userWorkflow.userInfo.wid}/to-approve`])
    }
  }

  fetchApprovals() {
    const req = {
      serviceName: 'profile',
      applicationStatus: 'SEND_FOR_APPROVAL',
      offset: 0,
      limit: 100,
    }
    this.apprService.getApprovals(req).subscribe(res => {
      let currentdate: Date
      res.result.data.forEach((approval: any) => {
        let keys = ''
        approval.wfInfo.forEach((wf: any) => {
          currentdate = new Date(wf.createdOn)
          if (typeof wf.updateFieldValues === 'string') {
            const fields = JSON.parse(wf.updateFieldValues)
            if (fields.length > 0) {
              fields.forEach((field: any) => {
                keys += `${field.fieldKey}, `
              })
            }
          }
        })

        this.data.push({
          fullname: approval.userInfo ? `${approval.userInfo.first_name} ${approval.userInfo.last_name}` : null,
          requestedon: `${currentdate.getDate()}
          ${moment(currentdate.getMonth() + 1, 'MM').format('MMM')}
          ${currentdate.getFullYear()}
          ${currentdate.getHours()} :
          ${currentdate.getMinutes()} :
          ${currentdate.getSeconds()}`,
          fields: keys.slice(0, -1),
          userWorkflow: approval,
        })
      })
    })
  }

  get getTableData() {
    return this.data
  }

  ngOnDestroy(): void { }
}
