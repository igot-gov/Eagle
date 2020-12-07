import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { FracComponent } from './frac.component'

describe('FracComponent', () => {
  let component: FracComponent
  let fixture: ComponentFixture<FracComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [FracComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(FracComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
