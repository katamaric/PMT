import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../auth.service';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    mockAuthService = jasmine.createSpyObj('AuthService', ['register']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [RegisterComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should have invalid form when empty', () => {
    expect(component.registerForm.valid).toBeFalse();
  });

  it('should call authService.register and navigate on success', fakeAsync(() => {
    mockAuthService.register.and.returnValue(of('Registration successful'));

    component.registerForm.setValue({
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123'
    });

    component.onSubmit();

    expect(mockAuthService.register).toHaveBeenCalledWith({
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123'
    });

    expect(component.isSuccess).toBeTrue();
    expect(component.message).toBe('Registration successful!');

    tick(1500); // simulate setTimeout
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
  }));

  it('should display string error response correctly', () => {
    mockAuthService.register.and.returnValue(throwError(() => ({ error: 'Email already in use' })));

    component.registerForm.setValue({
      username: 'existinguser',
      email: 'existing@example.com',
      password: 'password123'
    });

    component.onSubmit();

    expect(component.isSuccess).toBeFalse();
    expect(component.message).toBe('Email already in use');
  });

  it('should handle structured error response', () => {
    mockAuthService.register.and.returnValue(throwError(() => ({
      error: { message: 'Server validation failed' }
    })));

    component.registerForm.setValue({
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123'
    });

    component.onSubmit();

    expect(component.isSuccess).toBeFalse();
    expect(component.message).toBe('Server validation failed');
  });

  it('should fallback to generic error message', () => {
    mockAuthService.register.and.returnValue(throwError(() => ({ error: null })));

    component.registerForm.setValue({
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123'
    });

    component.onSubmit();

    expect(component.message).toBe('An error occurred. Please try again.');
  });
});
