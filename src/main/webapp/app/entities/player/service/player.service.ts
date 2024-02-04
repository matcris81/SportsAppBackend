import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPlayer, NewPlayer } from '../player.model';

export type PartialUpdatePlayer = Partial<IPlayer> & Pick<IPlayer, 'id'>;

type RestOf<T extends IPlayer | NewPlayer> = Omit<T, 'dob'> & {
  dob?: string | null;
};

export type RestPlayer = RestOf<IPlayer>;

export type NewRestPlayer = RestOf<NewPlayer>;

export type PartialUpdateRestPlayer = RestOf<PartialUpdatePlayer>;

export type EntityResponseType = HttpResponse<IPlayer>;
export type EntityArrayResponseType = HttpResponse<IPlayer[]>;

@Injectable({ providedIn: 'root' })
export class PlayerService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/players');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(player: NewPlayer): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(player);
    return this.http
      .post<RestPlayer>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(player: IPlayer): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(player);
    return this.http
      .put<RestPlayer>(`${this.resourceUrl}/${this.getPlayerIdentifier(player)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(player: PartialUpdatePlayer): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(player);
    return this.http
      .patch<RestPlayer>(`${this.resourceUrl}/${this.getPlayerIdentifier(player)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<RestPlayer>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPlayer[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPlayerIdentifier(player: Pick<IPlayer, 'id'>): string {
    return player.id;
  }

  comparePlayer(o1: Pick<IPlayer, 'id'> | null, o2: Pick<IPlayer, 'id'> | null): boolean {
    return o1 && o2 ? this.getPlayerIdentifier(o1) === this.getPlayerIdentifier(o2) : o1 === o2;
  }

  addPlayerToCollectionIfMissing<Type extends Pick<IPlayer, 'id'>>(
    playerCollection: Type[],
    ...playersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const players: Type[] = playersToCheck.filter(isPresent);
    if (players.length > 0) {
      const playerCollectionIdentifiers = playerCollection.map(playerItem => this.getPlayerIdentifier(playerItem)!);
      const playersToAdd = players.filter(playerItem => {
        const playerIdentifier = this.getPlayerIdentifier(playerItem);
        if (playerCollectionIdentifiers.includes(playerIdentifier)) {
          return false;
        }
        playerCollectionIdentifiers.push(playerIdentifier);
        return true;
      });
      return [...playersToAdd, ...playerCollection];
    }
    return playerCollection;
  }

  protected convertDateFromClient<T extends IPlayer | NewPlayer | PartialUpdatePlayer>(player: T): RestOf<T> {
    return {
      ...player,
      dob: player.dob?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restPlayer: RestPlayer): IPlayer {
    return {
      ...restPlayer,
      dob: restPlayer.dob ? dayjs(restPlayer.dob) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPlayer>): HttpResponse<IPlayer> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPlayer[]>): HttpResponse<IPlayer[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
