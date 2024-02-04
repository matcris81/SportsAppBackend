import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PlayerImageService } from '../service/player-image.service';
import { IPlayerImage } from '../player-image.model';
import { PlayerImageFormService } from './player-image-form.service';

import { PlayerImageUpdateComponent } from './player-image-update.component';

describe('PlayerImage Management Update Component', () => {
  let comp: PlayerImageUpdateComponent;
  let fixture: ComponentFixture<PlayerImageUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let playerImageFormService: PlayerImageFormService;
  let playerImageService: PlayerImageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), PlayerImageUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(PlayerImageUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PlayerImageUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    playerImageFormService = TestBed.inject(PlayerImageFormService);
    playerImageService = TestBed.inject(PlayerImageService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const playerImage: IPlayerImage = { id: 456 };

      activatedRoute.data = of({ playerImage });
      comp.ngOnInit();

      expect(comp.playerImage).toEqual(playerImage);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlayerImage>>();
      const playerImage = { id: 123 };
      jest.spyOn(playerImageFormService, 'getPlayerImage').mockReturnValue(playerImage);
      jest.spyOn(playerImageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ playerImage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: playerImage }));
      saveSubject.complete();

      // THEN
      expect(playerImageFormService.getPlayerImage).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(playerImageService.update).toHaveBeenCalledWith(expect.objectContaining(playerImage));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlayerImage>>();
      const playerImage = { id: 123 };
      jest.spyOn(playerImageFormService, 'getPlayerImage').mockReturnValue({ id: null });
      jest.spyOn(playerImageService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ playerImage: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: playerImage }));
      saveSubject.complete();

      // THEN
      expect(playerImageFormService.getPlayerImage).toHaveBeenCalled();
      expect(playerImageService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlayerImage>>();
      const playerImage = { id: 123 };
      jest.spyOn(playerImageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ playerImage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(playerImageService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
