import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { CurationProgressCardComponent } from './curation-progress-card.component'

describe('CurationProgressCardComponent', () => {
  let component: CurationProgressCardComponent
  let fixture: ComponentFixture<CurationProgressCardComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CurationProgressCardComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(CurationProgressCardComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
