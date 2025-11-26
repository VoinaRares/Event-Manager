import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionService } from '../session/session.service';

export const organizerGuard: CanActivateFn = () => {
  const session = inject(SessionService);
  const router = inject(Router);
  if (session.role() === 'organizer') {
    return true;
  }
  router.navigate(['/signup/organizer']);
  return false;
};

