package sh.calaba.instrumentationbackend.actions.device;

import java.util.HashMap;
import java.util.Map;

import sh.calaba.instrumentationbackend.Result;
import sh.calaba.instrumentationbackend.actions.Action;

/**
 * Created by PrabhashSingh on 31/10/21.
 */
public class ToastMessageReader implements Action {
    @Override
    public Result execute(String... args) {
        return new Result(true, NotificationListener.getInstance().getToastMSGs().toString());
    }

    @Override
    public String key() {
        return "get_toast";
    }
}
