import { Component, OnInit } from '@angular/core'
import { ValueService } from '../../../../../../../../../library/ws-widget/utils/src/public-api'
import { Subscription } from 'rxjs'
import { MatDialog } from '@angular/material'
import { ViewEventsComponent } from '../view-events/view-events.component'
import { ActivatedRoute } from '@angular/router'

@Component({
  selector: 'ws-app-pathfinders-calendar',
  templateUrl: './pathfinders-calendar.component.html',
  styleUrls: ['./pathfinders-calendar.component.scss'],
})
export class PathfindersCalendarComponent implements OnInit {

  constructor(
    private valueSvc: ValueService,
    public dialog: MatDialog,
    public activatedRoute: ActivatedRoute
  ) { }

  data: any
  isLtMedium$ = this.valueSvc.isLtMedium$
  screenSizeIsLtMedium = false
  mobileSubscription: Subscription | null = null

  ngOnInit() {
    this.mobileSubscription = this.isLtMedium$.subscribe((isLtMedium: boolean) => {
      this.screenSizeIsLtMedium = isLtMedium
    })
    this.data = this.activatedRoute.snapshot.data.searchPageData.data
  }

  openDetails(data: any) {
    const dialogRef = this.dialog.open(ViewEventsComponent, {
      data: data.events,
      width: '300px',
    })
    dialogRef.afterClosed().subscribe()
  }
}
