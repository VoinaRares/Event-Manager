import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { LoginPayload } from '../domain/LoginPayload';
import { LoginResponse } from '../domain/LoginResponse';

export interface SignupPayload {
  email: string;
  password: string;
}

export interface User {
  id: number;
  email: string;
}

export interface UserInvitation {
  eventId: number;
  name: string;
  startDate: string;
  endDate: string;
  locationName: string | null;
  latitude: number | null;
  longitude: number | null;
  placeId: number | null;
}

export interface UserInvitationsResponse {
  pending: UserInvitation[];
  confirmed: UserInvitation[];
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly usersUrl = 'http://localhost:8080/api/users';

  register(payload: SignupPayload): Observable<User> {
    return this.http.post<User>(this.usersUrl, payload);
  }

  lookupByEmail(email: string): Observable<User> {
    return this.http.get<User>(`${this.usersUrl}/lookup`, { params: { email } });
  }

  getInvitations(userId: number): Observable<UserInvitationsResponse> {
    return this.http.get<UserInvitationsResponse>(`${this.usersUrl}/${userId}/invitations`);
  }

  getHistory(userId: number): Observable<UserInvitationsResponse> {
    return this.http.get<UserInvitationsResponse>(`${this.usersUrl}/${userId}/history`);
  }

  respondToInvitation(userId: number, eventId: number, action: 'accept' | 'decline'): Observable<void> {
    const endpoint = action === 'accept' ? 'accept' : 'decline';
    return this.http.post<void>(`${this.usersUrl}/${userId}/invitations/${eventId}/${endpoint}`, {});
  }

  login(payload: LoginPayload): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.usersUrl}/auth/login`, payload);
  }
}
