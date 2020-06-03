import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { AppTocCohortsPathfindersComponent } from './app-toc-cohorts-pathfinders.component'

describe('AppTocCohorts2Component', () => {
  let component: AppTocCohortsPathfindersComponent
  let fixture: ComponentFixture<AppTocCohortsPathfindersComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AppTocCohortsPathfindersComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(AppTocCohortsPathfindersComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
