import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { DynamicNetworkComponent } from './dynamic-network.component'

describe('DynamicNetworkComponent', () => {
  let component: DynamicNetworkComponent
  let fixture: ComponentFixture<DynamicNetworkComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DynamicNetworkComponent],
    }).compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(DynamicNetworkComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
