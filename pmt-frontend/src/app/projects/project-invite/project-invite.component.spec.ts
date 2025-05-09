import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProjectInviteComponent } from './project-invite.component';
import { ReactiveFormsModule } from '@angular/forms';
import { ProjectService } from '../project.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { By } from '@angular/platform-browser';

describe('ProjectInviteComponent', () => {
  let component: ProjectInviteComponent;
  let fixture: ComponentFixture<ProjectInviteComponent>;
  let projectService: ProjectService;
  let router: Router;
  let activatedRoute: ActivatedRoute;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, HttpClientTestingModule],
      declarations: [ProjectInviteComponent],
      providers: [
        ProjectService,
        Router,
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: () => '1' } } }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProjectInviteComponent);
    component = fixture.componentInstance;
    projectService = TestBed.inject(ProjectService);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create the component and initialize the form', () => {
    expect(component).toBeTruthy();
    expect(component.inviteForm).toBeDefined();
  });

  it('should call inviteMember on submit with valid form data', () => {
    const mockInviteData = {
      email: 'test@example.com',
      role: 'MEMBER'
    };

    component.inviteForm.setValue(mockInviteData);

    const currentUser = { id: 1 };
    spyOn(localStorage, 'getItem').and.returnValue(JSON.stringify(currentUser));

    const spy = spyOn(projectService, 'inviteMember').and.returnValue(of({ success: true }));

    component.onSubmit();

    expect(spy).toHaveBeenCalledWith(1, 'test@example.com', 'MEMBER', 1);
  });

  it('should display error message on failed invitation', () => {
    component.inviteForm.setValue({
      email: 'test@example.com',
      role: 'MEMBER'
    });

    const currentUser = { id: 1 };
    spyOn(localStorage, 'getItem').and.returnValue(JSON.stringify(currentUser));

    const spy = spyOn(projectService, 'inviteMember').and.returnValue(throwError({ message: 'Error' }));
    fixture.detectChanges();

    component.onSubmit();

    fixture.detectChanges();
    const errorMessage = fixture.debugElement.query(By.css('.error-message')).nativeElement;
    expect(errorMessage.textContent).toContain('An error occurred. Please try again.');
  });
});
