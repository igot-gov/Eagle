import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { InfosysagmComponent } from './infosysagm.component'

describe('InfosysagmComponent', () => {
  let component: InfosysagmComponent
  let fixture: ComponentFixture<InfosysagmComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InfosysagmComponent],
    }).compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(InfosysagmComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
