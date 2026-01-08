import { EventParticipant } from "./EventParticipant";

export interface EventDto {
  id: number;
  name: string;
  startDate: string;
  endDate: string;
  longitude?: number | null;
  latitude?: number | null;
  locationName?: string | null;
  participants?: EventParticipant[];
}