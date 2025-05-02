import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TaskService } from '../task.service';

@Component({
  selector: 'app-task-detail',
  templateUrl: './task-detail.component.html',
  styleUrls: ['./task-detail.component.scss']
})
export class TaskDetailComponent implements OnInit {
  task: any = null;
  currentUser: any = null;
  statuses = ['TO_DO', 'IN_PROGRESS', 'COMPLETED'];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private taskService: TaskService
  ) {}

  ngOnInit(): void {
    const rawUser = localStorage.getItem('currentUser');
    if (rawUser) this.currentUser = JSON.parse(rawUser);

    const taskId = this.route.snapshot.paramMap.get('taskId');
    if (taskId && this.currentUser) {
      this.taskService.getTaskById(+taskId, this.currentUser.id).subscribe({
        next: (res) => this.task = res,
        error: (err) => console.error('Failed to load task', err)
      });
    }
  }

  updateTask(): void {
    if (!this.task || !this.currentUser) return;

    this.taskService.updateTask(this.task.id, this.task, this.currentUser.id).subscribe({
      next: () => {
        alert('Task updated successfully.');
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        console.error('Failed to update task', err);
        alert('There was an error updating the task.');
      }
    });
  }
}
