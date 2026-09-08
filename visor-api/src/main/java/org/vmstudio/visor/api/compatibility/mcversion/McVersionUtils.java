package org.vmstudio.visor.api.compatibility.mcversion;

import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.AABB;
//? if >=1.20.5 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.CustomModelData;
//?} else {
/*import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
*///?}

/**
 * Cross-mc-version Utils for common methods
 */
public class McVersionUtils {
    private McVersionUtils() {
        throw new UnsupportedOperationException("This is an utility class and cannot be instantiated");
    }

    // ------- RESOURCES -------

    public static ResourceLocation newResourceLoc(String namespace,
                                                  String path){
        return new ResourceLocation(namespace, path);
    }
    public static ResourceLocation newResourceLoc(String location){
        return new ResourceLocation(location);
    }


    // ------- TEXT HELPERS -------
    public static String filterText(String text,
                                    boolean allowLineBreaks){
        //? if >=1.20.5 {
        return StringUtil.filterText(text, allowLineBreaks);
        //?} else {
        /*return SharedConstants.filterText(text, allowLineBreaks);
        *///?}
    }

    public static boolean isAllowedChatCharacter(char character){
        //? if >=1.20.5 {
        return StringUtil.isAllowedChatCharacter(character);
        //?} else {
        /*return SharedConstants.isAllowedChatCharacter(character);
        *///?}
    }


    // ------- ENTITY -------

    // 1.20.5 made EntityDimensions a record
    public static float dimensionsWidth(EntityDimensions dimensions){
        //? if >=1.20.5 {
        return dimensions.width();
        //?} else {
        /*return dimensions.width;
        *///?}
    }

    // 1.20.5 replaced ServerGamePacketListenerImpl.MAX_INTERACTION_DISTANCE
    // with the block/entity interaction range attributes
    public static boolean canInteractWithEntity(Player player,
                                                AABB boundingBox){
        //? if >=1.20.5 {
        return player.canInteractWithEntity(boundingBox, 1.0);
        //?} else {
        /*return boundingBox.distanceToSqr(player.getEyePosition())
                < ServerGamePacketListenerImpl.MAX_INTERACTION_DISTANCE;
        *///?}
    }

    public static boolean canInteractWithBlock(Player player,
                                               BlockPos blockPos){
        //? if >=1.20.5 {
        return player.canInteractWithBlock(blockPos, 1.0);
        //?} else {
        /*return blockPos.distToCenterSqr(player.getEyePosition())
                < ServerGamePacketListenerImpl.MAX_INTERACTION_DISTANCE;
        *///?}
    }


    // 1.20.5 replaced Entity.setMaxUpStep with the STEP_HEIGHT attribute
    public static void setStepHeight(LivingEntity entity, float stepHeight){
        //? if >=1.20.5 {
        AttributeInstance instance = entity.getAttribute(Attributes.STEP_HEIGHT);
        if (instance != null) {
            instance.setBaseValue(stepHeight);
        }
        //?} else {
        /*entity.setMaxUpStep(stepHeight);
        *///?}
    }


    // ------- ITEMS -------
    // 1.20.5 replaced ItemStack NBT tags with data components

    public static ChatFormatting rarityColor(Rarity rarity){
        //? if >=1.20.5 {
        return rarity.color();
        //?} else {
        /*return rarity.color;
        *///?}
    }

    public static boolean hasCustomHoverName(ItemStack itemStack){
        //? if >=1.20.5 {
        return itemStack.has(DataComponents.CUSTOM_NAME);
        //?} else {
        /*return itemStack.hasCustomHoverName();
        *///?}
    }

    public static void addItemAttributeModifiers(LivingEntity entity,
                                                 ItemStack itemStack,
                                                 EquipmentSlot slot){
        //? if >=1.20.5 {
        itemStack.forEachModifier(slot, (attribute, modifier) -> {
            AttributeInstance instance = entity.getAttributes().getInstance(attribute);
            if (instance != null) {
                instance.removeModifier(modifier);
                instance.addTransientModifier(modifier);
            }
        });
        //?} else {
        /*entity.getAttributes().addTransientAttributeModifiers(
                itemStack.getAttributeModifiers(slot));
        *///?}
    }

    public static void removeItemAttributeModifiers(LivingEntity entity,
                                                    ItemStack itemStack,
                                                    EquipmentSlot slot){
        //? if >=1.20.5 {
        itemStack.forEachModifier(slot, (attribute, modifier) -> {
            AttributeInstance instance = entity.getAttributes().getInstance(attribute);
            if (instance != null) {
                instance.removeModifier(modifier);
            }
        });
        //?} else {
        /*entity.getAttributes().removeAttributeModifiers(
                itemStack.getAttributeModifiers(slot));
        *///?}
    }

    public static int customModelData(ItemStack itemStack){
        //? if >=1.20.5 {
        CustomModelData data = itemStack.get(DataComponents.CUSTOM_MODEL_DATA);
        return data == null ? 0 : data.value();
        //?} else {
        /*CompoundTag tag = itemStack.getTag();
        return tag == null ? 0 : tag.getInt("CustomModelData");
        *///?}
    }

}
