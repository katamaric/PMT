import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../auth.service';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    mockAuthService = jasmine.createSpyObj('AuthService', ['login']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should have invalid form when fields are empty', () => {
    expect(component.loginForm.valid).toBeFalse();
  });

  it('should call authService.login and navigate on successful login', fakeAsync(() => {
    const mockResponse = { token: 'fake-token' };
    mockAuthService.login.and.returnValue(of(mockResponse));
    spyOn(localStorage, 'setItem');

    component.loginForm.setValue({ email: 'test@test.com', password: 'password123' });
    component.onSubmit();

    expect(mockAuthService.login).toHaveBeenCalledWith({
      email: 'test@test.com',
      password: 'password123'
    });

    tick(1000); // simulate setTimeout
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/dashboard']);
    expect(localStorage.setItem).toHaveBeenCalledWith('currentUser', JSON.stringify(mockResponse));
    expect(component.isSuccess).toBeTrue();
    expect(component.message).toBe('Login successful!');
  }));

  it('should show error message on failed login with string', () => {
    const mockError = { error: 'Invalid credentials' };
    mockAuthService.login.and.returnValue(throwError(() => mockError));

    component.loginForm.setValue({ email: 'wrong@test.com', password: 'wrongpass' });
    component.onSubmit();

    expect(component.isSuccess).toBeFalse();
    expect(component.message).toBe('Invalid credentials');
  });

  it('should show generic error if error.error is undefined', () => {
    const mockError = { error: null };
    mockAuthService.login.and.returnValue(throwError(() => mockError));

    component.loginForm.setValue({ email: 'wrong@test.com', password: 'wrongpass' });
    component.onSubmit();

    expect(component.isSuccess).toBeFalse();
    expect(component.message).toBe('Login failed. Please try again.');
  });

  it('should show validation error if form is invalid', () => {
    component.loginForm.setValue({ email: '', password: '' });
    component.onSubmit();

    expect(component.isSuccess).toBeFalse();
    expect(component.message).toBe('Please fix errors before submitting.');
  });
});
