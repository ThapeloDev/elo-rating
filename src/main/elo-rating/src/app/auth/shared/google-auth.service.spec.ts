import { League } from 'app/leagues/shared/league.model';
import { TestBed, inject } from '@angular/core/testing';

import { GoogleAuthService } from './google-auth.service';
import { User } from "../../users/shared/user.model";

describe('GoogleAuthService', () => {
  let sessionStorageMock: Map<string, any> = new Map<string, any>();
  beforeEach(() => {
    sessionStorageMock.clear();
    TestBed.configureTestingModule({
      providers: [GoogleAuthService]
    });

    spyOn(sessionStorage, 'getItem').and.callFake((key) => {
      return sessionStorageMock.get(key);
    });

    spyOn(sessionStorage, 'setItem').and.callFake((key, value) => {
      sessionStorageMock.set(key, value);
    });

    signOut();
    setLeague(null);
  });

  function signIn() {
    let user = new User();
    user.id = '1234';
    user.name = 'Name LastName';
    user.givenName = 'Name';
    user.familyName = 'LastName';
    user.email = 'lastname@gmail.com';
    user.pictureUrl = '';
    let league = new League('123', 'Test league');
    user.leagues = [];
    user.leagues.push(league);
    sessionStorageMock.set('user', JSON.stringify(user));
    sessionStorageMock.set('token', '1q2w3e4r5t6y7u8i9o');
  }

  function signOut() {
    sessionStorageMock.set('user', null);
    sessionStorageMock.set('token', null);
  }

  function setLeague(league: League | null) {
    sessionStorageMock.set('league', JSON.stringify(league));
  }

  it('should be created', inject([GoogleAuthService], (service: GoogleAuthService) => {
    expect(service).toBeTruthy();
  }));

  it('isAuthenticated() should return true for signed in user', inject([GoogleAuthService], (service: GoogleAuthService) => {
    signIn();
    expect(service.isAuthenticated()).toBeTruthy();
  }));

  it('isAuthenticated() should return false for signed out user', inject([GoogleAuthService], (service: GoogleAuthService) => {
    expect(service.isAuthenticated()).toBeFalsy();
  }));

  it('getProfile() should return profile for signed in user', inject([GoogleAuthService], (service: GoogleAuthService) => {
    signIn();
    expect(service.getUser()).toBeTruthy();
  }));

  it('getProfile() should return null for signed out user', inject([GoogleAuthService], (service: GoogleAuthService) => {
    expect(service.getUser()).toBeNull();
  }));

  it('getSessionToken() should return token for signed in user', inject([GoogleAuthService], (service: GoogleAuthService) => {
    signIn();
    expect(service.getIdToken()).toBeTruthy();
  }));

  it('getSessionToken() should return null for signed out user', inject([GoogleAuthService], (service: GoogleAuthService) => {
    expect(service.getIdToken()).toBeNull();
  }));

  it('getCurrentLeague() should return league when league is selected', inject([GoogleAuthService], (service: GoogleAuthService) => {
    let league = new League('123', 'Test league');
    setLeague(league);
    expect(service.getCurrentLeague().id).toEqual(league.id);
    expect(service.getCurrentLeague().name).toEqual(league.name);
  }));

  it('getCurrentLeague() should return null when league is not selected', inject([GoogleAuthService], (service: GoogleAuthService) => {
    expect(service.getCurrentLeague()).toBeNull();
  }));

  it('getCurrentLeagueId() should return league id when league is selected', inject([GoogleAuthService], (service: GoogleAuthService) => {
    let league = new League('123', 'Test league');
    setLeague(league);
    expect(service.getCurrentLeagueId()).toEqual('123');
  }));

  it('getCurrentLeagueId() should return null when league is not selected', inject([GoogleAuthService], (service: GoogleAuthService) => {
    expect(service.getCurrentLeagueId()).toBeNull();
  }));

  it('setCurrentLeague() should set current league id and store it in sessionStorage', inject([GoogleAuthService], (service: GoogleAuthService) => {
    let league = new League('987', 'Test league');
    service.setCurrentLeague(league);
    expect(service.getCurrentLeagueId()).toEqual(league.id);
    expect(service.getCurrentLeague().name).toEqual(league.name);
  }));

  it('isAuthorized() should return true for authorized user', inject([GoogleAuthService], (service: GoogleAuthService) => {
    signIn();
    let league = new League('123', 'Test league');    
    setLeague(league);
    expect(service.isAuthorized()).toBeTruthy();
  }));

  it('isAuthorized() should return false for not authorized user', inject([GoogleAuthService], (service: GoogleAuthService) => {
    signIn();
    let league = new League('987', 'Test league');    
    setLeague(league);
    expect(service.isAuthorized()).toBeFalsy();    
  }));

  it('isAuthorized() should return false for not authenticated user', inject([GoogleAuthService], (service: GoogleAuthService) => {
    let league = new League('123', 'Test league');    
    setLeague(league);
    expect(service.isAuthorized()).toBeFalsy();    
  }));

  it('isAuthorized() should return false for not selected league', inject([GoogleAuthService], (service: GoogleAuthService) => {
    signIn();
    setLeague(null);
    expect(service.isAuthorized()).toBeFalsy();    
  }));
});
