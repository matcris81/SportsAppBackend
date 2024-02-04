import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPlayerImage } from '../player-image.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../player-image.test-samples';

import { PlayerImageService } from './player-image.service';

const requireRestSample: IPlayerImage = {
  ...sampleWithRequiredData,
};

describe('PlayerImage Service', () => {
  let service: PlayerImageService;
  let httpMock: HttpTestingController;
  let expectedResult: IPlayerImage | IPlayerImage[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PlayerImageService);
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

    it('should create a PlayerImage', () => {
      const playerImage = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(playerImage).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PlayerImage', () => {
      const playerImage = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(playerImage).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PlayerImage', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PlayerImage', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PlayerImage', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPlayerImageToCollectionIfMissing', () => {
      it('should add a PlayerImage to an empty array', () => {
        const playerImage: IPlayerImage = sampleWithRequiredData;
        expectedResult = service.addPlayerImageToCollectionIfMissing([], playerImage);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(playerImage);
      });

      it('should not add a PlayerImage to an array that contains it', () => {
        const playerImage: IPlayerImage = sampleWithRequiredData;
        const playerImageCollection: IPlayerImage[] = [
          {
            ...playerImage,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPlayerImageToCollectionIfMissing(playerImageCollection, playerImage);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PlayerImage to an array that doesn't contain it", () => {
        const playerImage: IPlayerImage = sampleWithRequiredData;
        const playerImageCollection: IPlayerImage[] = [sampleWithPartialData];
        expectedResult = service.addPlayerImageToCollectionIfMissing(playerImageCollection, playerImage);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(playerImage);
      });

      it('should add only unique PlayerImage to an array', () => {
        const playerImageArray: IPlayerImage[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const playerImageCollection: IPlayerImage[] = [sampleWithRequiredData];
        expectedResult = service.addPlayerImageToCollectionIfMissing(playerImageCollection, ...playerImageArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const playerImage: IPlayerImage = sampleWithRequiredData;
        const playerImage2: IPlayerImage = sampleWithPartialData;
        expectedResult = service.addPlayerImageToCollectionIfMissing([], playerImage, playerImage2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(playerImage);
        expect(expectedResult).toContain(playerImage2);
      });

      it('should accept null and undefined values', () => {
        const playerImage: IPlayerImage = sampleWithRequiredData;
        expectedResult = service.addPlayerImageToCollectionIfMissing([], null, playerImage, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(playerImage);
      });

      it('should return initial array if no PlayerImage is added', () => {
        const playerImageCollection: IPlayerImage[] = [sampleWithRequiredData];
        expectedResult = service.addPlayerImageToCollectionIfMissing(playerImageCollection, undefined, null);
        expect(expectedResult).toEqual(playerImageCollection);
      });
    });

    describe('comparePlayerImage', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePlayerImage(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePlayerImage(entity1, entity2);
        const compareResult2 = service.comparePlayerImage(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePlayerImage(entity1, entity2);
        const compareResult2 = service.comparePlayerImage(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePlayerImage(entity1, entity2);
        const compareResult2 = service.comparePlayerImage(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
