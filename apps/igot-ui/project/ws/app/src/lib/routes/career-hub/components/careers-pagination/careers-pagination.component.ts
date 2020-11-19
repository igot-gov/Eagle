import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core'

@Component({
  selector: 'ws-app-careers-pagination',
  templateUrl: './careers-pagination.component.html',
  styleUrls: ['./careers-pagination.component.scss'],
})
export class CareersPaginationComponent implements OnInit {

  @Input() pager: any
  @Output() changePage = new EventEmitter<any>(true)

  ngOnInit() {
  }

  setPage(page: number) {
    this.changePage.emit(page)
  }

}
