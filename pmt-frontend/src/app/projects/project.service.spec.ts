import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProjectService } from './project.service';

describe('ProjectService', () => {
  let service: ProjectService;
  let httpMock: HttpTestingController;

  const mockBaseUrl = 'http://localhost:8080/api/projects';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProjectService]
    });
    service = TestBed.inject(ProjectService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create a project', () => {
    const projectData = { name: 'New Project', description: 'Test Description', startDate: '2025-06-01' };
    service.createProject(projectData).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(`${mockBaseUrl}`);
    expect(req.request.method).toBe('POST');
    req.flush({ success: true });
  });

  it('should get projects by admin ID', () => {
    const adminId = 1;
    service.getProjectsByAdmin(adminId).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(`${mockBaseUrl}/admin/${adminId}`);
    expect(req.request.method).toBe('GET');
    req.flush([{ id: 1, name: 'Admin Project' }]);
  });

  it('should get projects by user ID', () => {
    const userId = 1;
    service.getProjectsByUser(userId).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(`${mockBaseUrl}/user/${userId}`);
    expect(req.request.method).toBe('GET');
    req.flush([{ id: 1, name: 'User Project' }]);
  });

  it('should invite a member to a project', () => {
    const projectId = 1;
    const email = 'test@example.com';
    const role = 'MEMBER';
    const adminId = 1;
    service.inviteMember(projectId, email, role, adminId).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(`${mockBaseUrl}/${projectId}/invite-member?adminId=${adminId}`);
    expect(req.request.method).toBe('POST');
    req.flush({ success: true });
  });
});
