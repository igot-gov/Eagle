import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { QumlLibraryModule } from '@project-sunbird/sunbird-quml-player'
import { CarouselModule } from 'ngx-bootstrap/carousel'
import { AssessmentComponent } from './assessment.component'
import { AssessmentRoutingModule } from './assessment-routing.module'

@NgModule({
  declarations: [AssessmentComponent],
  imports: [
    CommonModule,
    AssessmentRoutingModule,
    QumlLibraryModule,
    CarouselModule.forRoot(),
  ],
})
export class AssessmentModule { }
