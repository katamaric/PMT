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

    component.loginForm.setValue({ email: 'test@example.com', password: 'password123' });
    component.onSubmit();

    expect(mockAuthService.login).toHaveBeenCalledWith({
      email: 'test@example.com',
      password: 'password123'
    });

    tick(1000); // simulate setTimeout
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/dashboard']);
    expect(component.isSuccess).toBeTrue();
    expect(component.message).toBe('Login successful!');
  }));

  it('should show error message on failed login', () => {
    const mockError = { error: 'Invalid credentials' };
    mockAuthService.login.and.returnValue(throwError(() => mockError));

    component.loginForm.setValue({ email: 'wrong@example.com', password: 'wrongpass' });
    component.onSubmit();

    expect(component.isSuccess).toBeFalse();
    expect(component.message).toBe('Invalid credentials');
  });
});
