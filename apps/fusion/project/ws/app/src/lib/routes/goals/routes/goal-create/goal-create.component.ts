import { Component, OnInit } from '@angular/core'
import { ConfigurationsService } from '../../../../../../../../../library/ws-widget/utils/src/public-api'

@Component({
  selector: 'ws-app-goal-create',
  templateUrl: './goal-create.component.html',
  styleUrls: ['./goal-create.component.scss'],
})
export class GoalCreateComponent implements OnInit {
  isCommonGoalEnabled = false
  constructor(private configSvc: ConfigurationsService) {}

  ngOnInit() {
    if (this.configSvc.restrictedFeatures) {
      this.isCommonGoalEnabled = !this.configSvc.restrictedFeatures.has('commonGoals')
    }
  }
}
