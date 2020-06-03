import { Component, OnInit, Input } from '@angular/core'
import { IHallOfFameItem } from '../../models/leaderboard.model'

@Component({
  selector: 'ws-app-hall-of-fame-item',
  templateUrl: './hall-of-fame-item.component.html',
  styleUrls: ['./hall-of-fame-item.component.scss'],
})
export class HallOfFameItemComponent implements OnInit {
  @Input() hallOfFameItem!: IHallOfFameItem

  constructor() {}

  ngOnInit() {}
}
