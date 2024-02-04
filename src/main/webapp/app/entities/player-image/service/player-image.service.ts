import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPlayerImage, NewPlayerImage } from '../player-image.model';

export type PartialUpdatePlayerImage = Partial<IPlayerImage> & Pick<IPlayerImage, 'id'>;

export type EntityResponseType = HttpResponse<IPlayerImage>;
export type EntityArrayResponseType = HttpResponse<IPlayerImage[]>;

@Injectable({ providedIn: 'root' })
export class PlayerImageService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/player-images');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(playerImage: NewPlayerImage): Observable<EntityResponseType> {
    return this.http.post<IPlayerImage>(this.resourceUrl, playerImage, { observe: 'response' });
  }

  update(playerImage: IPlayerImage): Observable<EntityResponseType> {
    return this.http.put<IPlayerImage>(`${this.resourceUrl}/${this.getPlayerImageIdentifier(playerImage)}`, playerImage, {
      observe: 'response',
    });
  }

  partialUpdate(playerImage: PartialUpdatePlayerImage): Observable<EntityResponseType> {
    return this.http.patch<IPlayerImage>(`${this.resourceUrl}/${this.getPlayerImageIdentifier(playerImage)}`, playerImage, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPlayerImage>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPlayerImage[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPlayerImageIdentifier(playerImage: Pick<IPlayerImage, 'id'>): number {
    return playerImage.id;
  }

  comparePlayerImage(o1: Pick<IPlayerImage, 'id'> | null, o2: Pick<IPlayerImage, 'id'> | null): boolean {
    return o1 && o2 ? this.getPlayerImageIdentifier(o1) === this.getPlayerImageIdentifier(o2) : o1 === o2;
  }

  addPlayerImageToCollectionIfMissing<Type extends Pick<IPlayerImage, 'id'>>(
    playerImageCollection: Type[],
    ...playerImagesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const playerImages: Type[] = playerImagesToCheck.filter(isPresent);
    if (playerImages.length > 0) {
      const playerImageCollectionIdentifiers = playerImageCollection.map(
        playerImageItem => this.getPlayerImageIdentifier(playerImageItem)!,
      );
      const playerImagesToAdd = playerImages.filter(playerImageItem => {
        const playerImageIdentifier = this.getPlayerImageIdentifier(playerImageItem);
        if (playerImageCollectionIdentifiers.includes(playerImageIdentifier)) {
          return false;
        }
        playerImageCollectionIdentifiers.push(playerImageIdentifier);
        return true;
      });
      return [...playerImagesToAdd, ...playerImageCollection];
    }
    return playerImageCollection;
  }
}
