import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage: string | undefined;
  message: string = '';
  isSuccess: boolean = false;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

onSubmit(): void {
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value).subscribe(
        response => {
          this.isSuccess = true;
          this.message = response;
          // Navigate to dashboard or home
          setTimeout(() => {
            this.router.navigate(['/']);
          }, 1000);
        },
        error => {
          this.isSuccess = false;
          this.message = error.error || 'Login failed. Please try again.';
        }
      );
    }
  }
}
