import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { NetworkMyConnectionComponent } from './network-my-connection.component'

describe('NetworkMyConnectionComponent', () => {
  let component: NetworkMyConnectionComponent
  let fixture: ComponentFixture<NetworkMyConnectionComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NetworkMyConnectionComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(NetworkMyConnectionComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
