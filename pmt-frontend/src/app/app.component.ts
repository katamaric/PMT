import { Component } from '@angular/core';
import { AuthService } from './auth/auth.service';
import { firstValueFrom, Observable } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'pmt-frontend';
  public isLoggedIn$!: Observable<boolean>;
  authChecked = false;

  constructor(public authService: AuthService, public router: Router) {
    this.isLoggedIn$ = this.authService.isLoggedIn$;
  }

  async ngOnInit() {
    const isLoggedIn = await firstValueFrom(this.authService.isLoggedIn$);
    this.authChecked = true;

    if (window.location.pathname === '/') {
      this.router.navigate([isLoggedIn ? '/dashboard' : '/login']);
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
