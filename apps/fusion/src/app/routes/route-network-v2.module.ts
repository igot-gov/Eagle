import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { NetworkV2Module } from '@ws/app'

@NgModule({
  imports: [CommonModule, NetworkV2Module],
  exports: [NetworkV2Module],
})
export class RouteNetworkV2Module {

}
