import { LEAGUES } from './data/leagues';
import { League } from './../leagues/shared/league.model';
import { Observable } from 'rxjs/Observable';
import { Injectable } from '@angular/core';
import { Response } from '@angular/http'
import 'rxjs/add/observable/of';

@Injectable()
export class LeagueServiceStub {

  getLeague(id: string): Promise<League> {
    return Promise.resolve(LEAGUES.find(league => league.id === id));
  }

  getAllLeagues(): Promise<League[]> {
    return Promise.resolve(LEAGUES);
  }

  findByName(name: string): Observable<League[]> {
    return Observable.of(LEAGUES.filter(league => league.name.includes(name)));
  }

  create(league: League) {
    let lastId = LEAGUES[LEAGUES.length - 1].id;
    league.id = String(+lastId + 1);
    LEAGUES.push(league);
    return Promise.resolve(league);
  }
}