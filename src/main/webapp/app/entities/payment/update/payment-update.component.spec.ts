import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IPlayer } from 'app/entities/player/player.model';
import { PlayerService } from 'app/entities/player/service/player.service';
import { PaymentService } from '../service/payment.service';
import { IPayment } from '../payment.model';
import { PaymentFormService } from './payment-form.service';

import { PaymentUpdateComponent } from './payment-update.component';

describe('Payment Management Update Component', () => {
  let comp: PaymentUpdateComponent;
  let fixture: ComponentFixture<PaymentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let paymentFormService: PaymentFormService;
  let paymentService: PaymentService;
  let playerService: PlayerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), PaymentUpdateComponent],
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
      .overrideTemplate(PaymentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PaymentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    paymentFormService = TestBed.inject(PaymentFormService);
    paymentService = TestBed.inject(PaymentService);
    playerService = TestBed.inject(PlayerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Player query and add missing value', () => {
      const payment: IPayment = { id: 456 };
      const player: IPlayer = { id: '3daff1b1-e70c-4e3a-a177-02c91d7d0373' };
      payment.player = player;

      const playerCollection: IPlayer[] = [{ id: '7f9e353e-f670-4de5-9c1f-b921fb03cf68' }];
      jest.spyOn(playerService, 'query').mockReturnValue(of(new HttpResponse({ body: playerCollection })));
      const additionalPlayers = [player];
      const expectedCollection: IPlayer[] = [...additionalPlayers, ...playerCollection];
      jest.spyOn(playerService, 'addPlayerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ payment });
      comp.ngOnInit();

      expect(playerService.query).toHaveBeenCalled();
      expect(playerService.addPlayerToCollectionIfMissing).toHaveBeenCalledWith(
        playerCollection,
        ...additionalPlayers.map(expect.objectContaining),
      );
      expect(comp.playersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const payment: IPayment = { id: 456 };
      const player: IPlayer = { id: '32fc75ee-c170-4c3c-8ea4-4b69876dd0a9' };
      payment.player = player;

      activatedRoute.data = of({ payment });
      comp.ngOnInit();

      expect(comp.playersSharedCollection).toContain(player);
      expect(comp.payment).toEqual(payment);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPayment>>();
      const payment = { id: 123 };
      jest.spyOn(paymentFormService, 'getPayment').mockReturnValue(payment);
      jest.spyOn(paymentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ payment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: payment }));
      saveSubject.complete();

      // THEN
      expect(paymentFormService.getPayment).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(paymentService.update).toHaveBeenCalledWith(expect.objectContaining(payment));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPayment>>();
      const payment = { id: 123 };
      jest.spyOn(paymentFormService, 'getPayment').mockReturnValue({ id: null });
      jest.spyOn(paymentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ payment: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: payment }));
      saveSubject.complete();

      // THEN
      expect(paymentFormService.getPayment).toHaveBeenCalled();
      expect(paymentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPayment>>();
      const payment = { id: 123 };
      jest.spyOn(paymentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ payment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(paymentService.update).toHaveBeenCalled();
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
