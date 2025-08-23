import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { ProjectCreateComponent } from './projects/project-create/project-create.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ProjectInviteComponent } from './projects/project-invite/project-invite.component';
import { TaskCreateComponent } from './tasks/task-create/task-create.component';
import { TaskDetailComponent } from './tasks/task-detail/task-detail.component';
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  { 
    path: '', 
    pathMatch: 'full', 
    redirectTo: localStorage.getItem('currentUser') ? '/dashboard' : '/login' 
  },

  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'projects/create', component: ProjectCreateComponent, canActivate: [AuthGuard] },
  { path: 'projects/:id/invite', component: ProjectInviteComponent, canActivate: [AuthGuard] },
  { path: 'project/:id/tasks/create', component: TaskCreateComponent, canActivate: [AuthGuard] },
  { path: 'tasks/:taskId', component: TaskDetailComponent, canActivate: [AuthGuard] },

  { path: '**', redirectTo: '/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
