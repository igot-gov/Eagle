import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { CareersPaginationComponent } from './careers-pagination.component'

describe('CareersPaginationComponent', () => {
  let component: CareersPaginationComponent
  let fixture: ComponentFixture<CareersPaginationComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CareersPaginationComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(CareersPaginationComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
