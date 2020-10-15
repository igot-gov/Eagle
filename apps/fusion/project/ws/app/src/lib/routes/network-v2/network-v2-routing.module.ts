import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { NetworkComponent } from './routes/network/network.component'
import { NetworkHomeComponent } from './routes/network-home/network-home.component'
import { NetworkMyConnectionComponent } from './routes/network-my-connection/network-my-connection.component'
import { RecommendedResolveService } from './resolvers/recommended-resolve.service'
import { MyConnectionResolveService } from './resolvers/my-connection-resolve.service'
// import { NetworkV2ResolveService } from './resolvers/network-v2-resolve.service'

const routes: Routes = [
  {
    path: '',
    component: NetworkComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'home',
      },
      {
        path: 'home',
        component: NetworkHomeComponent,
        resolve: {
          recommendedUsers: RecommendedResolveService,
        },
      },
      {
        path: 'my-connection',
        component: NetworkMyConnectionComponent,
        resolve: {
          myConnectionList: MyConnectionResolveService,
        },
      },
    ],
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class NetworkV2RoutingModule { }
