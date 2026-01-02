import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs/operators';
import { UserService } from '../api/user.service';
import { LoginPayload } from '../domain/LoginPayload';
import { Router } from '@angular/router';
import { OrganizerService } from '../api/organizer.service';
import { SessionService } from '../session/session.service';

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
  private readonly sessionService = inject(SessionService);

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

    const preferredRole = this.form.value.role! as 'organizer' | 'user';
    this.isSubmitting.set(true);
    this.tryLogin(preferredRole, payload);
  }

  private tryLogin(role: 'organizer' | 'user', payload: LoginPayload): void {
    const service = role === 'organizer' ? this.organizerService : this.userService;
    service.login(payload).subscribe({
      next: (response) => {
        this.isSubmitting.set(false);
        localStorage.setItem('access_token', response.accessToken);
        localStorage.setItem('refresh_token', response.refreshToken);
        this.sessionService.setSession({
          role,
          userId: response.id,
          email: response.email
        });
        this.router.navigate(role === 'organizer' ? ['/admin'] : ['/home']);
      },
      error: (error) => {
        this.isSubmitting.set(false);
        if (error.status === 401) {
          this.submissionError.set('Incorrect email or password for the selected role. Please pick the correct role.');
        } else if (error.status === 0) {
          this.submissionError.set('Unable to reach the server.');
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
