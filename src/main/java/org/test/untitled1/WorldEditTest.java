package org.test.untitled1;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.FileInputStream;
public final class WorldEditTest extends JavaPlugin implements CommandExecutor {

    private WorldEditPlugin worldEdit;

    public void onEnable() {
        worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEdit == null) {
            getLogger().warning("WorldEdit plugin not found! Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getCommand("paste").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        Player player = (Player) sender;


        //wczytywanie
        String path = "D://MC/spigot_1.19.4/plugins/modernrestraunt1.schem";
        File file = new File(path);
        Clipboard clipboard;
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        } catch (Exception e)
        {
            player.sendMessage("Nie działa wczytanie schematika "+e.getMessage());
            throw new RuntimeException(e);
        }


        //Pokręcone zamienianie gracza Spigotowego na gracza WorldEditowego
        World actor = BukkitAdapter.adapt(player.getWorld()); // WorldEdit's native Player class extends Actor

        //wklejanie
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(actor)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(player.getLocation().getX(), player.getLocation().getY(),player.getLocation().getZ()))
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);
            editSession.close();
        } catch (WorldEditException e)
        {
            player.sendMessage("Nie wklejenie schematika "+e.getMessage());
            throw new RuntimeException(e);
        }
        player.sendMessage("Schematic pasted successfully!");
        return true;
    }
}