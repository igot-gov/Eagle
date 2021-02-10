import { Component, OnInit, OnDestroy, Input } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { NsWidgetResolver, WidgetBaseComponent } from '@ws-widget/resolver'
import { ILeftMenu } from './left-menu.model'

@Component({
  selector: 'ws-widget-left-menu',
  templateUrl: './left-menu.component.html',
  styleUrls: ['./left-menu.component.scss'],
})
export class LeftMenuComponent extends WidgetBaseComponent
  implements OnInit, OnDestroy, NsWidgetResolver.IWidgetData<ILeftMenu[]>  {
  @Input() widgetData!: ILeftMenu[]
  param: any
  constructor(private activatedRoute: ActivatedRoute) {
    super()
  }

  ngOnInit(): void {

  }

  public isLinkActive(url: string): boolean {
    return (this.activatedRoute.snapshot.fragment === url)
  }
  ngOnDestroy() {

  }
  getLink(tab: ILeftMenu) {
    if (tab && tab.customRouting && this.activatedRoute.snapshot && this.activatedRoute.snapshot.firstChild && tab.paramaterName) {
      return (tab.routerLink.replace('<param>', this.activatedRoute.snapshot.firstChild.params[tab.paramaterName]))
    }
    return
  }
}
