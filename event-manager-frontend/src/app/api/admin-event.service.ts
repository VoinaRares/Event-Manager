import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

export interface AdminEventPayload {
  name: string;
  startDate: string;
  endDate: string;
  longitude?: number | null;
  latitude?: number | null;
  placeId?: number | null;
  locationName?: string | null;
  organizerIds: number[];
}

@Injectable({ providedIn: 'root' })
export class AdminEventService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/admin/events';

  create(payload: AdminEventPayload): Observable<void> {
    return this.http.post<void>(this.baseUrl, payload);
  }

  delete(eventId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${eventId}`);
  }

  invite(eventId: number, email: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/${eventId}/invite`, { email });
  }
}
