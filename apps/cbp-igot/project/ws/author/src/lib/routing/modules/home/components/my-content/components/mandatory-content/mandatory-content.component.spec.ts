import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { MandatoryContentComponent } from './mandatory-content.component'

describe('MandatoryContentComponent', () => {
  let component: MandatoryContentComponent
  let fixture: ComponentFixture<MandatoryContentComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MandatoryContentComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(MandatoryContentComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
