import { EventParticipant } from "./EventParticipant";
import { ImageDto } from "./ImageDto";

export interface EventDto {
  id: number;
  name: string;
  startDate: string;
  endDate: string;
  longitude?: number | null;
  latitude?: number | null;
  locationName?: string | null;
  participants?: EventParticipant[];
  images?: ImageDto[];
}