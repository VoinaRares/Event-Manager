import { Injectable, computed, signal } from '@angular/core';

export type UserRole = 'organizer' | 'user' | null;

export interface SessionSnapshot {
  role: UserRole;
  userId?: number;
  email?: string;
}

@Injectable({ providedIn: 'root' })
export class SessionService {
  private readonly storageKey = 'event-manager-session';
  readonly session = signal<SessionSnapshot>(this.readSession());
  readonly role = computed(() => this.session().role);

  setSession(snapshot: SessionSnapshot): void {
    this.session.set(snapshot);
    if (snapshot.role) {
      localStorage.setItem(this.storageKey, JSON.stringify(snapshot));
    } else {
      localStorage.removeItem(this.storageKey);
    }
  }

  updateSession(partial: Partial<SessionSnapshot>): void {
    const next = { ...this.session(), ...partial };
    this.setSession(next);
  }

  setRole(role: UserRole): void {
    this.updateSession({ role });
  }

  clear(): void {
    this.setSession({ role: null });
  }

  userId(): number | undefined {
    return this.session().userId;
  }

  private readSession(): SessionSnapshot {
    const stored = localStorage.getItem(this.storageKey);
    if (stored) {
      try {
        const parsed = JSON.parse(stored);
        if (parsed && (parsed.role === 'organizer' || parsed.role === 'user')) {
          return parsed;
        }
      } catch {
        localStorage.removeItem(this.storageKey);
      }
    }
    return { role: null };
  }
}
