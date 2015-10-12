package uk.ac.uea.framework;

/**
 * Created by dha13jyu on 09/10/2015.
 */
public abstract class FrameworkCopyright {
    private final String text = "This application is based on the Simple Android Application Framework. (c) University\n" +
            "of East Anglia 2015.";

    public final String getCopyright(){
        return text + "\n\n" + getAppCopyright();
    }

    protected abstract String getAppCopyright();
}
