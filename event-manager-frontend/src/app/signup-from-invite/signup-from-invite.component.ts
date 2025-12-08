import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { EventService } from '../api/event.service';
import { SessionService } from '../session/session.service';

function passwordsMatch(control: AbstractControl): ValidationErrors | null {
  const password = control.get('password')?.value;
  const confirm = control.get('confirmPassword')?.value;
  return password && confirm && password !== confirm ? { passwordMismatch: true } : null;
}

@Component({
  selector: 'app-signup-from-invite',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './signup-from-invite.component.html',
  styleUrl: './signup-from-invite.component.css'
})
export class SignupFromInviteComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly eventService = inject(EventService);
  private readonly sessionService = inject(SessionService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly isSubmitting = signal(false);
  readonly submissionError = signal<string | null>(null);
  readonly submissionSuccess = signal(false);
  readonly invitedEmail = signal<string | null>(null);

  private eventId!: number;
  private token!: string;

  readonly form = this.fb.nonNullable.group(
    {
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]],
    },
    { validators: passwordsMatch }
  );

  readonly passwordMismatch = computed(
    () => this.form.hasError('passwordMismatch') && this.form.get('confirmPassword')?.touched
  );

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      const email = params['email'];
      const eventId = params['eventId'];
      const token = params['token'];

      if (!email || !eventId || !token) {
        this.router.navigate(['/']);
        return;
      }

      this.eventId = Number(eventId);
      this.token = token;
      this.invitedEmail.set(email);

      this.form.patchValue({ email });
      this.form.get('email')?.disable();
    });
  }

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

    const email = (this.invitedEmail() ?? this.form.get('email')!.value) as string;
    const password = this.form.get('password')!.value as string;

    this.isSubmitting.set(true);
    this.eventService
      .signupFromInvite({
        email: email,
        password,
        eventId: this.eventId,
        token: this.token
      })
      .pipe(finalize(() => this.isSubmitting.set(false)))
      .subscribe({
        next: (response) => {
          this.form.reset();
          this.form.markAsPristine();
          this.form.markAsUntouched();
          this.submissionSuccess.set(true);
          localStorage.setItem('access_token', response.accessToken);
          localStorage.setItem('refresh_token', response.refreshToken);
          this.sessionService.setSession({
            role: 'user',
            userId: response.id,
            email: response.email
          });

          setTimeout(() => {
            this.router.navigate(['/home']);
          }, 1500);
        },
        error: (err) => {
          if (err.status === 409) {
            this.submissionError.set('This email is already registered. Please log in instead.');
          } else if (err.status === 410) {
            this.submissionError.set('This invitation has expired. Please request a new one.');
          } else if (err.status === 400) {
            this.submissionError.set('Invalid invitation or email mismatch.');
          } else {
            this.submissionError.set('Unable to complete signup. Please try again later.');
          }
        }
      });
  }

  resetStatus(): void {
    this.submissionError.set(null);
    this.submissionSuccess.set(false);
  }
}
