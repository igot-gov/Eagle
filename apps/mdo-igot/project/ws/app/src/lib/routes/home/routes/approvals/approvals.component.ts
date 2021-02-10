
import { Component, OnDestroy, OnInit } from '@angular/core'
import { ActivatedRoute, Router } from '@angular/router'
import { ApprovalsService } from '../../services/approvals.service'
import moment from 'moment'
import { ITableData } from '@ws-widget/collection'
import { MatSnackBar } from '@angular/material'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */
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
  departName = ''
  tabledata: ITableData = {
    // actions: [{ name: 'Approve', label: 'Approve', icon: 'remove_red_eye', type: 'Approve' },
    // { name: 'Reject', label: 'Reject', icon: 'remove_red_eye', type: 'Reject' }],
    actions: [],
    columns: [
      { displayName: 'Full Name', key: 'fullname' },
      { displayName: 'Requested on', key: 'requestedon' },
      { displayName: 'Fields', key: 'fields', isList: true },
    ],
    needCheckBox: false,
    needHash: false,
    sortColumn: 'fullname',
    sortState: 'asc',
    needUserMenus: false,
  }

  constructor(
    private router: Router,
    private apprService: ApprovalsService,
    private activeRouter: ActivatedRoute,
    private snackbar: MatSnackBar) {
    if (this.activeRouter.parent && this.activeRouter.parent.snapshot.data.department.data.deptName
    ) {
      this.departName = _.get(this.activeRouter, 'parent.snapshot.data.department.data.deptName')
    }
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
    if (approval && approval.userWorkflow.userInfo) {
      this.router.navigate([`/app/approvals/${approval.userWorkflow.userInfo.wid}/to-approve`])
    }
  }

  fetchApprovals() {
    if (this.departName) {
      const req = {
        serviceName: 'profile',
        applicationStatus: 'SEND_FOR_APPROVAL',
        deptName: this.departName,
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
                  keys += `${_.first(Object.keys(field.fromValue))}, `
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
    } else {
      this.snackbar.open('Please connect to your SPV admin, to update MDO name.')
    }
  }

  get getTableData() {
    return this.data
  }

  ngOnDestroy(): void { }
}
