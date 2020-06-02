import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { AppTocHomeLestoreComponent } from './app-toc-home-lestore.component'

describe('AppTocHome3Component', () => {
  let component: AppTocHomeLestoreComponent
  let fixture: ComponentFixture<AppTocHomeLestoreComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AppTocHomeLestoreComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(AppTocHomeLestoreComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
