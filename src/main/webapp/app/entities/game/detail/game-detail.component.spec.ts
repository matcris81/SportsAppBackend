import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { GameDetailComponent } from './game-detail.component';

describe('Game Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GameDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: GameDetailComponent,
              resolve: { game: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(GameDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load game on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', GameDetailComponent);

      // THEN
      expect(instance.game).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
