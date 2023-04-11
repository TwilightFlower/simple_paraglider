package io.github.twilightflower.paraglider;

import io.github.twilightflower.paraglider.client.BakedModelHelper;
import io.github.twilightflower.paraglider.client.ClientEvents;
import io.github.twilightflower.paraglider.client.RenderParaglider;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod(modid = SimpleParagliderMod.MODID, name = "Simple Paraglider", version = "1.0.2")
public class SimpleParagliderMod {
	public static final String MODID = "simple_paraglider";
	public static final Item PARAGLIDER = new ParagliderItem()
			.setTranslationKey("simple_paraglider.paraglider")
			.setMaxDamage(1000)
			.setMaxStackSize(1)
			.setCreativeTab(CreativeTabs.TOOLS);
	
	public static boolean isClient;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		
		if(event.getSide() == Side.CLIENT) {
			isClient = true;
			MinecraftForge.EVENT_BUS.register(new ClientEvents());
			MinecraftForge.EVENT_BUS.register(new BakedModelHelper());
			RenderingRegistry.registerEntityRenderingHandler(ParagliderEntity.class, RenderParaglider::new);
			RenderParaglider.init();
		}
		
		Config.load(event.getSuggestedConfigurationFile());
		
		EntityRegistry.registerModEntity(id("paraglider"), ParagliderEntity.class, "paraglider", 0, this, 64, 2, true);
	}
	
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
		register(id("paraglider"), PARAGLIDER, event);
	}
	
	@SubscribeEvent
	public void dismountHandler(EntityMountEvent event) {
		Entity mounted = event.getEntityBeingMounted();
		
		if(event.isDismounting() && mounted instanceof ParagliderEntity) {
			((ParagliderEntity) mounted).shouldDismount = true;
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void preventInteraction(PlayerInteractEvent event) {
		if(!(event instanceof PlayerInteractEvent.EntityInteractSpecific)
				&& event.getEntityPlayer().getRidingEntity() instanceof ParagliderEntity
				&& event.isCancelable()) {
			event.setCanceled(true);
		}
	}
	
	public static ResourceLocation id(String path) {
		return new ResourceLocation(MODID, path);
	}
	
	private static <T extends IForgeRegistryEntry<T>> void register(ResourceLocation id, T entry, RegistryEvent.Register<T> event) {
		if(isClient && entry instanceof Item) {
			ModelLoader.setCustomModelResourceLocation((Item) entry, 0, new ModelResourceLocation(id.toString()));
		}
		
		entry.setRegistryName(id);
		event.getRegistry().register(entry);
	}
}
