import { NgModule } from '@angular/core'
import { Routes, RouterModule } from '@angular/router'
import { MyContentComponent } from './components/my-content/my-content.component'
import { MandatoryContentResolverService } from './resolvers/mandatory-content-resolver.service'

const routes: Routes = [
  {
    path: '',
    component: MyContentComponent,
    resolve: {
      courseTaken: MandatoryContentResolverService
    }
  },
]
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [MandatoryContentResolverService]
})
export class MyContentRoutingModule { }
