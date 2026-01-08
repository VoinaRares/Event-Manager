import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { SessionService } from '../session/session.service';

import {
  AdminEventSummary,
  AdminHomeResponse,
  DashboardService
} from '../api/dashboard.service';
import { Organizer, OrganizerService } from '../api/organizer.service';
import { AdminEventPayload, AdminEventService } from '../api/admin-event.service';
import {
  LocationPickerComponent,
  SelectedLocation
} from '../maps/location-picker.component';

@Component({
  selector: 'app-admin-home',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, LocationPickerComponent],
  templateUrl: './admin-home.component.html',
  styleUrl: './admin-home.component.css'
})
export class AdminHomeComponent implements OnInit {
  private readonly dashboardService = inject(DashboardService);
  private readonly organizerService = inject(OrganizerService);
  private readonly adminEventService = inject(AdminEventService);
  private readonly session = inject(SessionService);

  readonly organizers = signal<Organizer[]>([]);
  readonly selectedOrganizerId = signal<number | null>(null);
  readonly dashboard = signal<AdminHomeResponse | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly creatingEvent = signal(false);
  readonly createSuccess = signal<string | null>(null);
  readonly createError = signal<string | null>(null);
  readonly deletingEventId = signal<number | null>(null);
  readonly inviteState = signal<Record<number, InviteState>>({});
  readonly locationSelection = signal<SelectedLocation | null>(null);

  eventDraft = {
    name: '',
    startDate: '',
    endDate: '',
    locationName: '',
    coHostId: ''
  };

  inviteDrafts: Record<number, string> = {};

  ngOnInit(): void {
    this.organizerService.list().subscribe({
      next: (list) => {
        this.organizers.set(list);
        if (list.length) {
          const role = this.session.role();
          const currentUserId = this.session.userId();
          if (role === 'organizer' && currentUserId) {
            const found = list.find((o) => o.id === currentUserId);
            if (found) {
              this.changeOrganizer(found.id);
              return;
            }
          }

          this.changeOrganizer(list[0].id);
        }
      },
      error: () => this.error.set('Unable to load organizers. Please try again later.')
    });
  }

  changeOrganizer(id: number | string): void {
    const numericId = Number(id);
    if (!numericId || Number.isNaN(numericId) || this.loading()) {
      return;
    }
    this.selectedOrganizerId.set(numericId);
    this.fetchDashboard(numericId);
  }

  trackEvents(_index: number, event: AdminEventSummary): number {
    return event.eventId;
  }

  createEvent(): void {
    this.createError.set(null);
    this.createSuccess.set(null);

    const organizerId = this.selectedOrganizerId();
    const coHostId = Number(this.eventDraft.coHostId);
    if (!organizerId) {
      this.createError.set('Select a primary organizer first.');
      return;
    }
    if (!coHostId || Number.isNaN(coHostId)) {
      this.createError.set('Choose a co-host organizer.');
      return;
    }
    if (coHostId === organizerId) {
      this.createError.set('The co-host must be different from the primary organizer.');
      return;
    }
    if (!this.eventDraft.name.trim() || !this.eventDraft.startDate || !this.eventDraft.endDate) {
      this.createError.set('Name, start date and end date are required.');
      return;
    }
    const payload: AdminEventPayload = {
      name: this.eventDraft.name.trim(),
      startDate: this.eventDraft.startDate,
      endDate: this.eventDraft.endDate,
      locationName:
        this.eventDraft.locationName.trim() ||
        this.locationSelection()?.displayName ||
        null,
      latitude: this.locationSelection()?.latitude ?? null,
      longitude: this.locationSelection()?.longitude ?? null,
      placeId: null,
      organizerIds: [organizerId, coHostId]
    };

    this.creatingEvent.set(true);
    this.adminEventService.create(payload).subscribe({
      next: () => {
        this.createSuccess.set('Event created successfully.');
        this.resetDraft();
        this.fetchDashboard(organizerId);
        this.creatingEvent.set(false);
      },
      error: () => {
        this.createError.set('Unable to create the event. Please try again.');
        this.creatingEvent.set(false);
      }
    });
  }

  deleteEvent(eventId: number): void {
    const organizerId = this.selectedOrganizerId();
    if (!organizerId) {
      return;
    }
    if (!window.confirm('Delete this event? This cannot be undone.')) {
      return;
    }
    this.deletingEventId.set(eventId);
    this.adminEventService.delete(eventId).subscribe({
      next: () => {
        this.fetchDashboard(organizerId);
        this.deletingEventId.set(null);
      },
      error: () => {
        this.createError.set('Unable to delete the event. Please try again.');
        this.deletingEventId.set(null);
      }
    });
  }

  sendInvitation(eventId: number): void {
    const email = (this.inviteDrafts[eventId] || '').trim();
    if (!email) {
      this.updateInviteState(eventId, { error: 'Enter an email address.' });
      return;
    }
    this.updateInviteState(eventId, { loading: true, error: undefined, success: undefined });
    this.adminEventService.invite(eventId, email).subscribe({
      next: () => {
        this.inviteDrafts[eventId] = '';
        this.updateInviteState(eventId, { loading: false, success: 'Invitation sent.' });
        const organizerId = this.selectedOrganizerId();
        if (organizerId) {
          this.fetchDashboard(organizerId);
        }
      },
      error: (error) => {
        const message =
          error.status === 400
            ? 'User already invited or invalid email.'
            : 'Unable to send invitation. Please try again.';
        this.updateInviteState(eventId, { loading: false, error: message });
      }
    });
  }

  onLocationSelected(location: SelectedLocation): void {
    this.locationSelection.set(location);
    this.eventDraft.locationName = location.displayName;
  }

  onLocationCleared(): void {
    this.locationSelection.set(null);
  }

  mapLink(event: AdminEventSummary): string | null {
    if (event.latitude == null || event.longitude == null) {
      return null;
    }

    return `https://www.google.com/maps/search/?api=1&query=${event.latitude},${event.longitude}`;
  }

  private fetchDashboard(organizerId: number): void {
    this.loading.set(true);
    this.error.set(null);
    this.dashboard.set(null);

    this.dashboardService.getHome(organizerId).subscribe({
      next: (response) => {
        this.dashboard.set(response);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Unable to load dashboard data. Please try again.');
        this.loading.set(false);
      }
    });
  }

  private resetDraft(): void {
    this.eventDraft = {
      name: '',
      startDate: '',
      endDate: '',
      locationName: '',
      coHostId: ''
    };
    this.locationSelection.set(null);
  }

  private updateInviteState(eventId: number, state: Partial<InviteState>): void {
    const next = { ...this.inviteState() };
    const previous: InviteState = next[eventId] ?? { loading: false };
    next[eventId] = { ...previous, ...state };
    this.inviteState.set(next);
  }
}

interface InviteState {
  loading: boolean;
  success?: string;
  error?: string;
}
