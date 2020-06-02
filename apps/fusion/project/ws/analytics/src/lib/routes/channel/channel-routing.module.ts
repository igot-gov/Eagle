import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { ChannelComponent } from './channel.component'
import { PageResolve } from '@ws-widget/utils'

const routes: Routes = [
  {
    path: ':id',
    component: ChannelComponent,
    data: {
      pageType: 'feature',
      pageKey: 'analytics',
    },
    resolve: {
      pageData: PageResolve,
    },
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ChannelRoutingModule { }
