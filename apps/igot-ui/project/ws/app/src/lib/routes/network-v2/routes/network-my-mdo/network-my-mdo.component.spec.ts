import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { NetworkMyMdoComponent } from './network-my-mdo.component'

describe('NetworkMyMdoComponent', () => {
  let component: NetworkMyMdoComponent
  let fixture: ComponentFixture<NetworkMyMdoComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NetworkMyMdoComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(NetworkMyMdoComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
