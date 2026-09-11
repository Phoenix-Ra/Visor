package org.vmstudio.visor.core.client.input.redirect;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.vmstudio.visor.api.ModLoader;
import org.vmstudio.visor.api.client.input.redirect.RegisterVRInputRedirect;
import org.vmstudio.visor.api.client.input.redirect.VRInputRedirect;
import org.vmstudio.visor.api.common.addon.VisorAddon;
import org.vmstudio.visor.api.common.addon.component.ComponentIds;
import org.vmstudio.visor.api.common.addon.component.ComponentRegistry;
import org.vmstudio.visor.api.common.utils.LoggerUtils;
import org.vmstudio.visor.core.client.VisorClientImpl;

import java.lang.reflect.Constructor;
import java.util.*;


public class VRInputRedirectRegistry implements ComponentRegistry<VRInputRedirect> {
    private static final String REGISTRY_NAME = "VR Input Redirects";

    private static final String COMPONENT_NAME = "VRInputRedirect";
    private static final String ANNOTATION_NAME = "@RegisterVRInputRedirect";

    @Getter
    private final HashMap<String, VRInputRedirect> componentsMap = new HashMap<>();

    private final List<VRInputRedirect> sortedComponents = new ArrayList<>();

    @Getter
    private final Collection<VRInputRedirect> allComponents =
            Collections.unmodifiableCollection(componentsMap.values());


    public List<VRInputRedirect> getSortedComponents() {
        return Collections.unmodifiableList(sortedComponents);
    }


    @Override
    public void registerAddonPath(@NotNull VisorAddon addon) {

        String path = addon.getAddonPackagePath();
        if(path == null){
            return;
        }
        List<Class<?>> annotated = ModLoader.get().getClassesAnnotated(
                RegisterVRInputRedirect.class,
                addon.getModId(),
                path
        );

        VisorClientImpl.LOGGER.info("Found {} {} to register in addon: '{}'",
                annotated.size(), COMPONENT_NAME, addon.getAddonId());

        for (Class<?> clazz : annotated) {
            if (!VRInputRedirect.class.isAssignableFrom(clazz)) {
                VisorClientImpl.LOGGER.warn(
                        "{} is annotated with {} but does not implement {}",
                        clazz.getName(), ANNOTATION_NAME, COMPONENT_NAME
                );
                continue;
            }
            try {
                @SuppressWarnings("unchecked")
                Constructor<? extends VRInputRedirect> constructor =
                        ((Class<? extends VRInputRedirect>) clazz)
                                .getConstructor(VisorAddon.class);

                var component = constructor.newInstance(addon);

                registerComponent(component);

            } catch (Exception e) {
                VisorClientImpl.LOGGER.error("Failed to register {} from class: {}", COMPONENT_NAME, clazz.getName());
                LoggerUtils.printError(e);
            }
        }
    }

    @Override
    public void registerComponent(@NotNull VRInputRedirect component) {
        String validationError = ComponentIds.validate(component.getId());
        if(validationError != null){
            throw new RuntimeException(
                    "Tried to register "+COMPONENT_NAME+" with ID '"
                            + component.getId()
                            + "'. From addon: '"+component.getOwner().getAddonId()
                            + "'. The ID pattern is incorrect: " + validationError);
        }

        var previous = componentsMap.put(component.getId(), component);

        if (previous != null) {
            VisorClientImpl.LOGGER.info(
                    "Overriding existing {}: '{}' from addon '{}'",
                    COMPONENT_NAME,
                    previous.getId(),
                    previous.getOwner().getAddonId()
            );
            sortedComponents.remove(previous);

        }else{
            VisorClientImpl.LOGGER.info("Registered {}: '{}'", COMPONENT_NAME, component.getId());
        }
        sortedComponents.add(component);
        Collections.sort(sortedComponents);
    }

    @Override
    public VRInputRedirect unregisterComponent(@NotNull String id) {
        var removed = componentsMap.remove(id);
        if(removed != null) {
            sortedComponents.remove(removed);
            Collections.sort(sortedComponents);
            VRInputRedirectHandler.INSTANCE.onUnregistered(removed);
            VisorClientImpl.LOGGER.info("Unregistered {}: '{}'", COMPONENT_NAME, removed.getId());
        }
        return removed;
    }

    @Override
    public @Nullable VRInputRedirect getComponent(@NotNull String id) {
        return componentsMap.get(id);
    }


    @Override
    public @NotNull String getRegistryName() {
        return REGISTRY_NAME;
    }
}
