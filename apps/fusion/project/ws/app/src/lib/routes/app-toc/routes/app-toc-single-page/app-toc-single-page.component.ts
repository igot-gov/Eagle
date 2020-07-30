import { Component, OnInit, ComponentFactoryResolver, ViewChild } from '@angular/core'
import { AppTocSinglePageDirective } from './app-toc-single-page.directive'
import { AppTocSinglePageService } from './app-toc-single-page.service'

@Component({
  selector: 'ws-app-app-toc-single-page-root',
  templateUrl: './app-toc-single-page.component.html',
  styleUrls: ['./app-toc-single-page.component.scss'],
})
export class AppTocSinglePageComponent implements OnInit {

  @ViewChild(AppTocSinglePageDirective, { static: true }) wsAppAppTocSinglePage!: AppTocSinglePageDirective

  constructor(
    private componentFactoryResolver: ComponentFactoryResolver,
    private appTocSvc: AppTocSinglePageService,
  ) { }

  loadComponent() {
    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.appTocSvc.getComponent())
    const viewContainerRef = this.wsAppAppTocSinglePage.viewContainerRef
    viewContainerRef.clear()
    viewContainerRef.createComponent(componentFactory)
  }

  ngOnInit() {
    this.loadComponent()
  }

}
