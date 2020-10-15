import { Component, OnInit } from '@angular/core'
import { NSNetworkDataV2 } from '../../models/network-v2.model'
import { FormControl } from '@angular/forms'
import { ActivatedRoute } from '@angular/router'
import { NetworkV2Service } from '../../services/network-v2.service'

@Component({
  selector: 'ws-app-network-my-connection',
  templateUrl: './network-my-connection.component.html',
  styleUrls: ['./network-my-connection.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 mt-4' },
  /* tslint:enable */
})
export class NetworkMyConnectionComponent implements OnInit {
  data!: NSNetworkDataV2.INetworkUser[]
  queryControl = new FormControl('')
  currentFilter = 'timestamp'
  currentFilterSort = 'desc'
  constructor(
    private route: ActivatedRoute,
    // private networkV2Service: NetworkV2Service,
  ) {
    this.data = this.route.snapshot.data.myConnectionList.data.result.data
   }

  ngOnInit() {
  }

  updateQuery(key: string) {
    if (key) {

    }
  }

  filter(key: string | 'timestamp' | 'alphabetical', order: string | 'asc' | 'desc') {
    if (key) {
      this.currentFilter = key
      this.currentFilterSort = order
    }
  }

}
