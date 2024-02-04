import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISport } from '../sport.model';
import { SportService } from '../service/sport.service';

export const sportResolve = (route: ActivatedRouteSnapshot): Observable<null | ISport> => {
  const id = route.params['id'];
  if (id) {
    return inject(SportService)
      .find(id)
      .pipe(
        mergeMap((sport: HttpResponse<ISport>) => {
          if (sport.body) {
            return of(sport.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default sportResolve;
