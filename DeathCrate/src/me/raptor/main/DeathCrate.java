package me.raptor.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class DeathCrate extends JavaPlugin implements Listener{
public void onEnable() {
	Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lDeath Crate has been enabled"));
	Bukkit.getPluginManager().registerEvents(this, this);
	
}
HashMap<Location, Material> broken = new HashMap<Location, Material>();
ArrayList<Location> spawned = new ArrayList<Location>();
public void onDisable() {
	Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lDeath Crate has been disabled"));
}
boolean enable = true;
public boolean onCommand(CommandSender sd, Command cmd, String cmdLabel, String[] args) {
	if (cmd.getName().equalsIgnoreCase("deathcrate")) {
		if (args.length != 0) {
		switch (args[0].toLowerCase()) {
			case "enable":
				enable = true;
				Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Death Crate feature has been enabled");

				return true;
				
			case "disable":
				enable = false;
				Bukkit.getServer().broadcastMessage(ChatColor.RED + "Death Crate feature has been disabled");
				return true;
				
			default:
				sd.sendMessage("Unknown command for DeathCrate");
				return true;
		}
	}
	
	}
	return true;
}



@EventHandler
public void onPlayerDeath(PlayerDeathEvent e) {
	if (enable) {
	Player p = e.getEntity();
	e.getDrops().clear();
	Inventory player = p.getInventory();
			Material a = p.getLocation().add(1, 1, 0).getBlock().getType();
			if (p.getLocation().getBlock().getType() != Material.AIR) 
			p.getLocation().add(1, 0, 0).getBlock().setType(Material.AIR);
			p.getLocation().add(1, 1, 0).getBlock().setType(Material.AIR);
			p.getLocation().getBlock().setType(Material.CHEST);
			p.getLocation().add(1, 0, 0).getBlock().setType(Material.CHEST);
			Chest c1 = (Chest) p.getLocation().getBlock().getState();
			Chest c2 = (Chest) p.getLocation().add(1, 0, 0).getBlock().getState();
			
			org.bukkit.block.data.type.Chest cd1 = (org.bukkit.block.data.type.Chest) c1.getBlockData();
			org.bukkit.block.data.type.Chest cd2 = (org.bukkit.block.data.type.Chest) c2.getBlockData();
			cd1.setType(org.bukkit.block.data.type.Chest.Type.LEFT);
			cd2.setType(org.bukkit.block.data.type.Chest.Type.RIGHT);
			c1.setBlockData(cd1);
			c2.setBlockData(cd2);
			c2.setCustomName(ChatColor.RED + p.getName() + "'s Death Crate");
			c1.setCustomName(ChatColor.RED + p.getName() + "'s Death Crate");
			c1.update();
			c2.update();
			c2.getInventory().setContents(player.getContents());
			p.getLocation().add(1,1,0).getBlock().setType(a);
			spawned.add(c1.getLocation());
			spawned.add(c2.getLocation());
			new BukkitRunnable() {
				public void run() {
					if (spawned.contains(c1.getLocation())) spawned.remove(c1.getLocation());
					if (spawned.contains(c2.getLocation())) spawned.remove(c2.getLocation());
				}
			}.runTaskLater(this, 5);
			
	}

}


@EventHandler
public void onBlockBreak(BlockBreakEvent e) {
	if (e.getBlock().getState() instanceof Chest) {
		Chest c = (Chest) e.getBlock().getState();
		if (c.getCustomName() != null) {
			if (c.getCustomName().contains("Death Crate")) {
				e.setCancelled(true);
				e.getBlock().setType(Material.AIR);
				return;
			}
		}
	}
}
@EventHandler
public void onBlockExplode(EntityExplodeEvent e) {
		e.setCancelled(true);
		for (Block b : e.blockList()) {
			if (spawned.contains(b.getLocation())) continue;
			b.breakNaturally();
			Random r = new Random();
			int ra = r.nextInt(100);
			if (ra < e.getYield()*100) {
			b.getDrops().clear();
			}
		
	}
	
	}

@EventHandler
public void onPlayerCloseInventory(InventoryCloseEvent e) {
	if (e.getView().getTitle() != null) {

		if (e.getInventory().getHolder() instanceof DoubleChest) {
			DoubleChest c = (DoubleChest) e.getInventory().getHolder();
			Chest left = (Chest) c.getLeftSide();
			Chest right = (Chest) c.getRightSide();
			if (e.getView().getTitle().contains("Death Crate")) {
				for (ItemStack a : c.getInventory().getContents()) {
					if (a != null) {
						return;
					}
				}
				left.getBlock().setType(Material.AIR);
				right.getBlock().setType(Material.AIR);
			}
		} else if (e.getInventory().getHolder() instanceof Chest) {
			Chest c = (Chest) e.getInventory().getHolder();
			if (e.getView().getTitle().contains("Death Crate")) {
				for (ItemStack a : c.getInventory().getContents()) {
					if (a != null) {
						return;
					}
				}
				c.getBlock().setType(Material.AIR);
			}
		}
		
	}
}

}
