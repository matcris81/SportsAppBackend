import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { SportComponent } from './list/sport.component';
import { SportDetailComponent } from './detail/sport-detail.component';
import { SportUpdateComponent } from './update/sport-update.component';
import SportResolve from './route/sport-routing-resolve.service';

const sportRoute: Routes = [
  {
    path: '',
    component: SportComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SportDetailComponent,
    resolve: {
      sport: SportResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SportUpdateComponent,
    resolve: {
      sport: SportResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SportUpdateComponent,
    resolve: {
      sport: SportResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default sportRoute;
