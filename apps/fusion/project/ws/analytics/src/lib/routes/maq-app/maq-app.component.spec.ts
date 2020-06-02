import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { MaqAppComponent } from './maq-app.component'

describe('MaqAppComponent', () => {
  let component: MaqAppComponent
  let fixture: ComponentFixture<MaqAppComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MaqAppComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(MaqAppComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
