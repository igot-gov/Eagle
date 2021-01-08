import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { AllContentComponent } from './components/all-content/all-content.component'
import { MyContentComponent } from './components/my-content/my-content.component'
import { MandatoryContentResolverService } from './resolvers/mandatory-content-resolver.service'

const routes: Routes = [

  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'me',
  },
  {
    path: 'me',
    component: MyContentComponent,
    resolve: {
      courseTaken: MandatoryContentResolverService,
    },
  },
  {
    path: 'all',
    component: AllContentComponent,
    resolve: {
      courseTaken: MandatoryContentResolverService,
    },
  },
]
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [MandatoryContentResolverService],
})
export class MyContentRoutingModule { }
