import { Component, OnInit, Input, ViewChild, ElementRef, Output, EventEmitter } from '@angular/core'
import { NSNetworkDataV2 } from '../../models/network-v2.model'
import { NetworkV2Service } from '../../services/network-v2.service'
import { MatSnackBar } from '@angular/material'
import { Router } from '@angular/router'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'

@Component({
  selector: 'ws-app-connection-search-card',
  templateUrl: './connection-search-card.component.html',
  styleUrls: ['./connection-search-card.component.scss'],
})
export class ConnectionSearchCardComponent implements OnInit {
  @Input() user!: NSNetworkDataV2.IAutocompleteUser
  @Output() connection = new EventEmitter<string>()
  @ViewChild('toastSuccess', { static: true }) toastSuccess!: ElementRef<any>
  @ViewChild('toastError', { static: true }) toastError!: ElementRef<any>

  constructor(
    private networkV2Service: NetworkV2Service,
    private snackBar: MatSnackBar,
    private router: Router,
    private configSvc: ConfigurationsService
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
    if (this.configSvc.userProfile && this.configSvc.userProfile.userId === this.user.wid) {
      this.openSnackbar('Cannot send request to yourself')
    } else {
      // const req = { connectionId: this.user.wid }

      const req = {
        connectionId: this.user.id,
        userNameFrom: this.configSvc.userProfileV2 ? this.configSvc.userProfileV2.userName : '',
        userDepartmentFrom: this.configSvc.userProfileV2 ? this.configSvc.userProfileV2.departmentName : 'iGOT',
        userIdTo: this.user.id,
        userNameTo: `${this.user.personalDetails.firstname}${this.user.personalDetails.surname}`,
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
  }

  private openSnackbar(primaryMsg: string, duration: number = 5000) {
    this.snackBar.open(primaryMsg, 'X', {
      duration,
    })
  }

  goToUserProfile(user: any) {
    this.router.navigate(['/app/person-profile', (user.wid)])
    // this.router.navigate(['/app/person-profile'], { queryParams: { emailId: } })

  }

}
