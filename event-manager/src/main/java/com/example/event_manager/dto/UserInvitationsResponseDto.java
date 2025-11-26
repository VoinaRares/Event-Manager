package com.example.event_manager.dto;

import java.util.List;

public class UserInvitationsResponseDto {
    private List<UserInvitationDto> pending;
    private List<UserInvitationDto> confirmed;

    public List<UserInvitationDto> getPending() {
        return pending;
    }

    public void setPending(List<UserInvitationDto> pending) {
        this.pending = pending;
    }

    public List<UserInvitationDto> getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(List<UserInvitationDto> confirmed) {
        this.confirmed = confirmed;
    }
}

