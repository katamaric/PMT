import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { TaskDetailComponent } from './task-detail.component';
import { TaskService } from '../task.service';
import { Router, ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('TaskDetailComponent', () => {
  let component: TaskDetailComponent;
  let fixture: ComponentFixture<TaskDetailComponent>;
  let taskService: jasmine.SpyObj<TaskService>;
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
    const taskServiceSpy = jasmine.createSpyObj('TaskService', [
      'getTaskById',
      'getTaskHistory',
      'updateTask'
    ]);

    await TestBed.configureTestingModule({
      declarations: [TaskDetailComponent],
      imports: [ReactiveFormsModule],
      providers: [
        { provide: TaskService, useValue: taskServiceSpy },
        { provide: Router, useValue: { navigate: jasmine.createSpy() } },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '1' } } } },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    taskService = TestBed.inject(TaskService) as jasmine.SpyObj<TaskService>;
    taskService.getTaskById.and.returnValue(of(taskMock));
    taskService.getTaskHistory.and.returnValue(of(taskHistoryMock));
    taskService.updateTask.and.returnValue(of({}));

    router = TestBed.inject(Router);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskDetailComponent);
    component = fixture.componentInstance;
    localStorage.setItem('currentUser', JSON.stringify({ id: 1 }));
  });

  it('should fetch task and load task history on init', () => {
    component.ngOnInit();
    expect(taskService.getTaskById).toHaveBeenCalledWith(1, 1);
    expect(component.taskForm.value.name).toEqual(taskMock.name);
    expect(component.taskHistory).toEqual(taskHistoryMock);
  });

  it('should update the task successfully', fakeAsync(() => {
    component.ngOnInit();
    component.taskForm.patchValue({ name: 'Updated Task', status: 'IN_PROGRESS' });
    component.updateTask();
    tick(1500);

    expect(taskService.updateTask).toHaveBeenCalledWith(1, component.taskForm.value, 1);
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
    expect(component.isSuccess).toBeTrue();
  }));

  it('should show an error message if updateTask fails', () => {
    taskService.updateTask.and.returnValue(throwError(() => ({ error: 'Update failed' })));
    component.ngOnInit();
    component.taskForm.patchValue({ name: 'Updated', status: 'IN_PROGRESS' });
    component.updateTask();

    expect(component.isSuccess).toBeFalse();
    expect(component.message).toContain('error updating');
  });

  it('should show validation error if form is invalid on submit', () => {
    component.ngOnInit();
    component.taskForm.patchValue({ name: '' }); // invalid
    component.updateTask();

    expect(component.isSuccess).toBeFalse();
    expect(component.message).toBe('Please fix errors before submitting.');
  });

  it('should handle error when getTaskById fails', () => {
    taskService.getTaskById.and.returnValue(throwError(() => 'error'));
    component.ngOnInit();

    expect(component.taskForm).toBeUndefined();
  });

  it('should handle error when getTaskHistory fails', () => {
    taskService.getTaskHistory.and.returnValue(throwError(() => 'error'));
    component.loadTaskHistory(1);

    expect(component.taskHistory).toEqual([]);
  });

  it('should not fetch task if currentUser is missing', () => {
    localStorage.removeItem('currentUser');
    component.ngOnInit();

    expect(taskService.getTaskById).not.toHaveBeenCalled();
  });

  it('should not initialize form if taskIdParam is missing', () => {
    const route = TestBed.inject(ActivatedRoute);
    spyOn(route.snapshot.paramMap, 'get').and.returnValue(null);
  
    component.ngOnInit();
    expect(component.taskForm).toBeUndefined();
  });

  it('should return early if taskForm is missing on update', () => {
    component.taskForm = undefined as any;
    component.updateTask();
    expect(component.isSuccess).toBeFalse();
  });
  
  it('should return early if currentUser is missing on update', () => {
    component.ngOnInit();
    component.currentUser = null;
    component.updateTask();
    expect(component.isSuccess).toBeFalse();
  });

  it('should not initialize form if taskIdParam is missing', () => {
    const route = TestBed.inject(ActivatedRoute);
    spyOn(route.snapshot.paramMap, 'get').and.returnValue(null);

    component.ngOnInit();
    expect(component.taskForm).toBeUndefined();
  });

  it('should return early from updateTask if taskForm is missing', () => {
    component.taskForm = undefined as any;
    component.updateTask();
    expect(component.isSuccess).toBeFalse();
  });

  it('should return early from updateTask if currentUser is missing', () => {
    component.currentUser = null;
    component.updateTask();
    expect(component.isSuccess).toBeFalse();
  });

  it('should handle task with missing description and dueDate', () => {
    const incompleteTask = { ...taskMock, description: undefined, dueDate: undefined };
    component['initForm'](incompleteTask);
    expect(component.taskForm.value.description).toBe('');
    expect(component.taskForm.value.dueDate).toBe('');
  });
});

