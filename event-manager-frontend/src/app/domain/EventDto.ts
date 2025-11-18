import { EventParticipant } from "./EventParticipant";

export interface EventDto {
  id: number;
  name: string;
  startDate: string;
  endDate: string;
  participants?: EventParticipant[];
}