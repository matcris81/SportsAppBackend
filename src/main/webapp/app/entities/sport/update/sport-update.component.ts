import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISport } from '../sport.model';
import { SportService } from '../service/sport.service';
import { SportFormService, SportFormGroup } from './sport-form.service';

@Component({
  standalone: true,
  selector: 'jhi-sport-update',
  templateUrl: './sport-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SportUpdateComponent implements OnInit {
  isSaving = false;
  sport: ISport | null = null;

  editForm: SportFormGroup = this.sportFormService.createSportFormGroup();

  constructor(
    protected sportService: SportService,
    protected sportFormService: SportFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sport }) => {
      this.sport = sport;
      if (sport) {
        this.updateForm(sport);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const sport = this.sportFormService.getSport(this.editForm);
    if (sport.id !== null) {
      this.subscribeToSaveResponse(this.sportService.update(sport));
    } else {
      this.subscribeToSaveResponse(this.sportService.create(sport));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISport>>): void {
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

  protected updateForm(sport: ISport): void {
    this.sport = sport;
    this.sportFormService.resetForm(this.editForm, sport);
  }
}
