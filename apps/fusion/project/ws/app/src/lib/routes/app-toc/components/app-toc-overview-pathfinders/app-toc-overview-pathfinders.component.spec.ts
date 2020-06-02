import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { AppTocOverviewPathfindersComponent } from './app-toc-overview-pathfinders.component'

describe('AppTocOverview2Component', () => {
  let component: AppTocOverviewPathfindersComponent
  let fixture: ComponentFixture<AppTocOverviewPathfindersComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AppTocOverviewPathfindersComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(AppTocOverviewPathfindersComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
