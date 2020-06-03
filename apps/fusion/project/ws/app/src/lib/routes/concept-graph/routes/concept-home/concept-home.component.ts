import { Component, ElementRef, OnInit, ViewChild } from '@angular/core'
import { FormControl } from '@angular/forms'
import { Router, ActivatedRoute } from '@angular/router'
import { Observable } from 'rxjs'
import { debounceTime, distinctUntilChanged, startWith, switchMap } from 'rxjs/operators'
import { ConceptGraphService } from '../../services/concept-graph.service'
import { MatSnackBar } from '@angular/material'
import { NsPage, ConfigurationsService } from '@ws-widget/utils'

@Component({
  selector: 'ws-app-concept-graph-home',
  templateUrl: './concept-home.component.html',
  styleUrls: ['./concept-home.component.scss'],
})
export class ConceptHomeComponent implements OnInit {
  queryControl = new FormControl('')

  filteredOptions$: Observable<
    { id: string; name: string; concept_id?: number }[]
  > = this.queryControl.valueChanges.pipe(
    startWith(this.queryControl.value),
    debounceTime(500),
    distinctUntilChanged(),
    switchMap(value => this.conceptServ.getAutoComplete(value ? value : ' ')),
  )
  @ViewChild('searchInput', { static: true })
  searchInputElem!: ElementRef<any>
  pageNavbar: Partial<NsPage.INavBackground> = this.configSvc.pageNavBar
  constructor(
    private router: Router,
    private activated: ActivatedRoute,
    public matSnackBar: MatSnackBar,
    private conceptServ: ConceptGraphService,
    private configSvc: ConfigurationsService,
  ) {}

  ngOnInit() {}

  updateQuery(query: number) {
    this.searchInputElem.nativeElement.blur()
    if (query && query !== undefined) {
      this.router.navigate([])
      this.router.navigate([`${query}`], { relativeTo: this.activated.parent })
    } else {
      this.matSnackBar.open('The Selected topic doesn"t have any related items', 'close', {
        duration: 3000,
      })
      return
    }
  }
}
