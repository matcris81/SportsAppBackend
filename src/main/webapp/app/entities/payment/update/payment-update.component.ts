import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPlayer } from 'app/entities/player/player.model';
import { PlayerService } from 'app/entities/player/service/player.service';
import { PaymentStatus } from 'app/entities/enumerations/payment-status.model';
import { PaymentService } from '../service/payment.service';
import { IPayment } from '../payment.model';
import { PaymentFormService, PaymentFormGroup } from './payment-form.service';

@Component({
  standalone: true,
  selector: 'jhi-payment-update',
  templateUrl: './payment-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PaymentUpdateComponent implements OnInit {
  isSaving = false;
  payment: IPayment | null = null;
  paymentStatusValues = Object.keys(PaymentStatus);

  playersSharedCollection: IPlayer[] = [];

  editForm: PaymentFormGroup = this.paymentFormService.createPaymentFormGroup();

  constructor(
    protected paymentService: PaymentService,
    protected paymentFormService: PaymentFormService,
    protected playerService: PlayerService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  comparePlayer = (o1: IPlayer | null, o2: IPlayer | null): boolean => this.playerService.comparePlayer(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ payment }) => {
      this.payment = payment;
      if (payment) {
        this.updateForm(payment);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const payment = this.paymentFormService.getPayment(this.editForm);
    if (payment.id !== null) {
      this.subscribeToSaveResponse(this.paymentService.update(payment));
    } else {
      this.subscribeToSaveResponse(this.paymentService.create(payment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPayment>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(payment: IPayment): void {
    this.payment = payment;
    this.paymentFormService.resetForm(this.editForm, payment);

    this.playersSharedCollection = this.playerService.addPlayerToCollectionIfMissing<IPlayer>(this.playersSharedCollection, payment.player);
  }

  protected loadRelationshipsOptions(): void {
    this.playerService
      .query()
      .pipe(map((res: HttpResponse<IPlayer[]>) => res.body ?? []))
      .pipe(map((players: IPlayer[]) => this.playerService.addPlayerToCollectionIfMissing<IPlayer>(players, this.payment?.player)))
      .subscribe((players: IPlayer[]) => (this.playersSharedCollection = players));
  }
}
