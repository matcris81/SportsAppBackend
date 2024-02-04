import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IPlayerImage } from 'app/entities/player-image/player-image.model';
import { PlayerImageService } from 'app/entities/player-image/service/player-image.service';
import { IGame } from 'app/entities/game/game.model';
import { GameService } from 'app/entities/game/service/game.service';
import { IVenue } from 'app/entities/venue/venue.model';
import { VenueService } from 'app/entities/venue/service/venue.service';
import { IPlayer } from '../player.model';
import { PlayerService } from '../service/player.service';
import { PlayerFormService } from './player-form.service';

import { PlayerUpdateComponent } from './player-update.component';

describe('Player Management Update Component', () => {
  let comp: PlayerUpdateComponent;
  let fixture: ComponentFixture<PlayerUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let playerFormService: PlayerFormService;
  let playerService: PlayerService;
  let playerImageService: PlayerImageService;
  let gameService: GameService;
  let venueService: VenueService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), PlayerUpdateComponent],
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
      .overrideTemplate(PlayerUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PlayerUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    playerFormService = TestBed.inject(PlayerFormService);
    playerService = TestBed.inject(PlayerService);
    playerImageService = TestBed.inject(PlayerImageService);
    gameService = TestBed.inject(GameService);
    venueService = TestBed.inject(VenueService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call playerImage query and add missing value', () => {
      const player: IPlayer = { id: 'CBA' };
      const playerImage: IPlayerImage = { id: 23506 };
      player.playerImage = playerImage;

      const playerImageCollection: IPlayerImage[] = [{ id: 23721 }];
      jest.spyOn(playerImageService, 'query').mockReturnValue(of(new HttpResponse({ body: playerImageCollection })));
      const expectedCollection: IPlayerImage[] = [playerImage, ...playerImageCollection];
      jest.spyOn(playerImageService, 'addPlayerImageToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ player });
      comp.ngOnInit();

      expect(playerImageService.query).toHaveBeenCalled();
      expect(playerImageService.addPlayerImageToCollectionIfMissing).toHaveBeenCalledWith(playerImageCollection, playerImage);
      expect(comp.playerImagesCollection).toEqual(expectedCollection);
    });

    it('Should call Game query and add missing value', () => {
      const player: IPlayer = { id: 'CBA' };
      const games: IGame[] = [{ id: 14746 }];
      player.games = games;

      const gameCollection: IGame[] = [{ id: 30817 }];
      jest.spyOn(gameService, 'query').mockReturnValue(of(new HttpResponse({ body: gameCollection })));
      const additionalGames = [...games];
      const expectedCollection: IGame[] = [...additionalGames, ...gameCollection];
      jest.spyOn(gameService, 'addGameToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ player });
      comp.ngOnInit();

      expect(gameService.query).toHaveBeenCalled();
      expect(gameService.addGameToCollectionIfMissing).toHaveBeenCalledWith(
        gameCollection,
        ...additionalGames.map(expect.objectContaining),
      );
      expect(comp.gamesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Venue query and add missing value', () => {
      const player: IPlayer = { id: 'CBA' };
      const venues: IVenue[] = [{ id: 30048 }];
      player.venues = venues;

      const venueCollection: IVenue[] = [{ id: 31624 }];
      jest.spyOn(venueService, 'query').mockReturnValue(of(new HttpResponse({ body: venueCollection })));
      const additionalVenues = [...venues];
      const expectedCollection: IVenue[] = [...additionalVenues, ...venueCollection];
      jest.spyOn(venueService, 'addVenueToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ player });
      comp.ngOnInit();

      expect(venueService.query).toHaveBeenCalled();
      expect(venueService.addVenueToCollectionIfMissing).toHaveBeenCalledWith(
        venueCollection,
        ...additionalVenues.map(expect.objectContaining),
      );
      expect(comp.venuesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const player: IPlayer = { id: 'CBA' };
      const playerImage: IPlayerImage = { id: 31738 };
      player.playerImage = playerImage;
      const game: IGame = { id: 13676 };
      player.games = [game];
      const venue: IVenue = { id: 7856 };
      player.venues = [venue];

      activatedRoute.data = of({ player });
      comp.ngOnInit();

      expect(comp.playerImagesCollection).toContain(playerImage);
      expect(comp.gamesSharedCollection).toContain(game);
      expect(comp.venuesSharedCollection).toContain(venue);
      expect(comp.player).toEqual(player);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlayer>>();
      const player = { id: 'ABC' };
      jest.spyOn(playerFormService, 'getPlayer').mockReturnValue(player);
      jest.spyOn(playerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ player });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: player }));
      saveSubject.complete();

      // THEN
      expect(playerFormService.getPlayer).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(playerService.update).toHaveBeenCalledWith(expect.objectContaining(player));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlayer>>();
      const player = { id: 'ABC' };
      jest.spyOn(playerFormService, 'getPlayer').mockReturnValue({ id: null });
      jest.spyOn(playerService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ player: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: player }));
      saveSubject.complete();

      // THEN
      expect(playerFormService.getPlayer).toHaveBeenCalled();
      expect(playerService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlayer>>();
      const player = { id: 'ABC' };
      jest.spyOn(playerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ player });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(playerService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePlayerImage', () => {
      it('Should forward to playerImageService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(playerImageService, 'comparePlayerImage');
        comp.comparePlayerImage(entity, entity2);
        expect(playerImageService.comparePlayerImage).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareGame', () => {
      it('Should forward to gameService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(gameService, 'compareGame');
        comp.compareGame(entity, entity2);
        expect(gameService.compareGame).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareVenue', () => {
      it('Should forward to venueService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(venueService, 'compareVenue');
        comp.compareVenue(entity, entity2);
        expect(venueService.compareVenue).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
