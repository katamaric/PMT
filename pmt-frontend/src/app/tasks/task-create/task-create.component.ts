import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TaskService } from '../task.service';

@Component({
  selector: 'app-task-create',
  templateUrl: './task-create.component.html',
  styleUrls: ['./task-create.component.scss']
})
export class TaskCreateComponent implements OnInit {
  taskForm: FormGroup;
  message: string = '';
  isSuccess: boolean = false;
  projectId!: number;
  userId!: number;
  projectMembers: { id: number; username: string }[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private taskService: TaskService
  ) {
    this.taskForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      dueDate: [''],
      priority: [3, [Validators.min(1), Validators.max(5)]],
      assignedTo: [null],
      status: ['TO_DO']
    });
  }

  ngOnInit(): void {
    this.projectId = +this.route.snapshot.paramMap.get('id')!;
    const rawUser = localStorage.getItem('currentUser');
    if (rawUser) {
      this.userId = JSON.parse(rawUser).id;
    }
    
    this.taskService.getProjectMembers(this.projectId).subscribe({
    next: members => this.projectMembers = members,
    error: err => console.error('Failed to load members', err)
  });
  }

  onSubmit(): void {
    if (this.taskForm.valid) {
      const taskData = {
        ...this.taskForm.value,
        status: this.taskForm.value.status,
        projectId: this.projectId,
        assignedTo: this.taskForm.value.assignedTo || this.userId
      };

      this.taskService.createTask(taskData, this.projectId, this.userId).subscribe({
        next: () => {
          this.message = 'Task created successfully!';
          this.isSuccess = true;
          this.taskForm.reset({ priority: 3 });
          this.router.navigate(['/dashboard']);
        },
        error: err => {
          this.message = err.error || 'Task creation failed.';
          this.isSuccess = false;
        }
      });
    }
  }
}
