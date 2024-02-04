import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { PlayerImageService } from '../service/player-image.service';

import { PlayerImageComponent } from './player-image.component';

describe('PlayerImage Management Component', () => {
  let comp: PlayerImageComponent;
  let fixture: ComponentFixture<PlayerImageComponent>;
  let service: PlayerImageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'player-image', component: PlayerImageComponent }]),
        HttpClientTestingModule,
        PlayerImageComponent,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              }),
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(PlayerImageComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PlayerImageComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(PlayerImageService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        }),
      ),
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.playerImages?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to playerImageService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getPlayerImageIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getPlayerImageIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
