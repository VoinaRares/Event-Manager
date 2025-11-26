import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UserService, UserInvitation } from '../api/user.service';
import { SessionService } from '../session/session.service';

@Component({
  selector: 'app-user-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-home.component.html',
  styleUrls: ['./user-home.component.css']
})
export class UserHomeComponent implements OnInit {
  private readonly userService = inject(UserService);
  protected readonly session = inject(SessionService);
  private readonly sessionService = this.session;

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly pendingEvents = signal<UserInvitation[]>([]);
  readonly confirmedEvents = signal<UserInvitation[]>([]);
  readonly respondingEvent = signal<number | null>(null);
  readonly hasUserAccount = computed(() => !!this.session.session().userId || !!this.session.session().email);
  readonly userEmail = computed(() => this.session.session().email);

  ngOnInit(): void {
    const userId = this.sessionService.userId();
    if (userId) {
      this.fetchInvitations(userId);
      return;
    }
    const email = this.userEmail();
    if (email) {
      this.resolveUserByEmail(email);
    } else {
      this.error.set('Please log in as a user to view invitations.');
      this.loading.set(false);
    }
  }

  accept(eventId: number): void {
    this.respond(eventId, 'accept');
  }

  decline(eventId: number): void {
    this.respond(eventId, 'decline');
  }

  private fetchInvitations(userId: number): void {
    this.loading.set(true);
    this.error.set(null);
    this.userService
      .getInvitations(userId)
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: (response) => {
          this.pendingEvents.set(response.pending);
          this.confirmedEvents.set(response.confirmed);
          this.loading.set(false);
        },
        error: () => {
          this.error.set('An error occurred while loading events.');
          this.loading.set(false);
        }
      });
  }

  private respond(eventId: number, action: 'accept' | 'decline'): void {
    const userId = this.sessionService.userId();
    if (!userId) {
      this.error.set('You must be logged in as a user to respond.');
      return;
    }
    this.respondingEvent.set(eventId);
    this.userService
      .respondToInvitation(userId, eventId, action)
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: () => {
          this.fetchInvitations(userId);
          this.respondingEvent.set(null);
        },
        error: () => {
          this.error.set('Unable to update your response. Please try again.');
          this.respondingEvent.set(null);
        }
      });
  }

  private resolveUserByEmail(email: string): void {
    this.loading.set(true);
    this.userService
      .lookupByEmail(email)
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: (user) => {
          this.sessionService.updateSession({ userId: user.id, email: user.email, role: 'user' });
          this.fetchInvitations(user.id);
        },
        error: () => {
          this.error.set('No account found for this user email. Please sign up.');
          this.loading.set(false);
        }
      });
  }
}
