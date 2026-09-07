package org.vmstudio.visor.loader.neoforge;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.vmstudio.visor.api.ModLoader;
import org.vmstudio.visor.api.VisorAPI;
import org.vmstudio.visor.core.common.addon.AddonManagerImpl;

//? if >=1.20.4 {
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
//?}

@Mod(VisorAPI.MOD_ID)
public class VisorMod {

    public VisorMod(final IEventBus modEventBus){
        modEventBus.addListener(this::onLoadComplete);
        //? if >=1.20.4 {
        modEventBus.addListener(this::onRegisterPayloadHandler);
        //?}
    }

    private void onLoadComplete(final FMLLoadCompleteEvent event){
        event.enqueueWork(AddonManagerImpl::register);
    }

    //? if >=1.20.4 {
    private void onRegisterPayloadHandler(final RegisterPayloadHandlerEvent event){
        if (ModLoader.get() instanceof NeoForgeModLoader loader) {
            loader.registerPayloads(event);
        }
    }
    //?}


}
