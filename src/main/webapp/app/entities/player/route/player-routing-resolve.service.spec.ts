import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IPlayer } from '../player.model';
import { PlayerService } from '../service/player.service';

import playerResolve from './player-routing-resolve.service';

describe('Player routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: PlayerService;
  let resultPlayer: IPlayer | null | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    service = TestBed.inject(PlayerService);
    resultPlayer = undefined;
  });

  describe('resolve', () => {
    it('should return IPlayer returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      TestBed.runInInjectionContext(() => {
        playerResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultPlayer = result;
          },
        });
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultPlayer).toEqual({ id: 'ABC' });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        playerResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultPlayer = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultPlayer).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IPlayer>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      TestBed.runInInjectionContext(() => {
        playerResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultPlayer = result;
          },
        });
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultPlayer).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
