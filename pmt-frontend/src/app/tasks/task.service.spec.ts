import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TaskService } from './task.service';

describe('TaskService', () => {
  let service: TaskService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TaskService],
    });
    service = TestBed.inject(TaskService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Ensures no outstanding requests remain
  });

  it('should create a task', () => {
    const taskData = { name: 'Task 1', description: 'Test Task' };
    const projectId = 1;
    const userId = 1;
    
    service.createTask(taskData, projectId, userId).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((request) => 
      request.method === 'POST' &&
      request.url === 'http://localhost:8080/api/tasks' &&
      request.params.has('projectId') &&
      request.params.get('projectId') === '1' &&
      request.params.has('userId') &&
      request.params.get('userId') === '1'
    );
    req.flush({}); // Mock a successful response
  });

  it('should get project members', () => {
    const projectId = 1;
    const mockMembers = [
      { id: 1, username: 'User1' },
      { id: 2, username: 'User2' }
    ];

    service.getProjectMembers(projectId).subscribe(members => {
      expect(members.length).toBe(2);
      expect(members[0].username).toBe('User1');
    });

    const req = httpMock.expectOne({
      method: 'GET',
      url: `http://localhost:8080/api/projects/${projectId}/members`,
    });
    req.flush(mockMembers); // Mock response
  });

  it('should get tasks by project', () => {
    const projectId = 1;
    const userId = 1;
    const mockTasks = [{ id: 1, name: 'Task 1' }, { id: 2, name: 'Task 2' }];

    service.getTasksByProject(projectId, userId).subscribe(tasks => {
      expect(tasks.length).toBe(2);
      expect(tasks[0].name).toBe('Task 1');
    });

    const req = httpMock.expectOne((request) => 
      request.method === 'GET' &&
      request.url === `http://localhost:8080/api/tasks/project/${projectId}` &&
      request.params.has('userId') &&
      request.params.get('userId') === '1'
    );
    req.flush(mockTasks); // Mock response
  });

  it('should get task by ID', () => {
    const taskId = 1;
    const userId = 1;
    const mockTask = { id: taskId, name: 'Task 1' };

    service.getTaskById(taskId, userId).subscribe(task => {
      expect(task.id).toBe(taskId);
      expect(task.name).toBe('Task 1');
    });

    const req = httpMock.expectOne((request) => 
      request.method === 'GET' &&
      request.url === `http://localhost:8080/api/tasks/${taskId}` &&
      request.params.has('userId') &&
      request.params.get('userId') === '1'
    );
    req.flush(mockTask); // Mock response
  });

  it('should get task history', () => {
    const taskId = 1;
    const mockHistory = [{ id: 1, action: 'Created' }, { id: 2, action: 'Updated' }];

    service.getTaskHistory(taskId).subscribe(history => {
      expect(history.length).toBe(2);
      expect(history[0].action).toBe('Created');
    });

    const req = httpMock.expectOne({
      method: 'GET',
      url: `http://localhost:8080/api/tasks/${taskId}/history`,
    });

    req.flush(mockHistory); // Mock response
  });

  it('should update a task', () => {
    const taskId = 1;
    const updatedTask = { name: 'Updated Task' };
    const userId = 1;

    service.updateTask(taskId, updatedTask, userId).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne((request) => 
      request.method === 'PUT' &&
      request.url === `http://localhost:8080/api/tasks/${taskId}` &&
      request.params.has('userId') &&
      request.params.get('userId') === '1'
    );
    req.flush({}); // Mock successful response
  });
});
