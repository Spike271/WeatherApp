import java.io.File;
import java.net.URISyntaxException;

public class ApplicationUtils
{
    public static String jarFilePath;

    static
    {
        try
        {
            ApplicationUtils.initStrings temp = new ApplicationUtils.initStrings();
            jarFilePath = temp.mainPath;

            if(jarFilePath.contains(".jar"))
            {
                jarFilePath = new File(jarFilePath).getParent();
                jarFilePath += "/";
            }
        }
        catch (URISyntaxException _) {}
    }

    private static class initStrings
    {
        public String mainPath;

        public initStrings() throws URISyntaxException
        {
            mainPath = ApplicationUtils.class.getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI()
                            .getPath()
                            .replaceAll("%20", " ");
        }
    }
}
