import { Component, OnInit, Input, Output, EventEmitter, ViewChild, AfterViewInit, SimpleChange } from '@angular/core'
import { SelectionModel } from '@angular/cdk/collections'
import { MatTableDataSource } from '@angular/material/table'
import { MatSort } from '@angular/material/sort'
import { MatPaginator } from '@angular/material'
import * as _ from 'lodash'

import { ITableData, IColums } from '../interface/interfaces'

@Component({
  selector: 'ws-widget-ui-user-table',
  templateUrl: './ui-user-table.component.html',
  styleUrls: ['./ui-user-table.component.scss'],
})
export class UIUserTableComponent implements OnInit, AfterViewInit {

  @Input() tableData!: ITableData | undefined
  // @Input() columns?: IColums[]
  // @Input() columns?: IColums[];
  @Input() data?: []
  // @Input() needCheckBox?: Boolean
  // @Input() needHash?: boolean
  @Output() clicked?: EventEmitter<any>
  @Output() actionsClick?: EventEmitter<any>
  // @Input() actions: IAction[]
  bodyHeight = document.body.clientHeight - 125
  displayedColumns: IColums[] | undefined
  dataSource!: any

  selection = new SelectionModel<any>(true, [])
  @ViewChild(MatPaginator, { static: true }) paginator?: MatPaginator
  @ViewChild(MatSort, { static: true }) sort?: MatSort

  constructor() {
    // console.log('ui table', this.data)
    this.dataSource = new MatTableDataSource<any>()
    this.actionsClick = new EventEmitter()
    this.clicked = new EventEmitter()

  }

  ngOnInit() {
    if (this.tableData) {
      this.displayedColumns = this.tableData.columns
    }
    this.dataSource.data = this.data
    this.dataSource.paginator = this.paginator
    this.dataSource.sort = this.sort
  }
  // tslint:disable-next-line:use-lifecycle-interface
  ngOnChanges(data: SimpleChange) {
    // console.log(data);
    this.dataSource.data = _.get(data, 'data.currentValue')
  }

  ngAfterViewInit() {
    // this.cd.detectChanges();
  }

  applyFilter(filterValue: any) {
    if (filterValue && filterValue.value) {
      let fValue = filterValue.value.trim()
      fValue = filterValue.value.toLowerCase()
      this.dataSource.filter = fValue
    } else {
      this.dataSource.filter = ''
    }
  }
  buttonClick(action: string, row: any) {
    // console.log(action, row);
    /** debugger; */
    if (this.tableData) {
      const isDisabled = _.get(_.find(this.tableData.actions, ac => ac.name === action), 'disabled') || false
      if (!isDisabled && this.actionsClick) {
        this.actionsClick.emit({ action, row })
      }
    }

  }
  getFinalColumns() {
    if (this.tableData !== undefined) {
      const columns = _.map(this.tableData.columns, c => c.key)
      if (this.tableData.needCheckBox) {
        columns.splice(0, 0, 'select')
      }
      if (this.tableData.needHash) {
        columns.splice(0, 0, 'SR')
      }
      if (this.tableData.actions && this.tableData.actions.length > 0) {
        columns.push('Actions')
      }
      return columns
    }

    // console.log(columns);

    return ''
  }

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length
    const numRows = this.dataSource.data.length
    return numSelected === numRows
  }
  filterList(list: any[], key: string) {
    return list.map(lst => lst[key])
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    this.isAllSelected() ?
      this.selection.clear() :
      this.dataSource.data.forEach((row: any) => this.selection.select(row))
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: any): string {
    if (!row) {
      return `${this.isAllSelected() ? 'select' : 'deselect'} all`
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.position + 1}`
  }

}
