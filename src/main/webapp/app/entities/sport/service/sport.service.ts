import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISport, NewSport } from '../sport.model';

export type PartialUpdateSport = Partial<ISport> & Pick<ISport, 'id'>;

export type EntityResponseType = HttpResponse<ISport>;
export type EntityArrayResponseType = HttpResponse<ISport[]>;

@Injectable({ providedIn: 'root' })
export class SportService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sports');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(sport: NewSport): Observable<EntityResponseType> {
    return this.http.post<ISport>(this.resourceUrl, sport, { observe: 'response' });
  }

  update(sport: ISport): Observable<EntityResponseType> {
    return this.http.put<ISport>(`${this.resourceUrl}/${this.getSportIdentifier(sport)}`, sport, { observe: 'response' });
  }

  partialUpdate(sport: PartialUpdateSport): Observable<EntityResponseType> {
    return this.http.patch<ISport>(`${this.resourceUrl}/${this.getSportIdentifier(sport)}`, sport, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISport>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISport[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSportIdentifier(sport: Pick<ISport, 'id'>): number {
    return sport.id;
  }

  compareSport(o1: Pick<ISport, 'id'> | null, o2: Pick<ISport, 'id'> | null): boolean {
    return o1 && o2 ? this.getSportIdentifier(o1) === this.getSportIdentifier(o2) : o1 === o2;
  }

  addSportToCollectionIfMissing<Type extends Pick<ISport, 'id'>>(
    sportCollection: Type[],
    ...sportsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const sports: Type[] = sportsToCheck.filter(isPresent);
    if (sports.length > 0) {
      const sportCollectionIdentifiers = sportCollection.map(sportItem => this.getSportIdentifier(sportItem)!);
      const sportsToAdd = sports.filter(sportItem => {
        const sportIdentifier = this.getSportIdentifier(sportItem);
        if (sportCollectionIdentifiers.includes(sportIdentifier)) {
          return false;
        }
        sportCollectionIdentifiers.push(sportIdentifier);
        return true;
      });
      return [...sportsToAdd, ...sportCollection];
    }
    return sportCollection;
  }
}
