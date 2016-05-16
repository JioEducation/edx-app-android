package org.edx.mobile.user;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.MimeTypeMap;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.edx.mobile.event.AccountDataLoadedEvent;
import org.edx.mobile.event.ProfilePhotoUpdatedEvent;
import org.edx.mobile.http.ApiConstants;
import org.edx.mobile.http.RetroHttpException;
import org.edx.mobile.logger.Logger;
import org.edx.mobile.model.Page;
import org.edx.mobile.profiles.BadgeAssertion;
import org.edx.mobile.model.api.EnrolledCoursesResponse;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.RestAdapter;
import retrofit.mime.TypedFile;

@Singleton
public class UserAPI {

    @NonNull
    private final UserService userService;

    private Logger logger = new Logger(UserAPI.class.getName());

    @Inject
    public UserAPI(@NonNull RestAdapter restAdapter) {
        userService = restAdapter.create(UserService.class);
    }

    public Account getAccount(@NonNull String username) throws RetroHttpException {
        final Account account = userService.getAccount(username);
        EventBus.getDefault().post(new AccountDataLoadedEvent(account));
        return account;
    }

    public Account updateAccount(@NonNull String username, @NonNull String field, @Nullable Object value) throws RetroHttpException {
        final Account updatedAccount = userService.updateAccount(username, Collections.singletonMap(field, value));
        EventBus.getDefault().post(new AccountDataLoadedEvent(updatedAccount));
        return updatedAccount;
    }

    public void setProfileImage(@NonNull String username, @NonNull final File file) throws RetroHttpException, IOException {
        final String mimeType = "image/jpeg";
        logger.debug("Uploading file of type " + mimeType + " from " + file.toString());
        userService.setProfileImage(
                username,
                "attachment;filename=filename." + MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType),
                new TypedFile(mimeType, file));
        EventBus.getDefault().post(new ProfilePhotoUpdatedEvent(username, Uri.fromFile(file)));
    }

    public void deleteProfileImage(@NonNull String username) throws RetroHttpException {
        userService.deleteProfileImage(username);
        EventBus.getDefault().post(new ProfilePhotoUpdatedEvent(username, null));
    }

    public Page<BadgeAssertion> getBadges(@NonNull String username, int page) throws RetroHttpException {
        return userService.getBadges(username, page, ApiConstants.STANDARD_PAGE_SIZE);
    }

    public List<EnrolledCoursesResponse> getUserEnrolledCourses(@NonNull String username) throws RetroHttpException {
        return userService.getUserEnrolledCourses(username);
    }
}
