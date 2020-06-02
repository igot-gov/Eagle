import { Component, OnInit, Input } from '@angular/core'
import { IBadge } from '../../badges.model'

@Component({
  selector: 'ws-app-badges-not-earned',
  templateUrl: './badges-not-earned.component.html',
  styleUrls: ['./badges-not-earned.component.scss'],
})
export class BadgesNotEarnedComponent implements OnInit {
  constructor(
  ) { }
  @Input()
  badge!: IBadge
  isQuiz = false
  isCourse = false
  isLearn = false
  isHoverText = false
  isDay = false
  isResource = false
  isMinute = false

  ngOnInit() {
    if (this.badge.remainingCount === 0) {
      this.isHoverText = true
    }
    if (this.badge.remainingCountUnit === 'Quiz') {
      this.isQuiz = true
    }
    if (this.badge.remainingCountUnit === 'Course') {
      this.isCourse = true
    }
    const howToEarn = this.badge.how_to_earn.toLowerCase()
    if (howToEarn.includes('learn')) {
      this.isLearn = true
    }
    if (this.badge.remainingCountUnit === 'Day') {
      this.isDay = true
    }
    if (this.badge.remainingCountUnit === 'Minute') {
      this.isMinute = true
    }
    if (this.badge.remainingCountUnit === 'Resource') {
      this.isResource = true
    }

  }
}
