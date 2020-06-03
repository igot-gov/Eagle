import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { UploadKartifactComponent } from './upload-kartifact.component'

describe('UploadKartifactComponent', () => {
  let component: UploadKartifactComponent
  let fixture: ComponentFixture<UploadKartifactComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [UploadKartifactComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(UploadKartifactComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
