package xyz.auriium.mattlib2;

import xyz.auriium.mattlib2.log.ConsoleComponent;
import xyz.auriium.mattlib2.loop.IMattlibHooked;
import xyz.auriium.yuukonstants.exception.ExplainedException;

import static java.lang.String.format;

public class MattConsole implements IMattlibHooked {

    final ConsoleComponent component;

    public MattConsole(ConsoleComponent component) {
        this.component = component;

        mattRegister();
    }

    String stringCache;

    @Override public void logPeriodic() {
        component.reportToConsole(stringCache);

        stringCache = "";
    }

    public void reportExceptions(ExplainedException[] ahhhh) {

        StringBuilder diseaster = new StringBuilder();
        for (int i = 0; i < ahhhh.length; i++) {
            ExplainedException e = ahhhh[i];
            diseaster
                    .append("\n")
                    .append("\n")
                    .append(e.toOutput())
                    .append("\n")
                    .append("\n");

            if (e.getStackTrace().length >= 1) {

                for (int h = 0; h < Math.min(e.getStackTrace().length, 4); h++) {
                    diseaster.append(format("stacktrace %s: ", i)).append(e.getStackTrace()[i]);
                }
            }
        }

        stringCache = diseaster.toString();
    }
}
