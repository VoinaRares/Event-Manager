import { Component, computed, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ValidationErrors, Validators } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs/operators';

import { UserService, SignupPayload } from '../api/user.service';
import { SessionService } from '../session/session.service';

function passwordsMatch(control: AbstractControl): ValidationErrors | null {
  const password = control.get('password')?.value;
  const confirm = control.get('confirmPassword')?.value;
  return password && confirm && password !== confirm ? { passwordMismatch: true } : null;
}

@Component({
  selector: 'app-user-signup',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './user-signup.component.html',
  styleUrl: './user-signup.component.css'
})
export class UserSignupComponent {
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly sessionService = inject(SessionService);

  readonly isSubmitting = signal(false);
  readonly submissionError = signal<string | null>(null);
  readonly submissionSuccess = signal(false);

  readonly form = this.fb.nonNullable.group(
    {
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]]
    },
    { validators: passwordsMatch }
  );

  readonly passwordMismatch = computed(
    () => this.form.hasError('passwordMismatch') && this.form.get('confirmPassword')?.touched
  );

  onSubmit(): void {
    this.submissionError.set(null);
    this.submissionSuccess.set(false);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    if (this.isSubmitting()) {
      return;
    }

    const payload: SignupPayload = {
      email: this.form.controls.email.value,
      password: this.form.controls.password.value
    };

    this.isSubmitting.set(true);
    this.userService
      .register(payload)
      .pipe(finalize(() => this.isSubmitting.set(false)))
      .subscribe({
        next: (user) => {
          this.form.reset({ email: '', password: '', confirmPassword: '' });
          this.form.markAsPristine();
          this.form.markAsUntouched();
          this.submissionSuccess.set(true);
          this.sessionService.setSession({ role: 'user', userId: user.id, email: user.email });
        },
        error: (error) => {
          if (error.status === 400) {
            this.submissionError.set('An account with this email already exists.');
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
    this.submissionSuccess.set(false);
  }
}
