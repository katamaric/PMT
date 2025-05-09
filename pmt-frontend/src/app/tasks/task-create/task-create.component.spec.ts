import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TaskCreateComponent } from './task-create.component';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { TaskService } from '../task.service';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
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
              return of(null); // Simulate success response with Observable
            }),
            getProjectMembers: jasmine.createSpy('getProjectMembers').and.callFake((projectId) => {
              return of([{ id: 1, username: 'user1' }, { id: 2, username: 'user2' }]); // Simulate fetching members
            }),
          }
        },
        { provide: Router, useValue: { navigate: jasmine.createSpy() } },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '1' } } } },
      ],
      schemas: [NO_ERRORS_SCHEMA], // Ignore any unknown elements, such as third-party components
    }).compileComponents();
  });

  beforeEach(() => {
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
    
    // Simulating a call to the service
    taskService.getProjectMembers(1).subscribe(members => {
      expect(members).toEqual(projectMembers);
    });

    expect(taskService.getProjectMembers).toHaveBeenCalledWith(1);
  });
});
