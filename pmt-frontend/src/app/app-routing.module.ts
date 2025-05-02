import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { ProjectCreateComponent } from './projects/project-create/project-create.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ProjectInviteComponent } from './projects/project-invite/project-invite.component';
import { TaskCreateComponent } from './tasks/task-create/task-create.component';
import { TaskDetailComponent } from './tasks/task-detail/task-detail.component';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' }, 
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'projects/create', component: ProjectCreateComponent },
  { path: 'projects/:id/invite', component: ProjectInviteComponent },
  { path: 'project/:id/tasks/create', component: TaskCreateComponent },
  { path: 'tasks/:taskId', component: TaskDetailComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
