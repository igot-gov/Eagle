import { NgModule } from '@angular/core'
import { CardCourseComponent } from './card-course.component'
import { MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule, MatExpansionModule, MatIconModule, MatProgressSpinnerModule } from '@angular/material'
import { BrowserModule } from '@angular/platform-browser'
import { AvatarPhotoModule } from '../_common/avatar-photo/avatar-photo.module'
import { HorizontalScrollerModule, PipeNameTransformModule } from '@ws-widget/utils'
import { MatProgressBarModule } from '@angular/material/progress-bar'

@NgModule({
  declarations: [CardCourseComponent],
  imports: [BrowserModule, MatButtonModule, MatCardModule, MatChipsModule, MatDividerModule, MatProgressBarModule,
    MatExpansionModule, MatIconModule, MatProgressSpinnerModule, AvatarPhotoModule, HorizontalScrollerModule, PipeNameTransformModule],
  entryComponents: [CardCourseComponent],
})
export class CardCourseModule {

}
