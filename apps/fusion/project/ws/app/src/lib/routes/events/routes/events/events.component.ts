import { Component, OnInit } from '@angular/core'
import { NSDiscussData } from '../../../discuss/models/discuss.model'
import { ActivatedRoute, Router } from '@angular/router'
import { FormControl } from '@angular/forms'
import { DiscussService } from '../../../discuss/services/discuss.service'

@Component({
  selector: 'ws-app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.scss'],
})
export class EventsComponent implements OnInit {
  data!: NSDiscussData.IDiscussionData
  queryControl = new FormControl('')
  currentFilter = 'timestamp'
  pager = {}
  paginationData!: any
  currentActivePage!: any
  categoryId!: any
  fetchNewData = false

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private discussService: DiscussService,
  ) {
    console.log('here in app / events')
    this.data = this.route.snapshot.data.topics.data
    this.paginationData = this.data.pagination
    this.categoryId = this.route.snapshot.data['eventsCategoryId'] || 1
  }

  ngOnInit() {
    this.route.queryParams.subscribe(x => {
      this.currentActivePage = x.page || 1
      this.refreshData(this.currentActivePage)
    })
  }

  filter(key: string | 'timestamp' | 'viewcount') {
    if (key) {
      this.currentFilter = key
      this.refreshData(this.currentActivePage)
    }
  }
  updateQuery(key: string) {
    if (key) {

    }
  }

  refreshData(page: any) {
    if (this.fetchNewData) {
      if (this.currentFilter === 'timestamp') {
        this.discussService.fetchSingleCategoryDetails(this.categoryId, page).subscribe(
          (data: any) => {
            this.data = data
            this.paginationData = data.pagination
          },
          (_err: any) => {
          })
      } else {
        this.discussService.fetchSingleCategoryDetailsSort(this.categoryId, 'voted', page).subscribe(
          (data: any) => {
            this.data = data
            this.paginationData = data.pagination
          },
          (_err: any) => {
          })
      }
    }
  }

  navigateWithPage(page: any) {
    if (page !== this.currentActivePage) {
      this.router.navigate([`/app/event-hub/home`], { queryParams: { page } })
      this.fetchNewData = true
    }
  }

}
