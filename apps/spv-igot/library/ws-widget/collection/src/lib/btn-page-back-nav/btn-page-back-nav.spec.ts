import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { BtnPageBackNavComponent } from './btn-page-back.component'

describe('BtnPageBackNavComponent', () => {
  let component: BtnPageBackNavComponent
  let fixture: ComponentFixture<BtnPageBackNavComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [BtnPageBackNavComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(BtnPageBackNavComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
