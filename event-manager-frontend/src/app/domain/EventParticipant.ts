export interface EventParticipant {
  eventId: number;
  userId: number;
  isComing?: boolean;
  responded?: boolean;
  invitationSentAt?: string | null;
  responseAt?: string | null;
  token?: string | null;
}