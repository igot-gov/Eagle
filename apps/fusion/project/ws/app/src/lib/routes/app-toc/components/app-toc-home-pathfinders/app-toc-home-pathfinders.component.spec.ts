import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { AppTocHomePathfindersComponent } from './app-toc-home-pathfinders.component'

describe('AppTocHome2Component', () => {
  let component: AppTocHomePathfindersComponent
  let fixture: ComponentFixture<AppTocHomePathfindersComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AppTocHomePathfindersComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(AppTocHomePathfindersComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
