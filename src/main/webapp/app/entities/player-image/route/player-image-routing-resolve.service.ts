import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPlayerImage } from '../player-image.model';
import { PlayerImageService } from '../service/player-image.service';

export const playerImageResolve = (route: ActivatedRouteSnapshot): Observable<null | IPlayerImage> => {
  const id = route.params['id'];
  if (id) {
    return inject(PlayerImageService)
      .find(id)
      .pipe(
        mergeMap((playerImage: HttpResponse<IPlayerImage>) => {
          if (playerImage.body) {
            return of(playerImage.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default playerImageResolve;
