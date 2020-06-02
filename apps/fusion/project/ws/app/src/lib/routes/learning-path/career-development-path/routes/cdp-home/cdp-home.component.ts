import { Component, OnInit } from '@angular/core'
import { ValueService, NsPage, ConfigurationsService } from '@ws-widget/utils'
import { Subscription } from 'rxjs/internal/Subscription'
import { MatSnackBar } from '@angular/material'
import { ActivatedRoute, Router } from '@angular/router'
import { ITile, ICdpTopicChild, ILpHomeTile } from '../../models/career-development-path.model'

@Component({
  selector: 'ws-app-cdp-home',
  templateUrl: './cdp-home.component.html',
  styleUrls: ['./cdp-home.component.scss'],
})
export class CdpHomeComponent implements OnInit {
  tiles: ITile[] = []
  titleName = ''
  titleKey = ''
  childrenData: ICdpTopicChild = {} as ICdpTopicChild
  showChildren = false
  childName = ''
  child = ''
  screenSizeIsLtMedium = false
  defaultSideNavBarOpenedSubscription: Subscription | null = null
  pageNavbar: Partial<NsPage.INavBackground> = this.configSvc.pageNavBar
  isLtMedium$ = this.valueSvc.isLtMedium$
  constructor(
    private valueSvc: ValueService,
    private activatedRoute: ActivatedRoute,
    public matSnackBar: MatSnackBar,
    private route: Router,
    private configSvc: ConfigurationsService,
  ) { }

  ngOnInit() {
    this.defaultSideNavBarOpenedSubscription = this.isLtMedium$.subscribe((isLtMedium: boolean) => {
      this.screenSizeIsLtMedium = isLtMedium
    })
    // const childNameLocal = localStorage.getItem('childName')
    // if (childNameLocal === undefined || childNameLocal === null) {
    //   // Do nothing
    // } else {
    //   this.childName = childNameLocal
    // }
    this.activatedRoute.params.subscribe(params => {
      this.titleName = params.title.trim()
      let newArr: ILpHomeTile[] = []
      const first = this.activatedRoute.snapshot.data.pageData.data.first
      newArr = newArr.concat(first.tileData1, first.tileData2, first.tileData3)
      this.titleKey = newArr.filter(
        (obj: ILpHomeTile) => obj.titleKey === params.title.trim(),
      )[0].displayName
    })
    this.activatedRoute.queryParamMap.subscribe(queryParams => {
      if (queryParams.has('path')) {
        this.child = queryParams.get('path') || ''
        this.childrenData = this.activatedRoute.snapshot.data.pageData.data.third[this.titleName][
          this.child][0]
        this.showChildren = true
      } else {
        this.showChildren = false
      }
    })
    this.tiles = this.activatedRoute.snapshot.data.pageData.data.second[this.titleName]
  }

  getChildren(title: string, displayName?: string) {
    this.childName = displayName || ''
    if (displayName === 'Global Account Manager') {
      // tslint:disable-next-line:max-line-length
      window.open('https://intranet.lc-tools.siemens.de/filehosting/learningworld/editor/20190312_improvements_dist/preview.html?f=kam_gam_final_v2.json&r=0.23504553058239908', '_blank')
    } else {
      this.child = title
      if (this.activatedRoute.snapshot.data.pageData.data.third[this.titleName][title]) {
        this.showChildren = true
        this.childrenData = this.activatedRoute.snapshot.data.pageData.data.third[this.titleName][
          title][0]
        this.route.navigate([], {
          queryParams: { path: title },
          relativeTo: this.activatedRoute.parent,
          queryParamsHandling: 'merge',
        })
      } else {
        this.showChildren = false
        this.openSnackBar()
      }
    }
    // localStorage.setItem('childName', this.childNamse)
  }
  openSnackBar() {
    this.matSnackBar.open('Coming Soon...', 'close', {
      duration: 2000,
    })
  }
  gotoChild(type: string) {
    this.route.navigate([`/app/learning-journey/cdp/${this.titleName}/${this.child}/${type}`])
  }

  getOpacity(display: string) {
    if (this.child.length === 0) {
      return '1'
    }
    if (this.child.length > 0) {
      if (this.child === display) {
        return '1'
      }
      return '0.5'
    }
    return '1'
  }
}
