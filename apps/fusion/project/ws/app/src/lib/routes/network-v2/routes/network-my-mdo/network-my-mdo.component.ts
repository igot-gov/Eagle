import { Component, OnInit } from '@angular/core'
import { NSNetworkDataV2 } from '../../models/network-v2.model'
import { FormControl } from '@angular/forms'
import { ActivatedRoute } from '@angular/router'
import { NetworkV2Service } from '../../services/network-v2.service'

@Component({
  selector: 'ws-app-network-my-mdo',
  templateUrl: './network-my-mdo.component.html',
  styleUrls: ['./network-my-mdo.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 mt-4' },
  /* tslint:enable */
})
export class NetworkMyMdoComponent implements OnInit {

  data!: NSNetworkDataV2.INetworkUser[]
  queryControl = new FormControl('')
  currentFilter = 'timestamp'
  currentFilterSort = 'desc'
  constructor(
    private route: ActivatedRoute,
    private networkV2Service: NetworkV2Service,
  ) {
    // console.log('this.route.snapshot.data.myMdoList.data :', this.route.snapshot.data.myMdoList.data)
    this.data = this.route.snapshot.data.myMdoList.data.result.data.
    find((item: any) => item.field === 'employmentDetails.departmentName').results
    // console.log('this.data : ', this.data)
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
