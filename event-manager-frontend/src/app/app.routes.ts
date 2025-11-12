import { Routes } from '@angular/router';

import { AdminHomeComponent } from './admin-home/admin-home.component';
import { SignupComponent } from './signup/signup.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'signup' },
  { path: 'signup', component: SignupComponent },
  { path: 'admin', component: AdminHomeComponent },
  { path: '**', redirectTo: 'signup' }
];
