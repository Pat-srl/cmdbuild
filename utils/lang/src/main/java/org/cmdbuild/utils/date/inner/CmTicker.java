/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.date.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.time.ZoneOffset.UTC;
import java.time.ZonedDateTime;
import java.util.function.Supplier;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmTicker {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final CmTicker TICKER = new CmTicker();

    private final Supplier<ZonedDateTime> defaultTicker = this::getDefault, manualTicker = this::getManual;
    private Supplier<ZonedDateTime> delegate = defaultTicker;
    private ZonedDateTime time;

    private CmTicker() {
    }

    public static CmTicker getTicker() {
        return TICKER;
    }

    public ZonedDateTime now() {
        return delegate.get();
    }

    public synchronized void pause() {
        set(defaultTicker.get());
    }

    public synchronized void resume() {
        logger.debug("enable default ticker");
        delegate = defaultTicker;
        time = null;
    }

    public synchronized void set(ZonedDateTime time) {
        logger.debug("enable manual ticker with time = {}", toIsoDateTime(time));
        this.time = checkNotNull(time);
        delegate = manualTicker;
    }

    private ZonedDateTime getDefault() {
        return ZonedDateTime.now(UTC);
    }

    private ZonedDateTime getManual() {
        return checkNotNull(time);
    }

}
