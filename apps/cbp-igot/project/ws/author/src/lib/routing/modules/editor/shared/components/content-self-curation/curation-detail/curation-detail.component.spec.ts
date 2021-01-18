import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { CurationDetailComponent } from './curation-detail.component'

describe('CurationDetailComponent', () => {
  let component: CurationDetailComponent
  let fixture: ComponentFixture<CurationDetailComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CurationDetailComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(CurationDetailComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
