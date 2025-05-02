import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ProjectService } from '../projects/project.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  currentUser: any = null;
  adminProjects: any[] = [];

  constructor(private router: Router, private projectService: ProjectService) {}

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
      },
      error: (err) => {
        console.error('Error fetching admin projects', err);
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
