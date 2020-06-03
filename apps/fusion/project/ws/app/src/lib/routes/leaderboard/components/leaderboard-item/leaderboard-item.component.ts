import { Component, OnInit, Input } from '@angular/core'
import { ILeaderboardItem } from '../../models/leaderboard.model'

@Component({
  selector: 'ws-app-leaderboard-item',
  templateUrl: './leaderboard-item.component.html',
  styleUrls: ['./leaderboard-item.component.scss'],
})
export class LeaderboardItemComponent implements OnInit {
  @Input() leaderboardItem!: ILeaderboardItem

  constructor() { }

  ngOnInit() { }
}
