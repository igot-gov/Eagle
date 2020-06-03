import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { DragNSortComponent } from './drag-n-sort.component'

describe('DragNSortComponent', () => {
  let component: DragNSortComponent
  let fixture: ComponentFixture<DragNSortComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DragNSortComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(DragNSortComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
