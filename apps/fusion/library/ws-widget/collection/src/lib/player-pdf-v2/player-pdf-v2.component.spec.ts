import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { PlayerPdfV2Component } from './player-pdf-v2.component'

describe('PlayerPdfV2Component', () => {
  let component: PlayerPdfV2Component
  let fixture: ComponentFixture<PlayerPdfV2Component>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PlayerPdfV2Component],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(PlayerPdfV2Component)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
