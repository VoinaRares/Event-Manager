import { Routes } from '@angular/router';

import { AdminHomeComponent } from './admin-home/admin-home.component';
import { SignupComponent } from './signup/signup.component';
import { UserHomeComponent } from './user-home/user-home.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'signup' },
  { path: 'signup', component: SignupComponent },
  { path: 'home', component: UserHomeComponent },
  { path: 'admin', component: AdminHomeComponent },
  { path: '**', redirectTo: 'signup' }
];
