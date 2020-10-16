import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { ConnectionPeopleCardComponent } from './connection-people-card.component'

describe('ConnectionPeopleCardComponent', () => {
  let component: ConnectionPeopleCardComponent
  let fixture: ComponentFixture<ConnectionPeopleCardComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConnectionPeopleCardComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(ConnectionPeopleCardComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
