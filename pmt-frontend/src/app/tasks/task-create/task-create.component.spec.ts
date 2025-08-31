import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TaskCreateComponent } from './task-create.component';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { TaskService } from '../task.service';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('TaskCreateComponent', () => {
  let component: TaskCreateComponent;
  let fixture: ComponentFixture<TaskCreateComponent>;
  let taskService: TaskService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [TaskCreateComponent],
      providers: [
        FormBuilder,
        {
          provide: TaskService,
          useValue: {
            createTask: jasmine.createSpy('createTask').and.callFake((taskData, projectId, userId) => {
              return of(null); // simulate success
            }),
            getProjectMembers: jasmine.createSpy('getProjectMembers').and.callFake((projectId) => {
              return of([{ id: 1, username: 'user1' }, { id: 2, username: 'user2' }]);
            }),
          }
        },
        { provide: Router, useValue: { navigate: jasmine.createSpy() } },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '1' } } } },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    localStorage.setItem('currentUser', JSON.stringify({ id: 42 }));

    fixture = TestBed.createComponent(TaskCreateComponent);
    component = fixture.componentInstance;
    taskService = TestBed.inject(TaskService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form correctly', () => {
    expect(component.taskForm).toBeDefined();
    expect(component.taskForm.controls['name']).toBeDefined();
    expect(component.taskForm.controls['priority']).toBeDefined();
    expect(component.taskForm.controls['assignedTo']).toBeDefined();
  });

  it('should get project members on init', () => {
    const projectMembers = [{ id: 1, username: 'user1' }, { id: 2, username: 'user2' }];
    
    taskService.getProjectMembers(1).subscribe(members => {
      expect(members).toEqual(projectMembers);
    });

    expect(taskService.getProjectMembers).toHaveBeenCalledWith(1);
  });

  it('should submit successfully when form is valid', () => {
    component.taskForm.patchValue({ name: 'New Task', assignedTo: 1 });
    component.onSubmit();

    expect(taskService.createTask).toHaveBeenCalledWith(
      jasmine.objectContaining({
        name: 'New Task',
        assignedTo: 1,
        projectId: 1
      }),
      1,
      42
    );
    expect(component.isSuccess).toBeTrue();
    expect(component.message).toContain('successfully');
  });

  it('should fallback to userId if assignedTo is null', () => {
    component.taskForm.patchValue({ name: 'New Task', assignedTo: null });
    component.onSubmit();

    expect(taskService.createTask).toHaveBeenCalledWith(
      jasmine.objectContaining({ assignedTo: 42 }),
      1,
      42
    );
  });

  it('should handle service error when createTask fails', () => {
    (taskService.createTask as jasmine.Spy).and.returnValue(
      throwError(() => ({ error: 'Task creation failed' }))
    );
    component.taskForm.patchValue({ name: 'Task' });
    component.onSubmit();

    expect(component.isSuccess).toBeFalse();
    expect(component.message).toContain('failed');
  });

  it('should show validation error if form is invalid', () => {
    component.taskForm.patchValue({ name: '' });
    component.onSubmit();

    expect(component.isSuccess).toBeFalse();
    expect(component.message).toBe('Please fix errors before submitting.');
  });

  it('should handle getProjectMembers error', () => {
    (taskService.getProjectMembers as jasmine.Spy).and.returnValue(
      throwError(() => 'service fail')
    );

    component.ngOnInit();
    expect(taskService.getProjectMembers).toHaveBeenCalled();
  });
});
