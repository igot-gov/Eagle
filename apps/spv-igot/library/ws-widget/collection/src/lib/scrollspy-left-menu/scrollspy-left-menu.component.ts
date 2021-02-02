import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core'
import { NSScrollspyMenuData } from './scrollspy-left-menu.model'

@Component({
  selector: 'ws-widget-scrollspy-left-menu',
  templateUrl: './scrollspy-left-menu.component.html',
  styleUrls: ['./scrollspy-left-menu.component.scss'],
})
export class ScrollspyLeftMenuComponent implements OnInit, OnDestroy {

  @Input() tabsData!: NSScrollspyMenuData.IScrollspyTab
  @Input() currentTab!: string
  @Output() eSideNavClick = new EventEmitter<any>()
  constructor() { }

  ngOnInit(): void {

  }

  public isLinkActive(key: string): boolean {
    return (this.currentTab === key)
  }

  onSideNavTabClick(id: string) {
    this.eSideNavClick.emit(id)
  }

  ngOnDestroy() {

  }
}
