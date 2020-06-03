import { Component, OnInit, Input } from '@angular/core'

@Component({
  selector: 'ws-auth-view-slider-question',
  templateUrl: './view-slider-question.component.html',
  styleUrls: ['./view-slider-question.component.scss'],
})
export class ViewSliderQuestionComponent implements OnInit {
  value!: number
  @Input() data: any
  constructor() { }

  ngOnInit() {
    if (this.data.hasOwnProperty('userSolution')) {
      this.value = Number(this.data.userSolution)
    } else {
      this.value = Number(this.data.solution)
    }

  }

}
