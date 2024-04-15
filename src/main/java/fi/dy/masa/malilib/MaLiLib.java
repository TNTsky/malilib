package fi.dy.masa.malilib;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import fi.dy.masa.malilib.event.InitializationHandler;

public class MaLiLib implements ModInitializer
{
    public static final Logger logger = LogManager.getLogger(MaLiLibReference.MOD_ID);

    @Override
    public void onInitialize()
    {
        InitializationHandler.getInstance().registerInitializationHandler(new MaLiLibInitHandler());
    }

    public static void printDebug(String key, Object... args)
    {
        if (MaLiLibConfigs.Debug.DEBUG_LOG.getBooleanValue())
        {
            logger.info(key, args);
        }
    }
}
