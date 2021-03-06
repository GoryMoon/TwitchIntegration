package net.blay09.mods.bmc.twitchintegration;

import net.blay09.mods.bmc.BetterMinecraftChat;
import net.blay09.mods.bmc.api.BetterMinecraftChatAPI;
import net.blay09.mods.bmc.api.IntegrationModule;
import net.blay09.mods.bmc.api.TokenPair;
import net.blay09.mods.bmc.twitchintegration.gui.GuiTwitchAuthentication;
import net.blay09.mods.bmc.twitchintegration.gui.GuiTwitchChannels;
import net.blay09.mods.bmc.twitchintegration.handler.TwitchBadge;
import net.blay09.mods.bmc.twitchintegration.handler.TwitchChatHandler;
import net.blay09.mods.bmc.twitchintegration.handler.TwitchManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.io.File;

@Mod(modid = TwitchIntegration.MOD_ID, name = TwitchIntegration.NAME, acceptedMinecraftVersions = "[1.10]", clientSideOnly = true, dependencies = "required-after:betterminecraftchat",
updateJSON = "http://balyware.com/new/forge_update?modid=" + TwitchIntegration.MOD_ID)
public class TwitchIntegration implements IntegrationModule {

	public static final String MOD_ID = "twitchintegration";
	public static final String NAME = "Twitch Integration";

	@Mod.Instance(MOD_ID)
	public static TwitchIntegration instance;

	private TextureAtlasSprite icon;

	private TwitchManager twitchManager;
	private TwitchChatHandler twitchChatHandler;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);

		ClientCommandHandler.instance.registerCommand(new CommandTwitch());
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		twitchManager = new TwitchManager();
		twitchChatHandler = new TwitchChatHandler(twitchManager);
		TwitchIntegrationConfig.load(new File(Minecraft.getMinecraft().mcDataDir, "config/BetterMinecraftChat/twitchintegration.json"));
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		BetterMinecraftChatAPI.registerIntegration(this);

		TwitchBadge.loadInbuiltBadge("broadcaster");
		TwitchBadge.loadInbuiltBadge("moderator");
		TwitchBadge.loadInbuiltBadge("turbo");
		TwitchBadge.loadInbuiltBadge("staff");
		TwitchBadge.loadInbuiltBadge("admin");
		TwitchBadge.loadInbuiltBadge("global_mod");
		TwitchBadge.loadInbuiltBadge("premium");
		TwitchBadge.loadInbuiltBadge("bits1");
		TwitchBadge.loadInbuiltBadge("bits100");
		TwitchBadge.loadInbuiltBadge("bits1000");
		TwitchBadge.loadInbuiltBadge("bits5000");
		TwitchBadge.loadInbuiltBadge("bits10000");
		TwitchBadge.loadInbuiltBadge("bits100000");
	}

	@SubscribeEvent
	public void onTextureStitch(TextureStitchEvent.Pre event) {
		icon = event.getMap().registerSprite(new ResourceLocation(MOD_ID, "icon"));
	}

	@SubscribeEvent
	public void onWorldJoined(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		if(!twitchManager.isConnected()) {
			twitchManager.connect();
		}
	}

	@Override
	public String getModId() {
		return MOD_ID;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public TextureAtlasSprite getIcon() {
		return icon;
	}

	@Override
	public GuiScreen getConfigScreen(GuiScreen parentScreen) {
		TokenPair tokenPair = BetterMinecraftChatAPI.getAuthManager().getToken(BetterMinecraftChat.TWITCH_INTEGRATION);
		if (tokenPair != null) {
			return new GuiTwitchChannels();
		} else {
			return new GuiTwitchAuthentication(parentScreen);
		}
	}

	public static TwitchChatHandler getTwitchChatHandler() {
		return instance.twitchChatHandler;
	}

	public static TwitchManager getTwitchManager() {
		return instance.twitchManager;
	}
}
