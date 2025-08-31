import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ProjectService } from '../project.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-project-invite',
  templateUrl: './project-invite.component.html',
  styleUrls: ['./project-invite.component.scss']
})
export class ProjectInviteComponent implements OnInit {
  inviteForm: FormGroup;
  message: string = '';
  isSuccess: boolean = false;
  projectId!: number;

  roles = ['ADMIN', 'MEMBER', 'OBSERVER'];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private projectService: ProjectService,
    private router: Router 
  ) {
    this.inviteForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      role: ['MEMBER', Validators.required]  // default role
    });
  }

  ngOnInit(): void {
    this.projectId = +this.route.snapshot.paramMap.get('id')!;
  }

  onSubmit(): void {
    if (this.inviteForm.valid) {
      const { email, role } = this.inviteForm.value;
      const rawUser = localStorage.getItem('currentUser');

      if (!rawUser) {
        this.message = 'You must be logged in.';
        this.isSuccess = false;
        return;
      }

      const currentUser = JSON.parse(rawUser);
      const adminId = currentUser.id;

      this.projectService.inviteMember(this.projectId, email, role, adminId).subscribe({
        next: () => {
          this.message = 'Invitation sent successfully!';
          this.isSuccess = true;
          this.inviteForm.reset({ role: 'MEMBER' });
          setTimeout(() => this.router.navigate(['/dashboard']), 2000);
        },
        error: err => {
          this.message = err.error || 'An error occurred. Please try again.';
          this.isSuccess = false;
        }
      });
    } else {
      this.inviteForm.markAllAsTouched();
      this.message = 'Please fix errors before submitting.';
      this.isSuccess = false;
    }
  }
}
