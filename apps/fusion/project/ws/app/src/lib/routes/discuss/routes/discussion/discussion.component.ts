
import { Component, OnInit } from '@angular/core'
import { NSDiscussData } from '../../models/discuss.model'

@Component({
  selector: 'app-discuss-discus sion',
  templateUrl: './discussion.component.html',
  styleUrls: ['./discussion.component.scss'],
  host: { 'class': 'flex flex-1 margin-top-l' }
})
export class DiscussionComponent implements OnInit {

  constructor() {

  }
  data!: NSDiscussData.IDiscussionData
  ngOnInit(): void {


  }
  start() {

  }
}