import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private baseUrl = 'http://localhost:8080/api/tasks';

  constructor(private http: HttpClient) {}

  createTask(taskData: any, projectId: number, userId: number): Observable<any> {
    const params = new HttpParams()
      .set('projectId', projectId)
      .set('userId', userId);

    return this.http.post(`${this.baseUrl}`, taskData, { params });
  }

  getProjectMembers(projectId: number) {
    return this.http.get<{ id: number; username: string }[]>(`http://localhost:8080/api/projects/${projectId}/members`);
  }
}
