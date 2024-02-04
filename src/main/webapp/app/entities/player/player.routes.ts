import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { PlayerComponent } from './list/player.component';
import { PlayerDetailComponent } from './detail/player-detail.component';
import { PlayerUpdateComponent } from './update/player-update.component';
import PlayerResolve from './route/player-routing-resolve.service';

const playerRoute: Routes = [
  {
    path: '',
    component: PlayerComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PlayerDetailComponent,
    resolve: {
      player: PlayerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PlayerUpdateComponent,
    resolve: {
      player: PlayerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PlayerUpdateComponent,
    resolve: {
      player: PlayerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default playerRoute;
