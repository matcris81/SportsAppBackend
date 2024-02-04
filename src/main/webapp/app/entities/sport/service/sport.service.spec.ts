import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISport } from '../sport.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../sport.test-samples';

import { SportService } from './sport.service';

const requireRestSample: ISport = {
  ...sampleWithRequiredData,
};

describe('Sport Service', () => {
  let service: SportService;
  let httpMock: HttpTestingController;
  let expectedResult: ISport | ISport[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SportService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Sport', () => {
      const sport = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(sport).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Sport', () => {
      const sport = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(sport).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Sport', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Sport', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Sport', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSportToCollectionIfMissing', () => {
      it('should add a Sport to an empty array', () => {
        const sport: ISport = sampleWithRequiredData;
        expectedResult = service.addSportToCollectionIfMissing([], sport);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sport);
      });

      it('should not add a Sport to an array that contains it', () => {
        const sport: ISport = sampleWithRequiredData;
        const sportCollection: ISport[] = [
          {
            ...sport,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSportToCollectionIfMissing(sportCollection, sport);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Sport to an array that doesn't contain it", () => {
        const sport: ISport = sampleWithRequiredData;
        const sportCollection: ISport[] = [sampleWithPartialData];
        expectedResult = service.addSportToCollectionIfMissing(sportCollection, sport);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sport);
      });

      it('should add only unique Sport to an array', () => {
        const sportArray: ISport[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const sportCollection: ISport[] = [sampleWithRequiredData];
        expectedResult = service.addSportToCollectionIfMissing(sportCollection, ...sportArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const sport: ISport = sampleWithRequiredData;
        const sport2: ISport = sampleWithPartialData;
        expectedResult = service.addSportToCollectionIfMissing([], sport, sport2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sport);
        expect(expectedResult).toContain(sport2);
      });

      it('should accept null and undefined values', () => {
        const sport: ISport = sampleWithRequiredData;
        expectedResult = service.addSportToCollectionIfMissing([], null, sport, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sport);
      });

      it('should return initial array if no Sport is added', () => {
        const sportCollection: ISport[] = [sampleWithRequiredData];
        expectedResult = service.addSportToCollectionIfMissing(sportCollection, undefined, null);
        expect(expectedResult).toEqual(sportCollection);
      });
    });

    describe('compareSport', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSport(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSport(entity1, entity2);
        const compareResult2 = service.compareSport(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSport(entity1, entity2);
        const compareResult2 = service.compareSport(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSport(entity1, entity2);
        const compareResult2 = service.compareSport(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
