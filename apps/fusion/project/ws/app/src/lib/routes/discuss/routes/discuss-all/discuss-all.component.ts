
import { Component, OnInit } from '@angular/core'
import { NSDiscussData } from '../../models/discuss.model'
import { MatDialog } from '@angular/material/dialog'
import { DiscussStartComponent } from '../../components/discuss-start/discuss-start.component'
import { ActivatedRoute } from '@angular/router'
import { DiscussService } from '../../services/discuss.service'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */

@Component({
  selector: 'app-discuss-all',
  templateUrl: './discuss-all.component.html',
  styleUrls: ['./discuss-all.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 margin-top-l' },
  /* tslint:enable */
})
export class DiscussAllComponent implements OnInit {
  currentFilter = 'recent'
  trendingTags!: NSDiscussData.ITag[]
  discussionList!: NSDiscussData.IDiscussionData[]
  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private discussService: DiscussService
  ) {
    this.trendingTags = this.route.snapshot.data.availableTags.data.tags
    this.discussionList = this.route.snapshot.data.recent.data.topics || []
  }
  ngOnInit() {
    // this.fillDummyData()
    // console.log(this.discussionList)
  }
  start() {
    // const dialogRef =
    this.dialog.open(DiscussStartComponent, {
      minHeight: 'auto',
      width: '80%',
      panelClass: 'remove-pad',
    })
    // dialogRef.afterClosed().subscribe((response: any) => {
    //   console.log(response)

    // })
  }
  filter(key: string | 'recent' | 'popular' | 'watched') {
    if (key) {
      this.currentFilter = key
    }
    switch (key) {
      case 'recent':
        this.fillrecent()
        break
      case 'popular':
        this.fillPopular()
        break
      default:
        break
    }
  }
  fillrecent() {
    this.discussionList = this.route.snapshot.data.recent.data.topics || []
  }
  fillPopular() {
    // this.discussionList =;
    this.discussService.fetchPopularD().subscribe((response: any) => {
      this.discussionList = _.get(response, 'topics')
    })
    // , () => {
    //   // IN TROUBL
    // })
  }
}
