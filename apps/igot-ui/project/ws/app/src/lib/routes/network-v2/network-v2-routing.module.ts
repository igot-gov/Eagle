import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { NetworkComponent } from './routes/network/network.component'
import { NetworkHomeComponent } from './routes/network-home/network-home.component'
import { NetworkMyConnectionComponent } from './routes/network-my-connection/network-my-connection.component'
import { RecommendedResolveService } from './resolvers/recommended-resolve.service'
import { MyConnectionResolveService } from './resolvers/my-connection-resolve.service'
import { NetworkConnectionRequestsComponent } from './routes/network-connection-requests/network-connection-requests.component'
import { NetworkMyMdoComponent } from './routes/network-my-mdo/network-my-mdo.component'
import { NetworkRecommendedComponent } from './routes/network-recommended/network-recommended.component'
import { MyMdoResolveService } from './resolvers/my-mdo-resolve.service'
import { ConnectionRequestResolveService } from './resolvers/connection-request-resolve.service'

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
          recommendedUsers: MyMdoResolveService,
          connectionRequests: ConnectionRequestResolveService,
          myConnectionList: MyConnectionResolveService,
        },
      },
      {
        path: 'my-connection',
        component: NetworkMyConnectionComponent,
        resolve: {
          myConnectionList: MyConnectionResolveService,
        },
      },
      {
        path: 'connection-requests',
        component: NetworkConnectionRequestsComponent,
        resolve: {
          connectionRequests: ConnectionRequestResolveService,
        },
      },
      {
        path: 'my-mdo',
        component: NetworkMyMdoComponent,
        resolve: {
          myMdoList: MyMdoResolveService,
        },
      },
      {
        path: 'recommended',
        component: NetworkRecommendedComponent,
        resolve: {
          recommendedList: RecommendedResolveService,
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
