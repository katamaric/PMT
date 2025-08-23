import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { AuthService } from './auth/auth.service';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let isLoggedInSubject: BehaviorSubject<boolean>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    isLoggedInSubject = new BehaviorSubject<boolean>(false);

    mockAuthService = jasmine.createSpyObj<AuthService>('AuthService', ['logout'], {
      isLoggedIn$: isLoggedInSubject.asObservable()
    });

    mockRouter = jasmine.createSpyObj<Router>('Router', ['navigate']);

    await TestBed.configureTestingModule({
      declarations: [AppComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;

    component.authChecked = true;

    fixture.detectChanges();
  });

  it('should create the app component', () => {
    expect(component).toBeTruthy();
  });

  it('should render the title in the template', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent)
      .toContain('PMT: Project Management Tool');
  });

  it('should show login/register links when user is NOT logged in', () => {
    isLoggedInSubject.next(false);
    fixture.detectChanges();
  
    const compiled = fixture.nativeElement as HTMLElement;
    const links = Array.from(compiled.querySelectorAll('a')).map(a => a.textContent?.trim());
  
    expect(links).toContain('Login');
    expect(links).toContain('Register');
    expect(compiled.textContent).not.toContain('Logout');
  });

  it('should show logout button when user IS logged in', () => {
    isLoggedInSubject.next(true);
    fixture.detectChanges();
  
    const compiled = fixture.nativeElement as HTMLElement;
  
    const navText = Array.from(compiled.querySelectorAll('a, button'))
      .map(el => el.textContent?.trim());
  
    expect(navText).toContain('Dashboard');
    expect(navText).toContain('Logout');
    expect(navText).not.toContain('Login');
    expect(navText).not.toContain('Register');
  });
  it('should call authService.logout() and navigate to login on logout()', () => {
    component.logout();

    expect(mockAuthService.logout).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
  });
});
