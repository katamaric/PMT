import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  registerForm: FormGroup;
  errorMessage: string | undefined;
  message: string = '';
  isSuccess: boolean = false;


  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(100)]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  onSubmit() {
    if (this.registerForm.valid) {
      this.authService.register(this.registerForm.value).subscribe({
        next: (response) => {
          this.isSuccess = true;
          this.message = 'Registration successful!';

          setTimeout(() => {
            this.router.navigate(['/login']); // Redirect to login after successful registration
          }, 1500);

          this.registerForm.reset();
        },
        error: (err) => {
          this.isSuccess = false;

          // Check if error message
          if (err.error && typeof err.error === 'string') {
            this.message = err.error;
          } else if (err.error?.message) {
            this.message = err.error.message;
          } else {
            this.message = 'An error occurred. Please try again.';
          }
        }
      });
    }
  }
}
