import { Component, OnInit } from '@angular/core'
import { NSNetworkDataV2 } from '../../models/network-v2.model'
import { FormControl } from '@angular/forms'
import { ActivatedRoute } from '@angular/router'
import { NetworkV2Service } from '../../services/network-v2.service'

@Component({
  selector: 'ws-app-network-connection-requests',
  templateUrl: './network-connection-requests.component.html',
  styleUrls: ['./network-connection-requests.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 mt-6 ' },
  /* tslint:enable */
})
export class NetworkConnectionRequestsComponent implements OnInit {
  data!: NSNetworkDataV2.INetworkUser[]
  queryControl = new FormControl('')
  currentFilter = 'timestamp'
  currentFilterSort = 'desc'
  constructor(
    private route: ActivatedRoute,
    private networkV2Service: NetworkV2Service,
  ) {
    this.data = this.route.snapshot.data.connectionRequests.data.result.data
    this.data = this.data.map((v: NSNetworkDataV2.INetworkUser) => {
      if (v && v.personalDetails && v.personalDetails.firstname) {
        v.personalDetails.firstname = v.personalDetails.firstname.toLowerCase()
      }
      return v
    })
   }

  ngOnInit() {
  }

  updateQuery(key: string) {
    if (key) {

    }
  }

  filter(key: string, order: string | 'asc' | 'desc') {
    if (key) {
      this.currentFilter = key
      this.currentFilterSort = order
    }
  }

  connectionUpdate(event: any) {
    if (event === 'connection-updated') {
      this.networkV2Service.fetchAllReceivedConnectionRequests().subscribe(
        (data: any) => {
          this.data = data.result.data
        },
        (_err: any) => {
          // this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
        })
    }
  }

}
