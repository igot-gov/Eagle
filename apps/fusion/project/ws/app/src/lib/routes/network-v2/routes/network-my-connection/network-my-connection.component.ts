import { Component, OnInit } from '@angular/core'
import { NSNetworkDataV2 } from '../../models/network-v2.model'
import { FormControl } from '@angular/forms'
import { ActivatedRoute } from '@angular/router'

@Component({
  selector: 'ws-app-network-my-connection',
  templateUrl: './network-my-connection.component.html',
  styleUrls: ['./network-my-connection.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 mt-6 ' },
  /* tslint:enable */
})
export class NetworkMyConnectionComponent implements OnInit {
  data!: NSNetworkDataV2.INetworkUser[]
  queryControl = new FormControl('')
  currentFilter = 'timestamp'
  currentFilterSort = 'desc'
  enableSearchFeature = false
  constructor(
    private route: ActivatedRoute,
  ) {
    // this.data = this.route.snapshot.data.myConnectionList.data.result.data
    this.data = this.route.snapshot.data.myConnectionList.data.result.data.map((v: NSNetworkDataV2.INetworkUser) => {
      if (v && v.personalDetails && v.personalDetails.firstname) {
        v.personalDetails.firstname = v.personalDetails.firstname.toLowerCase()
      }
      return v
    })
  }

  ngOnInit() {
    this.queryControl.valueChanges.subscribe(val => {
      if (val.length === 0) {
        this.enableSearchFeature = false
      } else {
        this.enableSearchFeature = true
      }
    })
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

}
