import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
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

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  readonly pendingEvents = signal<UserInvitation[]>([]);
  readonly historyEvents = signal<UserInvitation[]>([]);

  readonly respondingEvent = signal<number | null>(null);

  readonly hasUserAccount = computed(
    () => !!this.session.session().userId || !!this.session.session().email
  );

  readonly userEmail = computed(() => this.session.session().email);

  ngOnInit(): void {
    const userId = this.session.userId();

    if (userId) {
      this.fetchAll(userId);
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

  private fetchAll(userId: number): void {
    this.loading.set(true);
    this.error.set(null);

    this.userService.getInvitations(userId).subscribe({
      next: (response) => {
        this.pendingEvents.set(response.pending);
      },
      error: () => {
        this.error.set('Failed to load invitations.');
      }
    });

    this.userService.getHistory(userId).subscribe({
      next: (response) => {
        this.historyEvents.set(response.confirmed);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Failed to load history.');
        this.loading.set(false);
      }
    });
  }

  private respond(eventId: number, action: 'accept' | 'decline'): void {
    const userId = this.session.userId();

    if (!userId) {
      this.error.set('You must be logged in to respond.');
      return;
    }

    this.respondingEvent.set(eventId);

    this.userService.respondToInvitation(userId, eventId, action).subscribe({
      next: () => {
        this.fetchAll(userId);
        this.respondingEvent.set(null);
      },
      error: () => {
        this.error.set('Unable to update your response.');
        this.respondingEvent.set(null);
      }
    });
  }

  private resolveUserByEmail(email: string): void {
    this.loading.set(true);

    this.userService.lookupByEmail(email).subscribe({
      next: (user) => {
        this.session.updateSession({
          userId: user.id,
          email: user.email,
          role: 'user'
        });

        this.fetchAll(user.id);
      },
      error: () => {
        this.error.set('No account found for this email.');
        this.loading.set(false);
      }
    });
  }
}
