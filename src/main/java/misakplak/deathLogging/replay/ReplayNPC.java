package misakplak.deathLogging.replay;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import misakplak.deathLogging.misc.SwingHand;
import misakplak.deathLogging.recordables.Position;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class ReplayNPC {
    private final ServerPlayer npc;

    public ReplayNPC(Location spawn, String name) {

        MinecraftServer minecraftserver = MinecraftServer.getServer();
        ServerLevel level = ((CraftWorld) spawn.getWorld()).getHandle();

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);

        npc = new ServerPlayer(
                minecraftserver,
               level,
                gameProfile,
                ClientInformation.createDefault()
        );

        CommonListenerCookie cookie = CommonListenerCookie.createInitial(gameProfile, false);
        npc.connection = new ServerGamePacketListenerImpl(
                minecraftserver,
                new Connection(PacketFlow.CLIENTBOUND),
                npc,
                cookie
        );

        npc.setYRot(spawn.getYaw());
        npc.setXRot(spawn.getPitch());
    }

    public void spawn(Player viewer) {

        ServerPlayer conection = ((CraftPlayer) viewer).getHandle();

        MinecraftServer minecraftserver = MinecraftServer.getServer();

        conection.connection.send(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(npc)));

        ServerEntity serverEntity = new ServerEntity(
                npc.level(),
                npc,
                2,
                true,
                new ServerEntity.Synchronizer() {
                    @Override
                    public void sendToTrackingPlayers(Packet<? super ClientGamePacketListener> packet) {}

                    @Override
                    public void sendToTrackingPlayersAndSelf(Packet<? super ClientGamePacketListener> packet) {}

                    @Override
                    public void sendToTrackingPlayersFiltered(
                            Packet<? super ClientGamePacketListener> packet,
                            Predicate<ServerPlayer> predicate) {}
                },
                Set.of()
        );

        Packet<ClientGamePacketListener> packet = npc.getAddEntityPacket(serverEntity);

        conection.connection.send(packet);


        conection.connection.send(
                new ClientboundSetEntityDataPacket(
                        npc.getId(),
                        npc.getEntityData().getNonDefaultValues()
                )
        );

        conection.connection.send(
                new ClientboundRotateHeadPacket(
                        npc,
                        (byte) (npc.getYRot() * 256 / 360)
                )
        );
    }

    public void destroy(Player viewer) {
        ServerPlayer conection = ((CraftPlayer) viewer).getHandle();

        conection.connection.send(
                new net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket(
                        npc.getId()
                )
        );

        conection.connection.send(
                new ClientboundPlayerInfoRemovePacket(
                        List.of(npc.getUUID())
                )
        );
    }

    public void teleport(Player viewer, Position location) {

        npc.setPos(location.x(), location.x(), location.x());
        npc.setYRot(location.yaw());
        npc.setXRot(location.pitch());

        ClientboundTeleportEntityPacket packet =
                ClientboundTeleportEntityPacket.teleport(
                        npc.getId(),
                        new net.minecraft.world.entity.PositionMoveRotation(
                                npc.position(),
                                npc.getDeltaMovement(),
                                location.yaw(),
                                location.pitch()
                        ),
                        java.util.Set.of(),
                        npc.onGround()
                );

        ((CraftPlayer) viewer).getHandle().connection.send(packet);

        ((CraftPlayer) viewer).getHandle().connection.send(
                new ClientboundRotateHeadPacket(
                        npc,
                        (byte) (location.pitch() * 256.0F / 360.0F)
                )
        );
    }

    public ServerPlayer getServerPlayer() {
        return npc;
    }

    public void PlayHurtAnimation(Player viewer) {
        npc.animateHurt(npc.getBukkitYaw());



        ServerPlayer conection = ((CraftPlayer) viewer).getHandle();

        conection.connection.send(new ClientboundHurtAnimationPacket(
                npc
        ));

        conection.connection.send(new ClientboundHurtAnimationPacket(
                npc.getId(),
                0f
        ));

        viewer.playSound(
                npc.getBukkitEntity().getLocation(),
                Sound.ENTITY_PLAYER_HURT,
                1f,
                1f
        );


    }

    public void PlayHandSwingAnimation(Player viewer, SwingHand hand) {

        ServerPlayer conection = ((CraftPlayer) viewer).getHandle();

        if (hand == SwingHand.MAIN){
            conection.connection.send(new ClientboundAnimatePacket(
                    npc,
                    0
            ));
        }else if (hand == SwingHand.OFF){
            conection.connection.send(new ClientboundAnimatePacket(
                    npc,
                    3
            ));
        }


    }

    public void PlayCritAnimation(Player viewer) {
        ServerPlayer conection = ((CraftPlayer) viewer).getHandle();

        conection.connection.send(new ClientboundAnimatePacket(
                npc,
                4
        ));
    }

    public void setArmor(Player viewer, EquipmentSlot slot) {
        ServerPlayer conection = ((CraftPlayer) viewer).getHandle();

        conection.connection.send(
                new ClientboundSetEquipmentPacket(
                        npc.getId(),
                        List.of(
                                Pair.of(
                                        slot,
                                        ItemStack.EMPTY
                                )
                        )
                )
        );
    }

}