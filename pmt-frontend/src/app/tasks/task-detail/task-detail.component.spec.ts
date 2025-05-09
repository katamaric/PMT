import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TaskDetailComponent } from './task-detail.component';
import { TaskService } from '../task.service';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('TaskDetailComponent', () => {
  let component: TaskDetailComponent;
  let fixture: ComponentFixture<TaskDetailComponent>;
  let taskService: TaskService;
  let router: Router;

  const taskMock = {
    id: 1,
    name: 'Task 1',
    description: 'Test task',
    status: 'TO_DO',
    priority: 3,
    dueDate: '2025-05-10',
    endDate: '2025-05-12',
  };

  const taskHistoryMock = [
    { modifiedBy: { username: 'user1' }, changes: 'Updated status', timestamp: '2025-05-08T12:00:00' },
    { modifiedBy: { username: 'user2' }, changes: 'Changed due date', timestamp: '2025-05-09T14:00:00' }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TaskDetailComponent],
      imports: [FormsModule],
      providers: [
        {
          provide: TaskService,
          useValue: {
            getTaskById: jasmine.createSpy('getTaskById').and.returnValue(of(taskMock)),
            getTaskHistory: jasmine.createSpy('getTaskHistory').and.returnValue(of(taskHistoryMock)),
            updateTask: jasmine.createSpy('updateTask').and.returnValue(of(null)),
          }
        },
        { provide: Router, useValue: { navigate: jasmine.createSpy() } },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '1' } } } },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskDetailComponent);
    component = fixture.componentInstance;
    taskService = TestBed.inject(TaskService);
    router = TestBed.inject(Router);

    localStorage.setItem('currentUser', JSON.stringify({ id: 1 }));
    component.ngOnInit();
    fixture.detectChanges();
  });

  it('should fetch task and load task history on init', () => {
    expect(component.task).toEqual(taskMock);
    expect(component.taskHistory).toEqual(taskHistoryMock);
  });

  it('should update the task successfully', () => {
    // Simulate form values being filled out
    component.task.name = 'Updated Task';
    component.task.status = 'IN_PROGRESS';

    // Trigger the update task method
    component.updateTask();

    // Check if updateTask was called with correct parameters
    expect(taskService.updateTask).toHaveBeenCalledWith(1, component.task, 1);
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
  });
});
