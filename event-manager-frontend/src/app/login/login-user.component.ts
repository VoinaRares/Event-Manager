import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs/operators';
import { UserService } from '../api/user.service';
import { LoginPayload } from '../domain/LoginPayload';
import { Router } from '@angular/router';
import { OrganizerService } from '../api/organizer.service';

@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login-user.component.html',
  styleUrl: './login-user.component.css',
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly organizerService = inject(OrganizerService);

  readonly isSubmitting = signal(false);
  readonly submissionError = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
    role: ['user']
  });

  private readonly router = inject(Router);
  onSubmit(): void {
    this.submissionError.set(null);

    if (this.form.invalid || this.isSubmitting()) return;

    const payload: LoginPayload = {
      email: this.form.value.email!,
      password: this.form.value.password!
    };

    const role = this.form.value.role!;

    const service = role === 'organizer'
      ? this.organizerService
      : this.userService;

    this.isSubmitting.set(true);

    service.login(payload)
      .pipe(finalize(() => this.isSubmitting.set(false)))
      .subscribe({
        next: (response) => {
          localStorage.setItem('access_token', response.accessToken);
          localStorage.setItem('refresh_token', response.refreshToken);
          localStorage.setItem('role', role);

          this.router.navigate(role === 'organizer' ? ['/admin'] : ['/home']);
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
