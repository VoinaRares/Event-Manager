import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

export interface SignupPayload {
  email: string;
  password: string;
}

export interface User {
  id: number;
  email: string;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);

  private readonly usersUrl = 'http://localhost:8080/api/users';
  private readonly eventsUrl = 'http://localhost:8080/api/events';

  register(payload: SignupPayload): Observable<User> {
    return this.http.post<User>(this.usersUrl, payload);
  }

  getEvents(): Observable<any[]> {
    return this.http.get<any[]>(this.eventsUrl);
  }
}
