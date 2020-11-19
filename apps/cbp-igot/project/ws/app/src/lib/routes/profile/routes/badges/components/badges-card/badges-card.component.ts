import { Component, OnInit, Input, ElementRef } from '@angular/core'
import { IBadgeRecent } from '../../../badges/badges.model'
import { BadgesShareDialogComponent } from '../badges-share-dialog/badges-share-dialog.component'
import { MatDialog } from '@angular/material'
@Component({
  selector: 'ws-app-badges-card',
  templateUrl: './badges-card.component.html',
  styleUrls: ['./badges-card.component.scss'],
})
export class BadgesCardComponent implements OnInit {
  shareErrorMessage!: ElementRef<any>
  @Input()
  badge!: IBadgeRecent
  constructor(private dialog: MatDialog,
 ) {

  }

  ngOnInit() { }

  openShareBadgesDialog() {
    const dialogRef = this.dialog.open(BadgesShareDialogComponent, {
      data: this.badge,
    })

    dialogRef.afterClosed().subscribe(shared => {
      if (shared) {
        // this.updateGoals.emit()
      }
    })
  }
}
