import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  private loggedIn = new BehaviorSubject<boolean>(!!localStorage.getItem('currentUser'));
  public isLoggedIn$ = this.loggedIn.asObservable();

  constructor(private http: HttpClient) {}

  register(user: { username: string, email: string, password: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, user, { responseType: 'text' });
  }

  login(user: { email: string, password: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, user).pipe(
      tap((response: any) => {
        localStorage.setItem('currentUser', JSON.stringify(response));
        this.loggedIn.next(true);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('currentUser');
    this.loggedIn.next(false);
  }
}
