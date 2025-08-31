import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TaskService } from '../task.service';

@Component({
  selector: 'app-task-detail',
  templateUrl: './task-detail.component.html',
  styleUrls: ['./task-detail.component.scss']
})
export class TaskDetailComponent implements OnInit {
  taskForm!: FormGroup;
  taskId!: number;
  currentUser: any = null;
  taskHistory: any[] = [];
  message: string = '';
  isSuccess: boolean = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private taskService: TaskService
  ) {}

  ngOnInit(): void {
    const rawUser = localStorage.getItem('currentUser');
    if (rawUser) this.currentUser = JSON.parse(rawUser);

    const taskIdParam = this.route.snapshot.paramMap.get('taskId');
    if (taskIdParam && this.currentUser) {
      this.taskId = +taskIdParam;
      this.taskService.getTaskById(this.taskId, this.currentUser.id).subscribe({
        next: (task) => {
          this.initForm(task);
          this.loadTaskHistory(this.taskId);
        },
        error: (err) => console.error('Failed to load task', err)
      });
    }
  }

  private initForm(task: any): void {
    this.taskForm = this.fb.group({
      name: [task.name, Validators.required],
      description: [task.description || ''],
      status: [task.status],
      priority: [task.priority, [Validators.min(1), Validators.max(5)]],
      dueDate: [task.dueDate || ''],
      endDate: [task.endDate || '']
    });
  }

  loadTaskHistory(taskId: number): void {
    this.taskService.getTaskHistory(taskId).subscribe({
      next: (history) => (this.taskHistory = history),
      error: (err) => console.error('Failed to load task history', err)
    });
  }

  updateTask(): void {
    if (!this.taskForm || !this.currentUser) return;

    if (this.taskForm.valid) {
      this.taskService.updateTask(this.taskId, this.taskForm.value, this.currentUser.id).subscribe({
        next: () => {
          this.message = 'Task updated successfully!';
          this.isSuccess = true;
          
          setTimeout(() => {
            this.router.navigate(['/dashboard']);
          }, 1500);
        },
        error: (err) => {
          console.error('Failed to update task', err);
          this.message = 'There was an error updating the task. Check errors in form and/or permission access.';
          this.isSuccess = false;
        }
      });
    } else {
      this.taskForm.markAllAsTouched();
      this.message = 'Please fix errors before submitting.';
      this.isSuccess = false;
    }
  }
}
