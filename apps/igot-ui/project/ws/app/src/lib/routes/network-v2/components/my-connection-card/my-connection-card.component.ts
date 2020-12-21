import { Component, OnInit, Input } from '@angular/core'
import { Router } from '@angular/router'
import { NSNetworkDataV2 } from '../../models/network-v2.model'

@Component({
  selector: 'ws-app-my-connection-card',
  templateUrl: './my-connection-card.component.html',
  styleUrls: ['./my-connection-card.component.scss'],
})
export class MyConnectionCardComponent implements OnInit {
  @Input() user!: NSNetworkDataV2.INetworkUser
  constructor(
    private router: Router
  ) { }

  ngOnInit() {
  }

  goToUserProfile(user: any) {
    this.router.navigate(['/app/person-profile', (user.userId || user.id)])
    // this.router.navigate(['/app/person-profile'], { queryParams: { emailId: } })

  }

  getUseravatarName() {
    if (this.user) {
      return `${this.user.name}`
    }
      return ''
  }

}
