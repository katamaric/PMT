import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProjectService } from '../project.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-project-create',
  templateUrl: './project-create.component.html',
  styleUrls: ['./project-create.component.scss']
})
export class ProjectCreateComponent {
  projectForm: FormGroup;
  message: string = '';
  isSuccess: boolean = false;

  constructor(
    private fb: FormBuilder,
    private projectService: ProjectService,
    private router: Router
  ) {
    this.projectForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(100)]],
      description: [''],
      startDate: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.projectForm.valid) {
      let rawUser = localStorage.getItem('currentUser');

      if (rawUser) {
        try {
          // Explicitly parse the JSON
          let currentUser = JSON.parse(rawUser.trim());

          if (!currentUser || !currentUser.id) {
            this.message = 'Utilisateur non connecté ou informations manquantes.';
            this.isSuccess = false;
            return;
          }

          // Proceed with project creation
          const projectData = {
            ...this.projectForm.value,
            adminId: currentUser.id  // Use the logged-in user's ID as adminId
          };

          this.projectService.createProject(projectData).subscribe({
            next: () => {
              this.message = 'Projet created successfully !';
              this.isSuccess = true;
              setTimeout(() => this.router.navigate(['/projects']), 2000);
            },
            error: err => {
              this.message = `Error : ${err.message || 'Please try again.'}`;
              this.isSuccess = false;
            }
          });
        } catch (error) {
          this.message = 'Error.';
          this.isSuccess = false;
        }
      } else {
        this.message = 'User not connected or information missing.';
        this.isSuccess = false;
      }
    }
  }
}
