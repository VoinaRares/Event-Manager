import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

export interface Organizer {
  id: number;
  name: string;
  email: string;
}

@Injectable({ providedIn: 'root' })
export class OrganizerService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/organizers';

  list(): Observable<Organizer[]> {
    return this.http.get<Organizer[]>(this.baseUrl);
  }
}

