import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGame, NewGame } from '../game.model';

export type PartialUpdateGame = Partial<IGame> & Pick<IGame, 'id'>;

type RestOf<T extends IGame | NewGame> = Omit<T, 'gameDate'> & {
  gameDate?: string | null;
};

export type RestGame = RestOf<IGame>;

export type NewRestGame = RestOf<NewGame>;

export type PartialUpdateRestGame = RestOf<PartialUpdateGame>;

export type EntityResponseType = HttpResponse<IGame>;
export type EntityArrayResponseType = HttpResponse<IGame[]>;

@Injectable({ providedIn: 'root' })
export class GameService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/games');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(game: NewGame): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(game);
    return this.http.post<RestGame>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(game: IGame): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(game);
    return this.http
      .put<RestGame>(`${this.resourceUrl}/${this.getGameIdentifier(game)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(game: PartialUpdateGame): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(game);
    return this.http
      .patch<RestGame>(`${this.resourceUrl}/${this.getGameIdentifier(game)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestGame>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestGame[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getGameIdentifier(game: Pick<IGame, 'id'>): number {
    return game.id;
  }

  compareGame(o1: Pick<IGame, 'id'> | null, o2: Pick<IGame, 'id'> | null): boolean {
    return o1 && o2 ? this.getGameIdentifier(o1) === this.getGameIdentifier(o2) : o1 === o2;
  }

  addGameToCollectionIfMissing<Type extends Pick<IGame, 'id'>>(
    gameCollection: Type[],
    ...gamesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const games: Type[] = gamesToCheck.filter(isPresent);
    if (games.length > 0) {
      const gameCollectionIdentifiers = gameCollection.map(gameItem => this.getGameIdentifier(gameItem)!);
      const gamesToAdd = games.filter(gameItem => {
        const gameIdentifier = this.getGameIdentifier(gameItem);
        if (gameCollectionIdentifiers.includes(gameIdentifier)) {
          return false;
        }
        gameCollectionIdentifiers.push(gameIdentifier);
        return true;
      });
      return [...gamesToAdd, ...gameCollection];
    }
    return gameCollection;
  }

  protected convertDateFromClient<T extends IGame | NewGame | PartialUpdateGame>(game: T): RestOf<T> {
    return {
      ...game,
      gameDate: game.gameDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restGame: RestGame): IGame {
    return {
      ...restGame,
      gameDate: restGame.gameDate ? dayjs(restGame.gameDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestGame>): HttpResponse<IGame> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestGame[]>): HttpResponse<IGame[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
