import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EventService } from '../api/event.service';
import { EventDto } from '../domain/EventDto';
import { EventParticipant } from '../domain/EventParticipant';
import { SessionService } from '../session/session.service';

export interface InvitationInfo {
  eventId: number;
  eventName: string;
  startDate: string;
  endDate: string;
  locationName: string;
  invitedEmail: string;
  expiresAt: string;
  expired: boolean;
}

@Component({
  selector: 'app-invite-response',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './invite-response.component.html',
  styleUrl: './invite-response.component.css'
})
export class InviteResponseComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly eventService = inject(EventService);
  private readonly sessionService = inject(SessionService);

  readonly event = signal<EventDto | null>(null);
  readonly invitedParticipant = signal<EventParticipant | null>(null);
  readonly invitationInfo = signal<InvitationInfo | null>(null);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly processing = signal(false);
  readonly actionMessage = signal<string | null>(null);
  readonly isAuthenticated = signal(false);
  readonly requiresSignup = signal(false);

  private eventId!: number;
  private token!: string;

  ngOnInit(): void {
    const ev = this.route.snapshot.paramMap.get('eventId');
    const t = this.route.snapshot.paramMap.get('token')
    if (!ev || !t) {
      this.error.set('Invalid invite link.');
      this.loading.set(false);
      return;
    }
    this.eventId = Number(ev);
    this.token = t;
    const isAuth = this.sessionService.role() !== null;
    this.isAuthenticated.set(isAuth);

    if (isAuth) {
      this.fetchEventForAuthenticated();
    } else {
      this.fetchInvitationInfo();
    }
  }

  private fetchEventForAuthenticated(): void {
    this.loading.set(true);
    this.error.set(null);
    this.eventService.list().subscribe({
      next: (list) => {
        const found = list.find((e) => e.id === this.eventId) ?? null;
        if (!found) {
          this.error.set('Event not found.');
          this.event.set(null);
          this.invitedParticipant.set(null);
          this.loading.set(false);
          return;
        }

        const participant = (found.participants || []).find((p) => p.token === this.token) ?? null;
        if (!participant) {
          this.error.set('No invitation found for this user on this event.');
        }

        this.event.set(found);
        this.invitedParticipant.set(participant);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load event. Please try again later.');
        this.event.set(null);
        this.invitedParticipant.set(null);
        this.loading.set(false);
      }
    });
  }

  private fetchInvitationInfo(): void {
    this.loading.set(true);
    this.error.set(null);
    
    this.eventService.getInvitationInfo(this.eventId, this.token).subscribe({
      next: (info) => {
        this.invitationInfo.set(info);
        if (info.expired) {
          this.error.set('This invitation has expired.');
        }
        this.requiresSignup.set(true);
        this.loading.set(false);
      },
      error: (err) => {
        if (err.status === 404) {
          this.error.set('Invitation not found.');
        } else if (err.status === 410) {
          this.error.set('This invitation has expired.');
        } else if (err.status === 409) {
          this.error.set('This invitation has already been used.');
        } else {
          this.error.set('Unable to load invitation. Please try again later.');
        }
        this.loading.set(false);
      }
    });
  }

  accept(): void {
    if (this.processing()) return;
    if (!this.invitedParticipant()) {
      this.actionMessage.set('You are not invited to this event.');
      return;
    }
    this.processing.set(true);
    this.eventService.accept(this.eventId, this.token).subscribe({
      next: () => {
        this.actionMessage.set('Thanks — your attendance has been recorded.');
        this.processing.set(false);
      },
      error: () => {
        this.actionMessage.set('Unable to record acceptance. Try again later.');
        this.processing.set(false);
      }
    });
  }

  decline(): void {
    if (this.processing()) return;
    if (!this.invitedParticipant()) {
      this.actionMessage.set('You are not invited to this event.');
      return;
    }
    this.processing.set(true);
    this.eventService.reject(this.eventId, this.token).subscribe({
      next: () => {
        this.actionMessage.set('We recorded your response. Sorry to miss you.');
        this.processing.set(false);
      },
      error: () => {
        this.actionMessage.set('Unable to record response. Try again later.');
        this.processing.set(false);
      }
    });
  }

  goToSignup(): void {
    this.router.navigate(['/signup-from-invite'], {
      queryParams: {
        eventId: this.eventId,
        token: this.token,
        email: this.invitationInfo()?.invitedEmail
      }
    });
  }
}

