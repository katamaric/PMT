import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DashboardComponent } from './dashboard.component';
import { Router } from '@angular/router';
import { ProjectService } from '../projects/project.service';
import { TaskService } from '../tasks/task.service';
import { of, throwError } from 'rxjs';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let mockRouter: any;
  let mockProjectService: any;
  let mockTaskService: any;

  beforeEach(async () => {
    mockRouter = { navigate: jasmine.createSpy('navigate') };
    mockProjectService = {
      getProjectsByAdmin: jasmine.createSpy(),
      getProjectsByUser: jasmine.createSpy()
    };
    mockTaskService = {
      getTasksByProject: jasmine.createSpy()
    };

    await TestBed.configureTestingModule({
      declarations: [DashboardComponent],
      providers: [
        { provide: Router, useValue: mockRouter },
        { provide: ProjectService, useValue: mockProjectService },
        { provide: TaskService, useValue: mockTaskService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect to login if no user in localStorage', () => {
    spyOn(localStorage, 'getItem').and.returnValue(null);
    component.ngOnInit();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should call loadAdminProjects and loadUserProjects if user exists', () => {
    const fakeUser = { id: 1, username: 'test', role: 'ADMIN' };
    spyOn(localStorage, 'getItem').and.returnValue(JSON.stringify(fakeUser));

    mockProjectService.getProjectsByAdmin.and.returnValue(of([]));
    mockProjectService.getProjectsByUser.and.returnValue(of([]));

    component.ngOnInit();

    expect(mockProjectService.getProjectsByAdmin).toHaveBeenCalledWith(1);
    expect(mockProjectService.getProjectsByUser).toHaveBeenCalledWith(1);
  });

  it('should filter out admin projects from otherProjects', () => {
    const fakeUser = { id: 1, username: 'test', role: 'ADMIN' };
    const fakeProjects = [
      { id: 10, name: 'Admin Project', admin: { id: 1 } },
      { id: 11, name: 'Other Project', admin: { id: 2 } }
    ];

    spyOn(localStorage, 'getItem').and.returnValue(JSON.stringify(fakeUser));
    mockProjectService.getProjectsByAdmin.and.returnValue(of([]));
    mockProjectService.getProjectsByUser.and.returnValue(of(fakeProjects));
    mockTaskService.getTasksByProject.and.returnValue(of([]));

    component.ngOnInit();

    expect(component.otherProjects.length).toBe(1);
    expect(component.otherProjects[0].id).toBe(11);
  });

  it('should load tasks for each admin project', () => {
    const fakeUser = { id: 1, username: 'test', role: 'ADMIN' };
    const fakeProjects = [{ id: 10 }, { id: 20 }];
    spyOn(localStorage, 'getItem').and.returnValue(JSON.stringify(fakeUser));
    mockProjectService.getProjectsByAdmin.and.returnValue(of(fakeProjects));
    mockProjectService.getProjectsByUser.and.returnValue(of([]));
    mockTaskService.getTasksByProject.and.returnValue(of([]));

    component.ngOnInit();

    expect(mockTaskService.getTasksByProject).toHaveBeenCalledWith(10, 1);
    expect(mockTaskService.getTasksByProject).toHaveBeenCalledWith(20, 1);
  });

  it('should load tasks for each other project', () => {
    const fakeUser = { id: 1, username: 'test', role: 'ADMIN' };
    const fakeProjects = [
      { id: 15, admin: { id: 2 } }
    ];

    spyOn(localStorage, 'getItem').and.returnValue(JSON.stringify(fakeUser));
    mockProjectService.getProjectsByAdmin.and.returnValue(of([]));
    mockProjectService.getProjectsByUser.and.returnValue(of(fakeProjects));
    mockTaskService.getTasksByProject.and.returnValue(of([]));

    component.ngOnInit();

    expect(mockTaskService.getTasksByProject).toHaveBeenCalledWith(15, 1);
  });

  it('should handle error if getProjectsByUser fails', () => {
    const fakeUser = { id: 1, username: 'test', role: 'ADMIN' };
    spyOn(localStorage, 'getItem').and.returnValue(JSON.stringify(fakeUser));
    mockProjectService.getProjectsByAdmin.and.returnValue(of([]));
    mockProjectService.getProjectsByUser.and.returnValue(throwError(() => new Error('fail')));
    spyOn(console, 'error');

    component.ngOnInit();

    expect(console.error).toHaveBeenCalledWith('Error fetching user projects', jasmine.any(Error));
  });
});
