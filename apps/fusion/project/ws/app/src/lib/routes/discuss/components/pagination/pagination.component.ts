
import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core'

@Component({
  selector: 'ws-app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.scss'],
})
export class PaginationComponent implements OnInit {

  @Input() pager: any
  @Output() changePage = new EventEmitter<any>(true)

  ngOnInit() {
  }

  setPage(page: number) {
    this.changePage.emit(page)
  }

}
