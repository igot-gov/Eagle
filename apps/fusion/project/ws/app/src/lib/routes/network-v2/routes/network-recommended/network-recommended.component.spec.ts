import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { NetworkRecommendedComponent } from './network-recommended.component'

describe('NetworkRecommendedComponent', () => {
  let component: NetworkRecommendedComponent
  let fixture: ComponentFixture<NetworkRecommendedComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NetworkRecommendedComponent],
    })
    .compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(NetworkRecommendedComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
