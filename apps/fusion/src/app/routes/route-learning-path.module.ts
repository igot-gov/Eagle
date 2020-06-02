import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { LearningPathModule } from '@ws/app'

@NgModule({
  declarations: [],
  imports: [CommonModule, LearningPathModule],
  exports: [LearningPathModule],
})
export class RouteLearningPathModule {}
