export interface ISport {
  id: number;
  sportName?: string | null;
}

export type NewSport = Omit<ISport, 'id'> & { id: null };
