import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs/operators';
import { UserService } from '../api/user.service';
import { LoginPayload } from '../domain/LoginPayload';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login-user.component.html',
  styleUrl: './login-user.component.css',
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);

  readonly isSubmitting = signal(false);
  readonly submissionError = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });

  private readonly router = inject(Router);
  onSubmit(): void {
    this.submissionError.set(null);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    if (this.isSubmitting()) {
      return;
    }

    const payload: LoginPayload = {
      email: this.form.controls.email.value,
      password: this.form.controls.password.value
    };

    this.isSubmitting.set(true);
    this.userService
      .login(payload)
      .pipe(finalize(() => this.isSubmitting.set(false)))
      .subscribe({
        next: (response) => {
          localStorage.setItem('access_token', response.accessToken);
          localStorage.setItem('refresh_token', response.refreshToken);

          localStorage.setItem('user', JSON.stringify({
            id: response.id,
            email: response.email
          }));
          this.router.navigate(['/home']);
        },
        error: (error) => {
          if (error.status === 401) {
            this.submissionError.set('Incorrect email or password.');
          } else if (error.status === 0) {
            this.submissionError.set('Unable to reach the server. Check that the backend is running.');
          } else {
            this.submissionError.set('Something went wrong. Please try again.');
          }
        }
      });
  }

  resetStatus(): void {
    this.submissionError.set(null);
  }
}
