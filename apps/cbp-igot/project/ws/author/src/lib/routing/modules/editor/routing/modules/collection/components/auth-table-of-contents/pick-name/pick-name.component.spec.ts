import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { PickNameComponent } from './pick-name.component'

describe('PickNameComponent', () => {
  let component: PickNameComponent
  let fixture: ComponentFixture<PickNameComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PickNameComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(PickNameComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
