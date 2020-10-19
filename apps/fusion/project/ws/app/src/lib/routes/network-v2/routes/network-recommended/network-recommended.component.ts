import { Component, OnInit } from '@angular/core'
import { NSNetworkDataV2 } from '../../models/network-v2.model'
import { FormControl } from '@angular/forms'
import { ActivatedRoute } from '@angular/router'
import { NetworkV2Service } from '../../services/network-v2.service'

@Component({
  selector: 'ws-app-network-recommended',
  templateUrl: './network-recommended.component.html',
  styleUrls: ['./network-recommended.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 mt-6 ' },
  /* tslint:enable */
})
export class NetworkRecommendedComponent implements OnInit {
  data!: NSNetworkDataV2.INetworkUser[]
  queryControl = new FormControl('')
  currentFilter = 'timestamp'
  currentFilterSort = 'desc'
  constructor(
    private route: ActivatedRoute,
    private networkV2Service: NetworkV2Service,
  ) {
    this.data = this.route.snapshot.data.recommendedList.data.result.data
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
      this.networkV2Service.fetchAllSuggestedUsers().subscribe(
        (data: any) => {
          this.data = data.result.data
        },
        (_err: any) => {
          // this.openSnackbar(err.error.message.split('|')[1] || this.defaultError)
        })
    }
  }

}
