
import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core'
// import { NSProfileDataV2 } from '../../models/profile-v2.model'
// import { MatDialog } from '@angular/material/dialog'
import { Router } from '@angular/router'
// import { ConfigurationsService } from '@ws-widget/utils/src/public-api'
import { ApprovalsService } from '../../services/approvals.service'
import lodash from 'lodash'
import moment from 'moment'
@Component({
  selector: 'ws-app-approvals',
  templateUrl: './approvals.component.html',
  styleUrls: ['./approvals.component.scss'],
})
export class ApprovalsComponent implements OnInit, AfterViewInit, OnDestroy {
  data: any = []
  tabledata: any = []
  currentFilter = 'toapprove'
  discussionList!: any
  discussProfileData!: any

  constructor(private router: Router, private apprService: ApprovalsService) { }

  ngOnDestroy(): void {
    // throw new Error('Method not implemented.')
  }
  ngAfterViewInit(): void {
    // throw new Error('Method not implemented.')
  }
  ngOnInit() {
    this.tabledata = {
      actions: [{ name: 'Approve', label: 'Approve', icon: 'remove_red_eye', type: 'Approve' },
      { name: 'Reject', label: 'Reject', icon: 'remove_red_eye', type: 'Reject' }],
      columns: [
        { displayName: 'Full Name', key: 'fullname' },
        { displayName: 'Requested on', key: 'requestedon' },
        { displayName: 'Fields', key: 'fields' },
      ],
      needCheckBox: false,
      needHash: false,
      sortColumn: '',
      sortState: 'asc',
    }
    // this.data = [{
    //   fullname: 'DevaPrathap Nagendra',
    //   requestedon: new Date(),
    //   fields: 'MDo,Period,Position',
    // },
    // {
    //   fullname: 'Nancy Jimenez',
    //   requestedon: new Date(),
    //   fields: 'Period,Position',
    // },
    // {
    //   fullname: 'Madison Tran',
    //   requestedon: new Date(),
    //   fields: 'Period,Position',
    // }]

    this.fetchApprovals()
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
          this.discussionList = _.uniqBy(this.discussProfileData.latestPosts, 'tid')
          break
      }
    }
  }

  onApprovalClick(approval: any) {
    this.router.navigate([`/app/approvals/${approval.fullname}/to-approve`],
      {
        queryParams: { updatedFileds: JSON.stringify(approval.updatedFileds) },
      }
    )
  }

  fetchApprovals() {
    const req = {
      serviceName: 'profile',
      applicationStatus: 'SEND_FOR_APPROVAL',
      offset: 0,
      limit: 10,
    }
    this.apprService.getApprovals(req).subscribe(res => {
      this.data = res.result.data.map((approval: any) => {
        const currentdate = new Date(approval.wfInfo.createdOn)
        const fileds = JSON.parse(approval.wfInfo.updateFieldValues).map((field: any) => {
          return field.fieldKey
        })
        const fullName = approval.userInfo ? `${approval.userInfo.first_name} ${approval.userInfo.last_name}` : null
        return {
          fullname: fullName,
          requestedon: `${currentdate.getDate()}
            ${moment(currentdate.getMonth() + 1, 'MM').format('MMM')}
            ${currentdate.getFullYear()}
            ${currentdate.getHours()} :
             ${currentdate.getMinutes()} :
             ${currentdate.getSeconds()}`,
          fields: fileds,
          updatedFileds: JSON.parse(approval.wfInfo.updateFieldValues),
        }
      })
    })
  }
}
