import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ProjectService } from '../projects/project.service';
import { TaskService } from '../tasks/task.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  currentUser: any = null;
  adminProjects: any[] = [];
  projectTasks: { [projectId: number]: any[] } = {};

  constructor(
    private router: Router,
    private projectService: ProjectService,
    private taskService: TaskService
  ) {}

  ngOnInit(): void {
    const rawUser = localStorage.getItem('currentUser');

    if (rawUser) {
      try {
        this.currentUser = JSON.parse(rawUser.trim());
        this.loadAdminProjects(this.currentUser.id);
      } catch (error) {
        console.error('Error parsing user data:', error);
        this.router.navigate(['/login']);
      }
    } else {
      this.router.navigate(['/login']);
    }
  }

  loadAdminProjects(adminId: number): void {
    this.projectService.getProjectsByAdmin(adminId).subscribe({
      next: (projects) => {
        this.adminProjects = projects;
        
        for (const project of this.adminProjects) {
          this.loadTasksForProject(project.id, this.currentUser.id);
        }
      },
      error: (err) => {
        console.error('Error fetching admin projects', err);
      }
    });
  }

  loadTasksForProject(projectId: number, userId: number): void {
    this.taskService.getTasksByProject(projectId, userId).subscribe({
      next: (tasks) => {
        this.projectTasks[projectId] = tasks;
      },
      error: (err) => {
        console.error(`Error loading tasks for project ${projectId}`, err);
      }
    });
  }

  goToCreateProject(): void {
    this.router.navigate(['/projects/create']);
  }

  goToInvite(projectId: number): void {
    this.router.navigate([`/projects/${projectId}/invite`]);
  }
}
