package org.cmdbuild.auth;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.LoginType;
import org.junit.Test;
import static org.cmdbuild.auth.login.file.FileAuthUtils.buildAuthFile;
import static org.cmdbuild.auth.login.file.FileAuthUtils.isAuthFilePassword;
import static org.cmdbuild.auth.login.file.FileAuthUtils.isValidAuthFilePassword;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoginTest {

    @Test
    public void testFileAuthUtils() {
        File dir = tempDir();

        String password = buildAuthFile(dir).getPassword();

        assertTrue(isAuthFilePassword("file:asd"));
        assertTrue(isAuthFilePassword(password));
        assertFalse(isAuthFilePassword("file:../something"));
        assertFalse(isAuthFilePassword("file:ast.xml"));

        assertTrue(isValidAuthFilePassword(dir, password));
        assertFalse(isValidAuthFilePassword(dir, "file:asd"));

        deleteQuietly(dir);
    }

    @Test
    public void theAtCharacterDoesNotDiscriminatesBetweenEmailAndUsername() {
        String STRING_WITHOUT_AT = "anything without the at char";
        String STRING_WITH_AT = "anything with the @ char"; // "firstname.surname@example.com";

        LoginUserIdentity usernameLogin = LoginUserIdentity.builder() //
                .withValue(STRING_WITHOUT_AT) //
                .build();

        LoginUserIdentity emailLogin = LoginUserIdentity.builder() //
                .withValue(STRING_WITH_AT) //
                .build();

        assertThat(usernameLogin.getValue(), is(STRING_WITHOUT_AT));
        assertThat(usernameLogin.getType(), is(LoginType.LT_AUTO));

        assertThat(emailLogin.getValue(), is(STRING_WITH_AT));
        assertThat(emailLogin.getType(), is(LoginType.LT_AUTO));
    }

    @Test(expected = NullPointerException.class)
    public void disallowsNullLoginStrings() {
        LoginUserIdentity.builder() //
                .build();
    }

}
