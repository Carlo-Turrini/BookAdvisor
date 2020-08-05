import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PrizeFormComponent } from './prize-form.component';

xdescribe('PrizeFormComponent', () => {
  let component: PrizeFormComponent;
  let fixture: ComponentFixture<PrizeFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PrizeFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PrizeFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
