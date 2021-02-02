import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { TodayEventCardComponent } from './today-event-card.component'

describe('TodayEventCardComponent', () => {
  let component: TodayEventCardComponent
  let fixture: ComponentFixture<TodayEventCardComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TodayEventCardComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(TodayEventCardComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
