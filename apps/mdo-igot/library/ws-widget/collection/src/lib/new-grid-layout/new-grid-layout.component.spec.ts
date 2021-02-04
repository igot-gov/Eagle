import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { NewGridLayoutComponent } from './new-grid-layout.component'

describe('NewGridLayoutComponent', () => {
  let component: NewGridLayoutComponent
  let fixture: ComponentFixture<NewGridLayoutComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NewGridLayoutComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(NewGridLayoutComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
