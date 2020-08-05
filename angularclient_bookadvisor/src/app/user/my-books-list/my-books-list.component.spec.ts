import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MyBooksListComponent } from './my-books-list.component';

xdescribe('MyBooksListComponent', () => {
  let component: MyBooksListComponent;
  let fixture: ComponentFixture<MyBooksListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MyBooksListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MyBooksListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
