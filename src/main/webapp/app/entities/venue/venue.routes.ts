import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { VenueComponent } from './list/venue.component';
import { VenueDetailComponent } from './detail/venue-detail.component';
import { VenueUpdateComponent } from './update/venue-update.component';
import VenueResolve from './route/venue-routing-resolve.service';

const venueRoute: Routes = [
  {
    path: '',
    component: VenueComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: VenueDetailComponent,
    resolve: {
      venue: VenueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: VenueUpdateComponent,
    resolve: {
      venue: VenueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: VenueUpdateComponent,
    resolve: {
      venue: VenueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default venueRoute;
