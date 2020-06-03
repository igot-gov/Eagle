import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { PathfindersCalendarComponent } from './pathfinders-calendar.component'

describe('PathfindersCalendarComponent', () => {
  let component: PathfindersCalendarComponent
  let fixture: ComponentFixture<PathfindersCalendarComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PathfindersCalendarComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(PathfindersCalendarComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
