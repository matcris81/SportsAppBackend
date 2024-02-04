import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SportService } from '../service/sport.service';
import { ISport } from '../sport.model';
import { SportFormService } from './sport-form.service';

import { SportUpdateComponent } from './sport-update.component';

describe('Sport Management Update Component', () => {
  let comp: SportUpdateComponent;
  let fixture: ComponentFixture<SportUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let sportFormService: SportFormService;
  let sportService: SportService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), SportUpdateComponent],
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
      .overrideTemplate(SportUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SportUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    sportFormService = TestBed.inject(SportFormService);
    sportService = TestBed.inject(SportService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const sport: ISport = { id: 456 };

      activatedRoute.data = of({ sport });
      comp.ngOnInit();

      expect(comp.sport).toEqual(sport);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISport>>();
      const sport = { id: 123 };
      jest.spyOn(sportFormService, 'getSport').mockReturnValue(sport);
      jest.spyOn(sportService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sport });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sport }));
      saveSubject.complete();

      // THEN
      expect(sportFormService.getSport).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(sportService.update).toHaveBeenCalledWith(expect.objectContaining(sport));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISport>>();
      const sport = { id: 123 };
      jest.spyOn(sportFormService, 'getSport').mockReturnValue({ id: null });
      jest.spyOn(sportService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sport: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sport }));
      saveSubject.complete();

      // THEN
      expect(sportFormService.getSport).toHaveBeenCalled();
      expect(sportService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISport>>();
      const sport = { id: 123 };
      jest.spyOn(sportService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sport });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(sportService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
