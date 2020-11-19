import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { NetworkConnectionRequestsComponent } from './network-connection-requests.component'

describe('NetworkConnectionRequestsComponent', () => {
  let component: NetworkConnectionRequestsComponent
  let fixture: ComponentFixture<NetworkConnectionRequestsComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NetworkConnectionRequestsComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(NetworkConnectionRequestsComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
