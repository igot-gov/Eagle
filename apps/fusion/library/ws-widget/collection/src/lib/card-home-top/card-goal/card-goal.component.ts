import { Component } from '@angular/core'

@Component({
  selector: 'ws-widget-card-goal',
  templateUrl: './card-goal.component.html',
  styleUrls: ['./card-goal.component.scss'],

})

export class CardGoalComponent {
  items = [{
    name: 'COVID 2020 Heath and other…',
    value: 40,
    days_left: '5D Left',
  }, {
    name: 'Role of Police & Security Agencies…',
    value: 60,
    days_left: '4D Left',
  }, {
    name: 'Psychological care of patients…',
    value: 50,
    days_left: '3D Left',
  }, {
    name: 'COVID-19 Training for NCC Cadets..',
    value: 80,
    days_left: '4D Left',
  },
  {
    name: 'Toolkit for COVID First-Line Treatment…',
    value: 80,
    days_left: '1D Left',
  }]

}
