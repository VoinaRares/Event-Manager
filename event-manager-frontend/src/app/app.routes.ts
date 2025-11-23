import { Routes } from '@angular/router';

import { AdminHomeComponent } from './admin-home/admin-home.component';
import { SignupComponent } from './signup/signup.component';
import { InviteResponseComponent } from './invite-response/invite-response.component';
import { UserHomeComponent } from './user-home/user-home.component';
import { LoginComponent } from './login/login-user.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'signup' },
  { path: 'signup', component: SignupComponent },
  { path: 'invite/:eventId/:token', component: InviteResponseComponent },
  { path: 'home', component: UserHomeComponent },
  { path: 'admin', component: AdminHomeComponent },
  { path: 'login', component: LoginComponent},
  { path: '**', redirectTo: 'signup' }
];
