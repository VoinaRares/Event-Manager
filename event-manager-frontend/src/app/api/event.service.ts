import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { EventDto } from '../domain/EventDto';



@Injectable({ providedIn: 'root' })
export class EventService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/events';

  list(): Observable<EventDto[]> {
    return this.http.get<EventDto[]>(this.baseUrl);
  }

  accept(eventId: number, token: string) {
    return this.http.post<void>(`${this.baseUrl}/${eventId}/accept/${token}`, {});
  }

  reject(eventId: number, token: string) {
    return this.http.post<void>(`${this.baseUrl}/${eventId}/reject/${token}`, {});
  }
}
