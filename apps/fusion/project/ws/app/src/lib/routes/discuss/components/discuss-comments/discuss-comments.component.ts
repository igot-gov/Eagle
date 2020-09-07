import { Component, OnInit } from '@angular/core'

@Component({
  selector: 'app-discuss-comment',
  templateUrl: './discuss-comments.component.html',
  styleUrls: ['./discuss-comments.component.scss'],
})
export class DiscussCommetsComponent implements OnInit {
  items = [
    'All new methods of control of powers of the administrative authorities and more such policies.',
    'Powers and functions of the administrative authorities Methods of control of powers of the administrative authorities',
    'New procedures to be followed by the administrative authorities',
    'India’s first line of workers is already engaged in training module for management of COVID 19']
  ngOnInit(): void {

  }

}
