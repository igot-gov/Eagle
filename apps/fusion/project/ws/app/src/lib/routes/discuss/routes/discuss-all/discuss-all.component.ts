
import { Component, OnInit, ViewChild, ElementRef, HostListener, AfterViewInit } from '@angular/core'
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
export class DiscussAllComponent implements OnInit, AfterViewInit {
  @ViewChild('stickyMenu', { static: true }) menuElement!: ElementRef
  sticky: boolean = false;
  elementPosition: any
  currentFilter = 'recent'
  trendingTags!: NSDiscussData.ITag[]
  discussionList!: NSDiscussData.IDiscussionData[]
  unread: any
  constructor(
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private discussService: DiscussService
  ) {
    this.trendingTags = this.route.snapshot.data.availableTags.data.tags
    this.discussionList = this.route.snapshot.data.recent.data.topics || []
    this.unread = this.route.snapshot.data.unread
  }
  ngOnInit() {
    // this.fillDummyData()
    // console.log(this.discussionList)
  }
  start() {
    const dialogRef = this.dialog.open(DiscussStartComponent, {
      minHeight: 'auto',
      width: '80%',
      panelClass: 'remove-pad',
    })
    dialogRef.afterClosed().subscribe((response: any) => {
      if (response === 'postCreated') {
        this.refreshData()
      }
    })
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
    // this.discussionList = this.route.snapshot.data.recent.data.topics || []
    this.getRecentData()
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

  refreshData() {
    if (this.currentFilter === 'recent') {
      this.getRecentData()
    } else {
      this.fillPopular()
    }
  }

  getRecentData() {
    return this.discussService.fetchRecentD().subscribe(
      data => {
        this.discussionList = _.get(data, 'topics')
      })
  }

  @HostListener('window:scroll', ['$event'])
  handleScroll() {
    const windowScroll = window.pageYOffset
    if (windowScroll >= this.elementPosition + 417) {
      this.sticky = false
    } else {
      this.sticky = false
    }
  }

  ngAfterViewInit() {
    this.elementPosition = this.menuElement.nativeElement.offsetTop
  }
}
