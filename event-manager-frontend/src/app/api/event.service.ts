import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { EventDto } from '../domain/EventDto';

export interface InvitationInfoDto {
  eventId: number;
  eventName: string;
  startDate: string;
  endDate: string;
  locationName: string;
  invitedEmail: string;
  expiresAt: string;
  expired: boolean;
}

export interface SignupFromInvitePayload {
  email: string;
  password: string;
  eventId: number;
  token: string;
}

@Injectable({ providedIn: 'root' })
export class EventService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/events';
  private readonly invitationBaseUrl = 'http://localhost:8080/api/invitations';

  list(): Observable<EventDto[]> {
    return this.http.get<EventDto[]>(this.baseUrl);
  }

  accept(eventId: number, token: string) {
    return this.http.post<void>(`${this.baseUrl}/${eventId}/accept/${token}`, {});
  }

  reject(eventId: number, token: string) {
    return this.http.post<void>(`${this.baseUrl}/${eventId}/reject/${token}`, {});
  }

  getInvitationInfo(eventId: number, token: string): Observable<InvitationInfoDto> {
    return this.http.get<InvitationInfoDto>(`${this.invitationBaseUrl}/${eventId}/${token}/info`);
  }

  signupFromInvite(payload: SignupFromInvitePayload): Observable<any> {
    return this.http.post<any>(`${this.invitationBaseUrl}/signup-and-accept`, payload);
  }

  declineInvitation(eventId: number, token: string): Observable<void> {
    return this.http.post<void>(`${this.invitationBaseUrl}/${eventId}/${token}/decline`, {});
  }
}
