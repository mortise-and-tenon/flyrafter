package org.mt.flyrafter.callback;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.mt.flyrafter.FlyRafter;
import org.mt.flyrafter.FlyRafterBuilder;
import org.mt.flyrafter.configuration.FlyRafterConfiguration;

import javax.sql.DataSource;

/**
 * 用于对接 flyway 的 Callback
 *
 * @author Moon Wu
 * @date 2021/4/22
 */
@Slf4j
public class FlyRafterCallback implements Callback {

    public FlyRafterCallback() {
    }

    public boolean supports(Event event, Context context) {
        return true;
    }

    public boolean canHandleInTransaction(Event event, Context context) {
        return false;
    }

    public void handle(Event event, Context context) {
        log.debug("flyrafter before validate callback event : {}", event.name());
    }

    public String getCallbackName() {
        return FlyRafterCallback.class.getSimpleName();
    }
}
