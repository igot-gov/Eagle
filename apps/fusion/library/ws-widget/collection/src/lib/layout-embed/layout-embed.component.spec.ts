import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { LayoutEmbedComponent } from './layout-embed.component'

describe('LayoutEmbedComponent', () => {
  let component: LayoutEmbedComponent
  let fixture: ComponentFixture<LayoutEmbedComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [LayoutEmbedComponent],
    })
      .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(LayoutEmbedComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
