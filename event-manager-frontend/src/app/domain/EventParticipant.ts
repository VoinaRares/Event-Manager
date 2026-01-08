export interface EventParticipant {
  eventId: number;
  userId: number;
  userEmail?: string | null;
  isComing?: boolean;
  responded?: boolean;
  invitationSentAt?: string | null;
  responseAt?: string | null;
  token?: string | null;
}