package org.edx.mobile.event;

import android.support.annotation.NonNull;

import org.edx.mobile.user.ProfileImage;

public class ProfilePhotoFetchedEvent {
    @NonNull
    private final ProfileImage profileImage;

    public ProfilePhotoFetchedEvent(@NonNull ProfileImage profileImage) {
        this.profileImage = profileImage;
    }

    @NonNull
    public ProfileImage getProfileImage() {
        return profileImage;
    }
}
