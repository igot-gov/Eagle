import { Component, OnInit, OnDestroy, Input } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { NsWidgetResolver, WidgetBaseComponent } from '@ws-widget/resolver'
import { ILeftMenu, IMenu } from './left-menu.model'

@Component({
  selector: 'ws-widget-left-menu',
  templateUrl: './left-menu.component.html',
  styleUrls: ['./left-menu.component.scss'],
})
export class LeftMenuComponent extends WidgetBaseComponent
  implements OnInit, OnDestroy, NsWidgetResolver.IWidgetData<ILeftMenu>  {
  @Input() widgetData!: ILeftMenu
  // @Input() Source
  constructor(private activatedRoute: ActivatedRoute) {
    super()
  }
  ngOnDestroy(): void {
    // throw new Error('Method not implemented.')
  }

  ngOnInit(): void {

  }

  public isLinkActive(url?: string, index?: number): boolean {
    let returnVal = false
    if (url && index) {
      returnVal = (this.activatedRoute.snapshot.fragment === url)
    } else if (index === 0) {
      returnVal = true
    } else {
      returnVal = false
    }
    return returnVal
  }

  getLink(tab: IMenu) {
    if (tab && tab.customRouting && this.activatedRoute.snapshot && this.activatedRoute.snapshot.firstChild && tab.paramaterName) {
      return (tab.routerLink.replace('<param>', this.activatedRoute.snapshot.firstChild.params[tab.paramaterName]))
    }
    return
  }
}
