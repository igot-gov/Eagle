import { Component, OnInit, OnDestroy, Input, HostListener, ElementRef, ViewChild, AfterViewInit } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { NsWidgetResolver, WidgetBaseComponent } from '@ws-widget/resolver'
import { ILeftMenu } from './left-menu.model'

@Component({
  selector: 'ws-widget-left-menu',
  templateUrl: './left-menu.component.html',
  styleUrls: ['./left-menu.component.scss'],
})
export class LeftMenuComponent extends WidgetBaseComponent
  implements OnInit, OnDestroy, AfterViewInit, NsWidgetResolver.IWidgetData<ILeftMenu[]>  {
  @Input() widgetData!: ILeftMenu[]
  @ViewChild('stickyMenu', { static: true }) menuElement!: ElementRef
  elementPosition: any
  sticky = false

  @HostListener('window:scroll', ['$event'])
  handleScroll() {
    const windowScroll = window.pageYOffset
    if (windowScroll >= this.elementPosition) {
      this.sticky = true
    } else {
      this.sticky = false
    }
  }
  constructor(private activatedRoute: ActivatedRoute) {
    super()
  }

  ngOnInit(): void {

  }
  ngAfterViewInit() {
    this.elementPosition = this.menuElement.nativeElement.offsetTop
  }
  public isLinkActive(url: string): boolean {
    return (this.activatedRoute.snapshot.fragment === url)
  }
  ngOnDestroy() {

  }
}
