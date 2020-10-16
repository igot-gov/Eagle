import { Component, OnInit, Input, ViewChild, ElementRef } from '@angular/core'
import { Router } from '@angular/router'
import { NSNetworkDataV2 } from '../../models/network-v2.model'
import { NetworkV2Service } from '../../services/network-v2.service'
import { MatSnackBar } from '@angular/material'

@Component({
  selector: 'ws-app-connection-request-card',
  templateUrl: './connection-request-card.component.html',
  styleUrls: ['./connection-request-card.component.scss'],
})
export class ConnectionRequestCardComponent implements OnInit {
  @Input() user!: NSNetworkDataV2.INetworkUser
  @ViewChild('toastAccept', { static: true }) toastAccept!: ElementRef<any>
  @ViewChild('toastReject', { static: true }) toastReject!: ElementRef<any>
  @ViewChild('toastError', { static: true }) toastError!: ElementRef<any>
  constructor(
    private router: Router,
    private networkV2Service: NetworkV2Service,
    private snackBar: MatSnackBar,
  ) { }

  ngOnInit() {
  }

  acceptConnection() {
    this.connetToUser('Approved')
  }

  rejectConnection() {
    this.connetToUser('Rejected')
  }

  goToUserProfile(user: any) {
    this.router.navigate(['/app/person-profile', (user.userId || user.id)])
    // this.router.navigate(['/app/person-profile'], { queryParams: { emailId: } })
  }

  connetToUser(action: string | 'Approved' | 'Rejected') {
    const req = { connectionId: this.user.id, status: action }
    this.networkV2Service.updateConnection(req).subscribe(
      () => {
        if (action === 'accept') {
          this.openSnackbar(this.toastAccept.nativeElement.value)
        } else {
          this.openSnackbar(this.toastReject.nativeElement.value)
        }
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

  getUseravatarName() {
    if (this.user) {
      return `${this.user.personalDetails.firstname} ${this.user.personalDetails.surname}`
    }
      return ''
  }

}
