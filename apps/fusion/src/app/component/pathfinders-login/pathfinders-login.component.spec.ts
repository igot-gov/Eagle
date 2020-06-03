import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { PathfindersLoginComponent } from './pathfinders-login.component'

describe('PathfindersLoginComponent', () => {
  let component: PathfindersLoginComponent
  let fixture: ComponentFixture<PathfindersLoginComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PathfindersLoginComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(PathfindersLoginComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
