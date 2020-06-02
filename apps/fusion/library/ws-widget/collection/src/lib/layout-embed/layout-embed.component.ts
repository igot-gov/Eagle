import { Component, OnInit, Input } from '@angular/core'
import { NsWidgetResolver, WidgetBaseComponent } from '@ws-widget/resolver'
import { RootService } from 'src/app/component/root/root.service'

interface IEmbedWidget {
  widget: NsWidgetResolver.IRenderConfigWithAnyData
}

@Component({
  selector: 'ws-widget-layout-embed',
  templateUrl: './layout-embed.component.html',
  styleUrls: ['./layout-embed.component.scss'],
})
export class LayoutEmbedComponent extends WidgetBaseComponent
  implements OnInit, NsWidgetResolver.IWidgetData<IEmbedWidget>  {

  @Input() widgetData!: IEmbedWidget

  constructor(
    private rootSvc: RootService,
  ) {
    super()
  }

  ngOnInit() {
    this.rootSvc.showNavbarDisplay$.next(false)
  }

}
