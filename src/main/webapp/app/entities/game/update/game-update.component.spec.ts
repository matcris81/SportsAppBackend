import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IPlayer } from 'app/entities/player/player.model';
import { PlayerService } from 'app/entities/player/service/player.service';
import { GameService } from '../service/game.service';
import { IGame } from '../game.model';
import { GameFormService } from './game-form.service';

import { GameUpdateComponent } from './game-update.component';

describe('Game Management Update Component', () => {
  let comp: GameUpdateComponent;
  let fixture: ComponentFixture<GameUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let gameFormService: GameFormService;
  let gameService: GameService;
  let playerService: PlayerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), GameUpdateComponent],
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
      .overrideTemplate(GameUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GameUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    gameFormService = TestBed.inject(GameFormService);
    gameService = TestBed.inject(GameService);
    playerService = TestBed.inject(PlayerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Player query and add missing value', () => {
      const game: IGame = { id: 456 };
      const organizer: IPlayer = { id: '935fecd0-8cc2-4bbb-8d5a-bd2d21cce86f' };
      game.organizer = organizer;

      const playerCollection: IPlayer[] = [{ id: '1e31d8db-ad31-4029-96ee-810240475e08' }];
      jest.spyOn(playerService, 'query').mockReturnValue(of(new HttpResponse({ body: playerCollection })));
      const additionalPlayers = [organizer];
      const expectedCollection: IPlayer[] = [...additionalPlayers, ...playerCollection];
      jest.spyOn(playerService, 'addPlayerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ game });
      comp.ngOnInit();

      expect(playerService.query).toHaveBeenCalled();
      expect(playerService.addPlayerToCollectionIfMissing).toHaveBeenCalledWith(
        playerCollection,
        ...additionalPlayers.map(expect.objectContaining),
      );
      expect(comp.playersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const game: IGame = { id: 456 };
      const organizer: IPlayer = { id: 'b8a04163-d439-40b8-815f-34fca3361c5b' };
      game.organizer = organizer;

      activatedRoute.data = of({ game });
      comp.ngOnInit();

      expect(comp.playersSharedCollection).toContain(organizer);
      expect(comp.game).toEqual(game);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGame>>();
      const game = { id: 123 };
      jest.spyOn(gameFormService, 'getGame').mockReturnValue(game);
      jest.spyOn(gameService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ game });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: game }));
      saveSubject.complete();

      // THEN
      expect(gameFormService.getGame).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(gameService.update).toHaveBeenCalledWith(expect.objectContaining(game));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGame>>();
      const game = { id: 123 };
      jest.spyOn(gameFormService, 'getGame').mockReturnValue({ id: null });
      jest.spyOn(gameService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ game: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: game }));
      saveSubject.complete();

      // THEN
      expect(gameFormService.getGame).toHaveBeenCalled();
      expect(gameService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGame>>();
      const game = { id: 123 };
      jest.spyOn(gameService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ game });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(gameService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePlayer', () => {
      it('Should forward to playerService', () => {
        const entity = { id: 'ABC' };
        const entity2 = { id: 'CBA' };
        jest.spyOn(playerService, 'comparePlayer');
        comp.comparePlayer(entity, entity2);
        expect(playerService.comparePlayer).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
