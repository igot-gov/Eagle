import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ConnectionRequestCardComponent } from './connection-request-card.component'

describe('ConnectionRequestCardComponent', () => {
  let component: ConnectionRequestCardComponent
  let fixture: ComponentFixture<ConnectionRequestCardComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConnectionRequestCardComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ConnectionRequestCardComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
