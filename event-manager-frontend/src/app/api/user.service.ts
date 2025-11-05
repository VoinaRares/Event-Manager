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
  private readonly baseUrl = 'http://localhost:8080/api/users';

  register(payload: SignupPayload): Observable<User> {
    return this.http.post<User>(this.baseUrl, payload);
  }
}

