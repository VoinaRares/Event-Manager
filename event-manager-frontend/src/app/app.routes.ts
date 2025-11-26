import { Routes } from '@angular/router';

import { AdminHomeComponent } from './admin-home/admin-home.component';
import { SignupComponent } from './signup/signup.component';
import { InviteResponseComponent } from './invite-response/invite-response.component';
import { UserHomeComponent } from './user-home/user-home.component';
import { LoginComponent } from './login/login-user.component';
import { organizerGuard } from './guards/organizer.guard';
import { UserSignupComponent } from './user-signup/user-signup.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'signup/organizer' },
  { path: 'signup', pathMatch: 'full', redirectTo: 'signup/organizer' },
  { path: 'signup/organizer', component: SignupComponent },
  { path: 'signup/user', component: UserSignupComponent },
  { path: 'invite/:eventId/:token', component: InviteResponseComponent },
  { path: 'home', component: UserHomeComponent },
  { path: 'admin', component: AdminHomeComponent, canActivate: [organizerGuard] },
  { path: 'login', component: LoginComponent},
  { path: '**', redirectTo: 'signup/organizer' }
];
