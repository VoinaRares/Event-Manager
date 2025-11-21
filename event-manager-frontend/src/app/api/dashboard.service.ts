import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

export interface AdminStats {
  totalEvents: number;
  activeEvents: number;
  upcomingEvents: number;
  pastEvents: number;
  invitedGuests: number;
  confirmedGuests: number;
  pendingGuests: number;
}

export interface AdminEventSummary {
  eventId: number;
  name: string;
  startDate: string;
  endDate: string;
  status: string;
  longitude: number | null;
  latitude: number | null;
  placeId: number | null;
  locationName: string | null;
  photoCount: number;
  invitedGuests: number;
  confirmedGuests: number;
  pendingGuests: number;
}

export interface AdminHomeResponse {
  organizerId: number;
  organizerName: string;
  organizerEmail: string;
  stats: AdminStats;
  events: AdminEventSummary[];
}

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/admin';

  getHome(organizerId: number): Observable<AdminHomeResponse> {
    const params = new HttpParams().set('organizerId', organizerId);
    return this.http.get<AdminHomeResponse>(`${this.baseUrl}/home`, { params });
  }
}
