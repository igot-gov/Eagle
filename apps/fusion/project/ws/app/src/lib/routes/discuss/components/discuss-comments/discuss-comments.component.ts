import { Component, OnInit } from '@angular/core'

@Component({
  selector: 'app-discuss-comment',
  templateUrl: './discuss-comments.component.html',
  styleUrls: ['./discuss-comments.component.scss'],
})
export class DiscussCommetsComponent implements OnInit {
  items = ['1', '2', '3', '4', '5']
  ngOnInit(): void {

  }

}
