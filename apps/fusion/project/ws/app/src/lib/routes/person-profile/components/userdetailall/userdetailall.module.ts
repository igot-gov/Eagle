import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { UserdetailallComponent } from './userdetailall.component'

@NgModule({
  declarations: [UserdetailallComponent],
  imports: [
    CommonModule,
  ],
  exports: [UserdetailallComponent],
  entryComponents: [UserdetailallComponent],
})
export class UserdetailallModule { }
