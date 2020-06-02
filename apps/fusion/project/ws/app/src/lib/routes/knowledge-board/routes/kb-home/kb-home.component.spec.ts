import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { KbHomeComponent } from './kb-home.component'

describe('KbHomeComponent', () => {
  let component: KbHomeComponent
  let fixture: ComponentFixture<KbHomeComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [KbHomeComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(KbHomeComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
