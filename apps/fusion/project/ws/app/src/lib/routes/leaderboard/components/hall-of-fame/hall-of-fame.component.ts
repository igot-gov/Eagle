import { Component, OnInit } from '@angular/core'
import { IResolveResponse } from '@ws-widget/utils'
import { IHallOfFameItem, EDurationTypeRouteParam } from '../../models/leaderboard.model'
import { ActivatedRoute } from '@angular/router'

@Component({
  selector: 'ws-app-hall-of-fame',
  templateUrl: './hall-of-fame.component.html',
  styleUrls: ['./hall-of-fame.component.scss'],
})
export class HallOfFameComponent implements OnInit {
  hallOfFame!: IHallOfFameItem[]
  hallOfFameError!: string
  durationType!: EDurationTypeRouteParam

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.data.subscribe(data => {
      const hallOfFameResolve: IResolveResponse<IHallOfFameItem[]> = data.hallOfFameResolve

      if (hallOfFameResolve.data) {
        this.hallOfFame = hallOfFameResolve.data
      }

      if (hallOfFameResolve.error) {
        this.hallOfFameError = hallOfFameResolve.error
      }
    })
  }
}
