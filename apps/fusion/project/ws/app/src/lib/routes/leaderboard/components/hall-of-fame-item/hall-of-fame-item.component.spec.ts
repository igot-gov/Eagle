import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { HallOfFameItemComponent } from './hall-of-fame-item.component'

describe('HallOfFameItemComponent', () => {
  let component: HallOfFameItemComponent
  let fixture: ComponentFixture<HallOfFameItemComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [HallOfFameItemComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(HallOfFameItemComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
