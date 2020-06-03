import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core'
import { ILeaderboard, ILeaderboardPrevNext } from '../../models/leaderboard.model'

@Component({
  selector: 'ws-app-leaderboard-controls',
  templateUrl: './leaderboard-controls.component.html',
  styleUrls: ['./leaderboard-controls.component.scss'],
})
export class LeaderboardControlsComponent implements OnInit {
  @Input() leaderboard!: ILeaderboard
  @Input() disabledPrev!: boolean
  @Input() disabledNext!: boolean
  @Output() fetchLeaderboard: EventEmitter<ILeaderboardPrevNext>

  constructor() {
    this.fetchLeaderboard = new EventEmitter()
  }

  ngOnInit() { }

  onClickBtnPrevNext(prevNext?: ILeaderboardPrevNext) {
    if (!prevNext) {
      return
    }

    this.fetchLeaderboard.emit(prevNext)
  }
}
