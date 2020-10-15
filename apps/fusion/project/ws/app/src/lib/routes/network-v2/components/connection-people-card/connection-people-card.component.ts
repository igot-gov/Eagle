import { Component, OnInit, Input, ViewChild, ElementRef } from '@angular/core'
import { NSNetworkDataV2 } from '../../models/network-v2.model'
import { NetworkV2Service } from '../../services/network-v2.service'
import { MatSnackBar } from '@angular/material'

@Component({
  selector: 'ws-app-connection-people-card',
  templateUrl: './connection-people-card.component.html',
  styleUrls: ['./connection-people-card.component.scss'],
})
export class ConnectionPeopleCardComponent implements OnInit {
  @Input() user!: NSNetworkDataV2.INetworkUser
  @ViewChild('toastSuccess', { static: true }) toastSuccess!: ElementRef<any>
  @ViewChild('toastError', { static: true }) toastError!: ElementRef<any>
  constructor(
    private networkV2Service: NetworkV2Service,
    private snackBar: MatSnackBar
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
    const req = { connectionId: this.user.id }
    this.networkV2Service.createConnection(req).subscribe(
      () => {
        this.openSnackbar(this.toastSuccess.nativeElement.value)
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

}
