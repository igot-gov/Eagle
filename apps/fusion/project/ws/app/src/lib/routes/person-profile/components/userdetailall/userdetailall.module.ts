import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { UserdetailallComponent } from './userdetailall.component'
import { BadgesModule } from '../../../profile/routes/badges/badges.module'

@NgModule({
  declarations: [UserdetailallComponent],
  imports: [
    CommonModule,
    BadgesModule,
  ],
  exports: [UserdetailallComponent],
  entryComponents: [UserdetailallComponent],
})
export class UserdetailallModule { }
