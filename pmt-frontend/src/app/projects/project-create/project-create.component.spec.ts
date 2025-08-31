import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ProjectCreateComponent } from './project-create.component';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { ProjectService } from '../project.service';
import { of, throwError } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('ProjectCreateComponent', () => {
  let component: ProjectCreateComponent;
  let fixture: ComponentFixture<ProjectCreateComponent>;
  let projectService: ProjectService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, HttpClientTestingModule],
      declarations: [ProjectCreateComponent],
      providers: [FormBuilder, ProjectService, Router]
    }).compileComponents();

    fixture = TestBed.createComponent(ProjectCreateComponent);
    component = fixture.componentInstance;
    projectService = TestBed.inject(ProjectService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create the component and initialize the form', () => {
    expect(component).toBeTruthy();
    expect(component.projectForm).toBeDefined();
  });

  it('should call createProject on submit with valid form data', () => {
    const mockProjectData = { name: 'New Project', description: 'Test', startDate: '2025-06-01' };
    component.projectForm.setValue(mockProjectData);
    spyOn(localStorage, 'getItem').and.returnValue(JSON.stringify({ id: 1 }));
    const spy = spyOn(projectService, 'createProject').and.returnValue(of({ success: true }));

    component.onSubmit();

    expect(spy).toHaveBeenCalledWith({ ...mockProjectData, adminId: 1 });
    expect(component.isSuccess).toBeTrue();
  });

  it('should display error message on failed project creation', () => {
    const mockProjectData = { name: 'New Project', description: 'Test', startDate: '2025-06-01' };
    component.projectForm.setValue(mockProjectData);
    spyOn(localStorage, 'getItem').and.returnValue(JSON.stringify({ id: 1 }));
    spyOn(projectService, 'createProject').and.returnValue(throwError({ message: 'Error' }));

    component.onSubmit();

    expect(component.isSuccess).toBeFalse();
    expect(component.message).toContain('Error');
  });

  it('should show validation error if form is invalid', () => {
    component.projectForm.setValue({ name: '', description: '', startDate: '' });
    component.onSubmit();

    expect(component.isSuccess).toBeFalse();
    expect(component.message).toBe('Please fix errors before submitting.');
  });

  it('should handle missing currentUser in localStorage', () => {
    spyOn(localStorage, 'getItem').and.returnValue(null);
    const mockProjectData = { name: 'Project', description: 'Description', startDate: '2025-06-01' };
    component.projectForm.setValue(mockProjectData);

    component.onSubmit();

    expect(component.isSuccess).toBeFalse();
    expect(component.message).toContain('User not connected or information missing.');
  });

  it('should handle malformed currentUser JSON', () => {
    spyOn(localStorage, 'getItem').and.returnValue('invalid-json');
    const mockProjectData = { name: 'Project', description: 'Description', startDate: '2025-06-01' };
    component.projectForm.setValue(mockProjectData);

    component.onSubmit();

    expect(component.isSuccess).toBeFalse();
    expect(component.message).toBe('Error.');
  });

  it('should handle currentUser missing id', () => {
    spyOn(localStorage, 'getItem').and.returnValue(JSON.stringify({ name: 'Test' }));
    const mockProjectData = { name: 'Project', description: 'Description', startDate: '2025-06-01' };
    component.projectForm.setValue(mockProjectData);

    component.onSubmit();

    expect(component.isSuccess).toBeFalse();
    expect(component.message).toContain('User not connected or information missing.');
  });
});
