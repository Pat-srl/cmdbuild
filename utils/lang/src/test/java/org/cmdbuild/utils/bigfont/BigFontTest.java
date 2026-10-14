/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.bigfont;

import java.io.IOException;
import static org.cmdbuild.utils.bigfont.BigFontUtils.convertToBarlineFont;
import static org.cmdbuild.utils.bigfont.BigFontUtils.convertToFatFont;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author ataboga
 */
public class BigFontTest {

    @Test
    public void test() throws IOException {
        assertEquals("""
                        ____ __  __ ____  ____        _ _     _
                       / ___|  \\/  |  _ \\| __ ) _   _(_) | __| |
                      | |   | |\\/| | | | |  _ \\| | | | | |/ _` |
                      | |___| |  | | |_| | |_) | |_| | | | (_| |
                       \\____|_|  |_|____/|____/ \\__,_|_|_|\\__,_|
                      """, convertToBarlineFont("CMDBuild"));
        assertEquals("""
                                              __  __    _    ___ _   _ _____
                        ___  _ __   ___ _ __ |  \\/  |  / \\  |_ _| \\ | |_   _|
                       / _ \\| '_ \\ / _ \\ '_ \\| |\\/| | / _ \\  | ||  \\| | | |
                      | (_) | |_) |  __/ | | | |  | |/ ___ \\ | || |\\  | | |
                       \\___/| .__/ \\___|_| |_|_|  |_/_/   \\_\\___|_| \\_| |_|
                            |_|
                      """, convertToBarlineFont("openMAINT"));
        assertEquals("""
                      ____  _____    _    ____ __   ______  _   _ ____  _____
                     |  _ \\| ____|  / \\  |  _ \\\\ \\ / /___ \\| | | / ___|| ____|
                     | |_) |  _|   / _ \\ | | | |\\ V /  __) | | | \\___ \\|  _|
                     |  _ <| |___ / ___ \\| |_| | | |  / __/| |_| |___) | |___
                     |_| \\_\\_____/_/   \\_\\____/  |_| |_____|\\___/|____/|_____|
                     """, convertToBarlineFont("READY2USE"));
        assertEquals("""
                       ____ __  __ ____  ____        _ _     _    ____  _____    _    ____ __   ______  _   _ ____  _____
                      / ___|  \\/  |  _ \\| __ ) _   _(_) | __| |  |  _ \\| ____|  / \\  |  _ \\\\ \\ / /___ \\| | | / ___|| ____|
                     | |   | |\\/| | | | |  _ \\| | | | | |/ _` |  | |_) |  _|   / _ \\ | | | |\\ V /  __) | | | \\___ \\|  _|
                     | |___| |  | | |_| | |_) | |_| | | | (_| |  |  _ <| |___ / ___ \\| |_| | | |  / __/| |_| |___) | |___
                      \\____|_|  |_|____/|____/ \\__,_|_|_|\\__,_|  |_| \\_\\_____/_/   \\_\\____/  |_| |_____|\\___/|____/|_____|
                     """, convertToBarlineFont("CMDBuild READY2USE"));

        assertEquals("CMDBuild...", convertToBarlineFont("CMDBuild..."));
    }

    @Test
    public void testAll() {
        assertEquals("""
                            _              _       __       _     _   _ _    _                                            _
                       __ _| |__   ___  __| | ___ / _| __ _| |__ (_) (_) | __ |_ __ ___  _ __   ___  _ __   __ _ _ __ ___| |_ _   ___   ___      ___  ___   _ ____
                      / _` | '_ \\ / __|/ _` |/ _ \\ |_ / _` | '_ \\| | | | |/ / | '_ ` _ \\| '_ \\ / _ \\| '_ \\ / _` | '__/ __| __| | | \\ \\ / \\ \\ /\\ / \\ \\/ / | | |_  /
                     | (_| | |_) | (__| (_| |  __/  _| (_| | | | | | | |   <| | | | | | | | | | (_) | |_) | (_| | |  \\__ \\ |_| |_| |\\ V / \\ V  V / >  <| |_| |/ /
                      \\__,_|_.__/ \\___|\\__,_|\\___|_|  \\__, |_| |_|_|_/ |_|\\_\\_|_| |_| |_|_| |_|\\___/| .__/ \\__, |_|  |___/\\__|\\__,_| \\_/   \\_/\\_/ /_/\\_\\\\__, /___|
                                                      |___/        |__/                             |_|       |_|                                       |___/
                     """, convertToBarlineFont("abcdefghijklmnopqrstuvwxyz"));
        assertEquals("""
                         _    ____   ____ ____  _____ _____  ____ _   _ ___     _ _  ___     __  __ _   _  ___  ____   ___  ____  ____  _____ _   ___     ___        ___  ___   _______
                        / \\  | __ ) / ___|  _ \\| ____|  ___|/ ___| | | |_ _|   | | |/ / |   |  \\/  | \\ | |/ _ \\|  _ \\ / _ \\|  _ \\/ ___||_   _| | | \\ \\   / \\ \\      / \\ \\/ \\ \\ / /__  /
                       / _ \\ |  _ \\| |   | | | |  _| | |_  | |  _| |_| || | _  | | ' /| |   | |\\/| |  \\| | | | | |_) | | | | |_) \\___ \\  | | | | | |\\ \\ / / \\ \\ /\\ / / \\  / \\ V /  / /
                      / ___ \\| |_) | |___| |_| | |___|  _| | |_| |  _  || || |_| | . \\| |___| |  | | |\\  | |_| |  __/| |_| |  _ < ___) | | | | |_| | \\ V /   \\ V  V /  /  \\  | |  / /_
                     /_/   \\_\\____/ \\____|____/|_____|_|    \\____|_| |_|___|\\___/|_|\\_\\_____|_|  |_|_| \\_|\\___/|_|    \\__\\_\\_| \\_\\____/  |_|  \\___/   \\_/     \\_/\\_/  /_/\\_\\ |_| /____|
                     """, convertToBarlineFont("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        assertEquals("""
                       ___  _ ____  _____ _  _   ____   __   _____  ___   ___
                      / _ \\/ |___ \\|___ /| || | | ___| / /_ |___  |( _ ) / _ \\
                     | | | | | __) | |_ \\| || |_|___ \\| '_ \\   / / / _ \\| (_) |
                     | |_| | |/ __/ ___) |__   _|___) | (_) | / / | (_) |\\__, |
                      \\___/|_|_____|____/   |_| |____/ \\___/ /_/   \\___/   /_/
                     """, convertToBarlineFont("0123456789"));
    }

    @Test
    @Ignore
    public void testFont() {
        System.out.println(convertToBarlineFont("CMDBuild"));
        System.out.println(convertToFatFont("CMDBuild"));
        System.out.println(convertToFatFont("openMAINT"));
        System.out.println(convertToFatFont("CMDBuild READY2USE"));
    }
}
