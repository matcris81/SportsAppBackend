import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { PlayerImageComponent } from './list/player-image.component';
import { PlayerImageDetailComponent } from './detail/player-image-detail.component';
import { PlayerImageUpdateComponent } from './update/player-image-update.component';
import PlayerImageResolve from './route/player-image-routing-resolve.service';

const playerImageRoute: Routes = [
  {
    path: '',
    component: PlayerImageComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PlayerImageDetailComponent,
    resolve: {
      playerImage: PlayerImageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PlayerImageUpdateComponent,
    resolve: {
      playerImage: PlayerImageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PlayerImageUpdateComponent,
    resolve: {
      playerImage: PlayerImageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default playerImageRoute;
