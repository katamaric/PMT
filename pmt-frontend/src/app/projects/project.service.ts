import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ProjectService {
  private baseUrl = 'http://localhost:8080/api/projects';

  constructor(private http: HttpClient) {}

  createProject(projectData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}`, projectData);
  }

  getProjectsByAdmin(adminId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/admin/${adminId}`);
  }

  getProjectsByUser(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/user/${userId}`);
  }

  inviteMember(projectId: number, email: string, role: string, adminId: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/${projectId}/invite-member?adminId=${adminId}`, {
      email,
      role
    });
  }


}
