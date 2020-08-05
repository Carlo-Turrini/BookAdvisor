import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UtenteUpdateComponent } from './utente-update.component';

xdescribe('UtenteUpdateComponent', () => {
  let component: UtenteUpdateComponent;
  let fixture: ComponentFixture<UtenteUpdateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UtenteUpdateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UtenteUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
