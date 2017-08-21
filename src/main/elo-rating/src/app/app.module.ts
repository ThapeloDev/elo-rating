import { UserService } from './users/shared/user.service';
import { GoogleAuthService } from './auth/shared/google-auth.service';
import { MatchService } from './matches/shared/match.service';
import { PlayerService } from './players/shared/player.service';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ChartModule } from 'angular2-highcharts';
import { CookieService } from 'ng2-cookies';
import { OrderModule } from 'ngx-order-pipe';

import { RoutingModule } from './core/routing/routing.module';
import { AppComponent } from './app.component';
import { NavComponent } from './core/directives/nav/nav.component';
import { LeagueAddComponent } from './leagues/league-add/league-add.component';
import { LeagueDetailComponent } from './leagues/league-detail/league-detail.component';
import { LeagueService } from './leagues/shared/league.service';
import { PlayerListComponent } from './players/player-list/player-list.component';
import { PlayerAddComponent } from './players/player-add/player-add.component';
import { ConfirmModalComponent } from './core/directives/confirm-modal/confirm-modal.component';
import { PlayerDetailComponent } from './players/player-detail/player-detail.component';
import { PlayerEditComponent } from './players/player-edit/player-edit.component';
import { MatchSaveComponent } from './matches/match-save/match-save.component';
import { QueueListComponent } from './queue/queue-list/queue-list.component';
import { LeagueSearchComponent } from './leagues/league-search/league-search.component';
import { MatchListComponent } from './matches/match-list/match-list.component';
import { PlayerRankingComponent } from './players/player-ranking/player-ranking.component';
import { PlayerMatchesComponent } from './players/player-matches/player-matches.component';
import { PlayerStatisticsComponent } from './players/player-statistics/player-statistics.component';
import { HighchartsStatic } from "angular2-highcharts/dist/HighchartsService";
import { SmallSpinnerComponent } from './core/directives/small-spinner/small-spinner.component';
import { PageSizeComponent } from './core/directives/page-size/page-size.component';
import { PlayerForecastComponent } from './players/player-forecast/player-forecast.component';
import { PlayerCellComponent } from './players/player-cell/player-cell.component';
import { SpinnerComponent } from './core/directives/spinner/spinner.component';
import { GoogleAuthComponent } from './auth/google-auth/google-auth.component';
import { PlayerOpponentsComponent } from './players/player-opponents/player-opponents.component';
import { LeagueAssignComponent } from './leagues/league-assign/league-assign.component';
import { UserInviteComponent } from './users/user-invite/user-invite.component';
import { GoogleButtonComponent } from './auth/google-button/google-button.component';
import { UserConfirmInvitationComponent } from './users/user-confirm-invitation/user-confirm-invitation.component';
import { UserCreatePlayerComponent } from './users/user-create-player/user-create-player.component';

@NgModule({
  declarations: [
    AppComponent,
    NavComponent,
    LeagueAddComponent,
    LeagueDetailComponent,
    PlayerListComponent,
    PlayerAddComponent,
    ConfirmModalComponent,
    PlayerDetailComponent,
    PlayerEditComponent,
    MatchSaveComponent,
    QueueListComponent,
    LeagueSearchComponent,
    MatchListComponent,
    PlayerRankingComponent,
    PlayerMatchesComponent,
    PlayerStatisticsComponent,
    SmallSpinnerComponent,
    PageSizeComponent,
    PlayerForecastComponent,
    PlayerCellComponent,
    SpinnerComponent,
    GoogleAuthComponent,
    PlayerOpponentsComponent,
    LeagueAssignComponent,
    UserInviteComponent,
    GoogleButtonComponent,
    UserConfirmInvitationComponent,
    UserCreatePlayerComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    RoutingModule,
    NgbModule.forRoot(),
    ChartModule,
    OrderModule
  ],
  providers: [
    LeagueService,
    PlayerService,
    MatchService,
    CookieService,
    GoogleAuthService,
    UserService,
    {
      provide: HighchartsStatic,
      useFactory: highchartsFactory
    }
  ],
  bootstrap: [AppComponent],
  entryComponents: [ConfirmModalComponent],

})
export class AppModule { }

export function highchartsFactory() {
  const highcharts = require('highcharts');
  return highcharts;
}
