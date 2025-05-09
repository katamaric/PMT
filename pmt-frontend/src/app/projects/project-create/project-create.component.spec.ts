import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProjectCreateComponent } from './project-create.component';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { ProjectService } from '../project.service';
import { of, throwError } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { By } from '@angular/platform-browser';

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
    const mockProjectData = { 
      name: 'New Project', 
      description: 'Test Description', 
      startDate: '2025-06-01' 
    };

    component.projectForm.setValue(mockProjectData);

    const currentUser = { id: 1 };
    spyOn(localStorage, 'getItem').and.returnValue(JSON.stringify(currentUser));

    const spy = spyOn(projectService, 'createProject').and.returnValue(of({ success: true }));

    component.onSubmit();

    expect(spy).toHaveBeenCalledWith({
      ...mockProjectData, 
      adminId: currentUser.id
    });
  });


  it('should display error message on failed project creation', () => {
    component.projectForm.setValue({ 
      name: 'New Project', 
      description: 'Test Description', 
      startDate: '2025-06-01' 
    });

    const currentUser = { id: 1 };
    spyOn(localStorage, 'getItem').and.returnValue(JSON.stringify(currentUser));

    const spy = spyOn(projectService, 'createProject').and.returnValue(throwError({ message: 'Error' }));
    fixture.detectChanges();  // Trigger change detection

    component.onSubmit();

    // Ensure the error message is displayed in the DOM
    fixture.detectChanges();
    const errorMessage = fixture.debugElement.query(By.css('.error-message')).nativeElement;
    expect(errorMessage.textContent).toContain('Error');
  });

});
