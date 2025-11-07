import { Component, computed, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ValidationErrors, Validators } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import {CommonModule} from '@angular/common'

import { SignupPayload, UserService } from '../api/user.service';

function passwordsMatch(control: AbstractControl): ValidationErrors | null {
  const password = control.get('password')?.value;
  const confirm = control.get('confirmPassword')?.value;
  return password && confirm && password !== confirm ? { passwordMismatch: true } : null;
}

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.css'
})
export class SignupComponent {
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);

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
        next: () => {
          this.form.reset({ email: '', password: '', confirmPassword: '' });
          this.form.markAsPristine();
          this.form.markAsUntouched();
          this.submissionSuccess.set(true);
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
