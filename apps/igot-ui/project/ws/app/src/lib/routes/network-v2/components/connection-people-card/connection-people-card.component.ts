import { Component, OnInit, Input, ViewChild, ElementRef, Output, EventEmitter } from '@angular/core'
import { NSNetworkDataV2 } from '../../models/network-v2.model'
import { NetworkV2Service } from '../../services/network-v2.service'
import { MatSnackBar } from '@angular/material'
import { Router } from '@angular/router'
import { ConfigurationsService } from '@ws-widget/utils'

@Component({
  selector: 'ws-app-connection-people-card',
  templateUrl: './connection-people-card.component.html',
  styleUrls: ['./connection-people-card.component.scss'],
})
export class ConnectionPeopleCardComponent implements OnInit {
  @Input() user!: NSNetworkDataV2.INetworkUser
  @Output() connection = new EventEmitter<string>()
  @ViewChild('toastSuccess', { static: true }) toastSuccess!: ElementRef<any>
  @ViewChild('toastError', { static: true }) toastError!: ElementRef<any>
  constructor(
    private networkV2Service: NetworkV2Service, private configSvc: ConfigurationsService,
    private snackBar: MatSnackBar,
    private router: Router
  ) { }

  ngOnInit() {
  }

  getUseravatarName() {
    if (this.user) {
      return `${this.user.personalDetails.firstname} ${this.user.personalDetails.surname}`
    }
    return ''
  }
  connetToUser() {
    const req = {
      connectionId: this.user.id,
      userNameFrom: this.configSvc.userProfile ? this.configSvc.userProfile.userName : '',
      userDepartmentFrom: 'iGOT',
      userIdTo: this.user.id,
      userNameTo: `${this.user.personalDetails.firstname}${this.user.personalDetails.lasttname}`,
      userDepartmentTo: this.user.employmentDetails.departmentName,
    }

    this.networkV2Service.createConnection(req).subscribe(
      () => {
        this.openSnackbar(this.toastSuccess.nativeElement.value)
        this.connection.emit('connection-updated')
      },
      () => {
        this.openSnackbar(this.toastError.nativeElement.value)
      })
  }

  private openSnackbar(primaryMsg: string, duration: number = 5000) {
    this.snackBar.open(primaryMsg, 'X', {
      duration,
    })
  }

  goToUserProfile(user: any) {
    this.router.navigate(['/app/person-profile', (user.userId || user.id)])
    // this.router.navigate(['/app/person-profile'], { queryParams: { emailId: } })

  }

}
