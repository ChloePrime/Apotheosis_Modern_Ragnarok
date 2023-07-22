package mod.chloeprime.apotheosismodernragnarok.common.util;

import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;

public class OneUseLogger extends RunOnce<String, Throwable> {
    public OneUseLogger() {
        super(ApotheosisModernRagnarok::logError);
    }
}
