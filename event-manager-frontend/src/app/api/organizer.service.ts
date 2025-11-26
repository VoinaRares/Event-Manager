import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { LoginPayload } from '../domain/LoginPayload';
import { LoginResponse } from '../domain/LoginResponse';

export interface Organizer {
  id: number;
  name: string;
  email: string;
}

export interface OrganizerSignupPayload {
  name: string;
  email: string;
  password: string;
  phone?: string;
}

@Injectable({ providedIn: 'root' })
export class OrganizerService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/organizers';

  list(): Observable<Organizer[]> {
    return this.http.get<Organizer[]>(this.baseUrl);
  }

  register(payload: OrganizerSignupPayload): Observable<Organizer> {
    return this.http.post<Organizer>(this.baseUrl, payload);
  }

  login(payload: LoginPayload): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/auth/login`, payload);
  }
}
