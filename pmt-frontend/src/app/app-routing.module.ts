import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { ProjectCreateComponent } from './projects/project-create/project-create.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ProjectInviteComponent } from './projects/project-invite/project-invite.component';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' }, 
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'projects/create', component: ProjectCreateComponent },
  { path: 'projects/:id/invite', component: ProjectInviteComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
