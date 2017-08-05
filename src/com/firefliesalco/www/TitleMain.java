package com.firefliesalco.www;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class TitleMain extends JavaPlugin implements Listener{

	public HashMap<UUID, List<String>> playerOwned = new HashMap<UUID, List<String>>();
	public HashMap<UUID, String> activeTitle = new HashMap<UUID, String>();
	public HashMap<UUID, String> typing = new HashMap<UUID, String>();
	public HashMap<UUID, Integer> points = new HashMap<UUID, Integer>();
	public HashMap<UUID, Boolean> capital = new HashMap<UUID, Boolean>();
	public HashMap<UUID, Boolean> italic = new HashMap<UUID, Boolean>();
	public HashMap<UUID, Boolean> bold = new HashMap<UUID, Boolean>();
	public HashMap<UUID, Boolean> strikethrough = new HashMap<UUID, Boolean>();
	public HashMap<UUID, Boolean> underline = new HashMap<UUID, Boolean>();
	public HashMap<UUID, ChatColor> color = new HashMap<UUID, ChatColor>();
	
	
	@Override
	public void onEnable(){
		createConfig();
		getServer().getPluginManager().registerEvents(this, this);
		for(Player p : getServer().getOnlinePlayers()){
			loadPlayer(p);
		}
	}

	
	
	@Override
	public void onDisable(){
		for(Player p : getServer().getOnlinePlayers()){
			savePlayer(p);
		}
	}
	
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		loadPlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		unloadPlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event){
		String active = activeTitle.get(event.getPlayer().getUniqueId());
		event.setFormat(event.getFormat().replace("{TITLE}", active.equals("//NONE//") ? "" : ChatColor.GRAY + "[" + active + ChatColor.GRAY + "]"));
	}
	
	private void createConfig() {
	    try {
	        if (!getDataFolder().exists()) {
	            getDataFolder().mkdirs();
	        }
	        File file = new File(getDataFolder(), "config.yml");
	        if (!file.exists()) {
	            getLogger().info("Config.yml not found, creating!");
	            saveDefaultConfig();
	            saveConfig();
	        } else {
	            getLogger().info("Config.yml found, loading!");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();

	    }
	    
	}
	
	public void createTitle(Player p){
		if(bold.containsKey(p.getUniqueId())){
			Inventory inv = Bukkit.createInventory(null, 27, "Customize Title: " + typing.get(p.getUniqueId()));
			inv.setItem(0, wool(p, "BLACK", 15));
			inv.setItem(1, wool(p, "DARK_BLUE", 11));
			inv.setItem(2, wool(p, "DARK_GREEN", 13));
			inv.setItem(3, wool(p, "DARK_AQUA", 9));
			inv.setItem(4, wool(p, "DARK_RED", 14));
			inv.setItem(5, wool(p, "DARK_PURPLE", 10));
			inv.setItem(6, wool(p, "GOLD", 1));
			inv.setItem(7, wool(p, "GRAY", 8));
			inv.setItem(10, wool(p, "DARK_GRAY", 7));
			inv.setItem(11, wool(p, "BLUE", 12));
			inv.setItem(12, wool(p, "GREEN", 5));
			inv.setItem(13, wool(p, "AQUA", 3));
			inv.setItem(14, wool(p, "RED", 6));
			inv.setItem(15, wool(p, "LIGHT_PURPLE", 2));
			inv.setItem(16, wool(p, "YELLOW", 4));
			inv.setItem(17, wool(p, "WHITE", 0));
			
			inv.setItem(20, button(p, bold, "BOLD"));
			inv.setItem(21, button(p, strikethrough, "STRIKETHROUGH"));
			ItemStack capitalz = new ItemStack(Material.INK_SACK, 1, (short) (capital.get(p.getUniqueId()) ? 10 : 8));
			ItemMeta im = capitalz.getItemMeta();
			im.setDisplayName("CAPITALIZED");
			capitalz.setItemMeta(im);
			inv.setItem(22, capitalz);
			inv.setItem(23, button(p, underline, "UNDERLINE"));
			inv.setItem(24, button(p, italic, "ITALIC"));
			ItemStack arrow = new ItemStack(Material.ARROW);
			ItemMeta ima = arrow.getItemMeta();
			ima.setDisplayName("Next Letter ->");
			arrow.setItemMeta(ima);
			inv.setItem(26, arrow);
			p.openInventory(inv);
		}
	}
	
	public void invert(int slot, Player p){
		HashMap<UUID, Boolean> map = null;
		if(slot == 20)
			map = bold;
		if(slot == 21)
			map = strikethrough;
		if(slot == 22)
			map = capital;
		if(slot == 23)
			map = underline;
		if(slot == 24)
			map = italic;
		map.put(p.getUniqueId(), !map.get(p.getUniqueId()));
	}
	
	public ItemStack button(Player p, HashMap<UUID, Boolean> map, String color){
		ItemStack is = new ItemStack(Material.INK_SACK, 1, (short) (map.get(p.getUniqueId()) ? 10 : 8));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.valueOf(color)+ color);
		is.setItemMeta(im);
		return is;
	}
	
	public ItemStack wool(Player p, String color, int damage){
		ItemStack is = new ItemStack(Material.WOOL, 1, (short) damage);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.valueOf(color)+ "" + ChatColor.BOLD + color);
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		is.setItemMeta(im);

		if(ChatColor.valueOf(color) == this.color.get(p.getUniqueId()))
			is.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		
		return is;
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event){
		if(event.getInventory().getName().split(" ")[0].equals("Customize") && capital.containsKey(event.getPlayer().getUniqueId())){
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
				public void run(){
					createTitle((Player) event.getPlayer());
				}
			}, 2);
		}
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(label.equalsIgnoreCase("title") && sender.hasPermission("title.change")){
			if(args.length == 0){
				showTitles((Player)sender, 1);
			}
			if(args.length == 1){
				if(args[0].equalsIgnoreCase("help")){
					sender.sendMessage(ChatColor.AQUA + "---- " + ChatColor.GOLD + "Title Commands" + ChatColor.AQUA + " ----");
					msg(sender, "", "Choose and customize your title");
					msg(sender, "help", "List title commands");
					msg(sender, "clear", "Clear your title");
					msg(sender, "bal(ance)", "Check your title creation points");
					msg(sender, "add <player> <amount>", "Gives the player x amount of custom titles");
					msg(sender, "give <player> <title>", "Gives the player the title");
					msg(sender, "remove <player> <title>", "Removes the title from the player");
					msg(sender, "create <title>", "Create a custom title, for a price (note color codes are chosen later)");
				}
				if(args[0].equalsIgnoreCase("clear") && sender.hasPermission("title.clear")){
					Player p = (Player)sender;
					activeTitle.put(p.getUniqueId(), "//NONE//");
					p.sendMessage(ChatColor.AQUA + "Title Cleared");
				}
				if((args[0].equalsIgnoreCase("bal") || args[0].equalsIgnoreCase("balance")) && sender.hasPermission("title.balance")){
					Player p = (Player)sender;
					p.sendMessage(ChatColor.AQUA + "You have " + ChatColor.GOLD + points.get(p.getUniqueId()) + ChatColor.AQUA + " title points.");
				}
			}
			if(args.length == 2){
				if(args[0].equalsIgnoreCase("create") && sender.hasPermission("titles.create")){
					Player p = (Player)sender;
					
					if(points.get(p.getUniqueId()) > 0){
						if(ChatColor.stripColor(args[1]).length() <= 10){
							if(!hasTitle(ChatColor.stripColor(args[1]), p)){
								points.put(p.getUniqueId(), points.get(p.getUniqueId())-1);
								playerOwned.get(p.getUniqueId()).add(ChatColor.stripColor(args[1]));
								p.sendMessage(ChatColor.AQUA + "Title \"" + ChatColor.GOLD + ChatColor.stripColor(args[1]) + ChatColor.AQUA + "\" created!");
							}else{
								p.sendMessage(ChatColor.RED + "You already have this title!");
							}
						}else{
							p.sendMessage(ChatColor.RED + "This title is too long!");
						}
						
					}else{
						p.sendMessage(ChatColor.RED + "You can not afford to create a title!");
					}
				}
			}
			if(args.length == 3){
				if(args[0].equalsIgnoreCase("add") && sender.hasPermission("titles.add")){
					Player p = getServer().getPlayer(args[1]);
					if(p != null){
						points.put(p.getUniqueId(), points.get(p.getUniqueId())+Integer.parseInt(args[2]));
						p.sendMessage(ChatColor.AQUA + "You recieved " + ChatColor.GOLD + args[2] + ChatColor.AQUA + " title points");
						sender.sendMessage(ChatColor.AQUA + "Points sent");
					}
				}
				if(args[0].equalsIgnoreCase("give")&& sender.hasPermission("titles.give")){
					Player p = getServer().getPlayer(args[1]);
					if(p != null){
						if(!hasTitle(args[2], p)){
							playerOwned.get(p.getUniqueId()).add(args[2]);
						}else{
							sender.sendMessage(ChatColor.RED + "Already had title");
						}
					}
				}
				if(args[0].equalsIgnoreCase("remove")&&sender.hasPermission("titles.remove")){
					Player p = getServer().getPlayer(args[1]);
					if(p != null){
						remove(args[2], p);
					}
				}
			}
		}

		return true;
	}
	
	public void msg(CommandSender sender, String args, String desc){
		sender.sendMessage(ChatColor.GOLD + "/title " + args + ChatColor.AQUA + " - " + desc);
	}
	
	public void remove(String title, Player p){
		for(int i = 0; i < playerOwned.get(p.getUniqueId()).size(); i++){
			if(playerOwned.get(p.getUniqueId()).get(i).equals(title))
				playerOwned.get(p.getUniqueId()).remove(i);
		}
	}
	
	public boolean hasTitle(String title, Player p){
		for(int i = 0; i < playerOwned.get(p.getUniqueId()).size(); i++){
			if(playerOwned.get(p.getUniqueId()).get(i).equals(title))
				return true;
		} return false;
	}
	
	
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		if(event.getInventory().getName().split(" ")[0].equals("Customize")){
			event.setCancelled(true);
			if(event.getCurrentItem() != null){
				if(event.getCurrentItem().getType() == Material.INK_SACK){
					invert(event.getRawSlot(), (Player) event.getWhoClicked());
				}else if(event.getCurrentItem().getType() == Material.WOOL || event.getCurrentItem().getType() == Material.STONE_BUTTON){
					color.put(event.getWhoClicked().getUniqueId(), ChatColor.valueOf(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())));
				}
				if(event.getCurrentItem().getType() == Material.ARROW){
					String add = ("" + activeTitle.get(event.getWhoClicked().getUniqueId()).charAt(ChatColor.stripColor(typing.get(event.getWhoClicked().getUniqueId())).length())).toLowerCase();
					if(capital.get(event.getWhoClicked().getUniqueId()))
						add = add.toUpperCase();
					String finished = color.get(event.getWhoClicked().getUniqueId()) + "";
					if(bold.get(event.getWhoClicked().getUniqueId()))
						finished += ChatColor.BOLD;
					if(italic.get(event.getWhoClicked().getUniqueId()))
						finished += ChatColor.ITALIC;
					if(underline.get(event.getWhoClicked().getUniqueId()))
						finished += ChatColor.UNDERLINE;
					if(strikethrough.get(event.getWhoClicked().getUniqueId()))
						finished += ChatColor.STRIKETHROUGH;
					finished += add;
					typing.put(event.getWhoClicked().getUniqueId(), typing.get(event.getWhoClicked().getUniqueId()) + finished);

					
				}
				createTitle((Player) event.getWhoClicked());
				if(ChatColor.stripColor(typing.get(event.getWhoClicked().getUniqueId())).length() == (activeTitle.get(event.getWhoClicked().getUniqueId())).length()){
					activeTitle.put(event.getWhoClicked().getUniqueId(), typing.get(event.getWhoClicked().getUniqueId()));
					event.getWhoClicked().sendMessage(ChatColor.AQUA + "Title changed to: " + activeTitle.get(event.getWhoClicked().getUniqueId()));
					capital.remove(event.getWhoClicked().getUniqueId());
					typing.remove(event.getWhoClicked().getUniqueId());
					color.remove(event.getWhoClicked().getUniqueId());
					strikethrough.remove(event.getWhoClicked().getUniqueId());
					underline.remove(event.getWhoClicked().getUniqueId());
					italic.remove(event.getWhoClicked().getUniqueId());
					bold.remove(event.getWhoClicked().getUniqueId());

					Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
						public void run(){
							event.getWhoClicked().closeInventory();
						}
					}, 3);
				}
				
			}
			
		}
		if(event.getInventory().getName().split(" ")[0].equals("Titles")){
			event.setCancelled(true);
			if(event.getCurrentItem() != null && event.getInventory() == event.getWhoClicked().getOpenInventory().getTopInventory() && event.getCurrentItem().getType() == Material.NAME_TAG){
				typing.put(event.getWhoClicked().getUniqueId(), "");
				capital.put(event.getWhoClicked().getUniqueId(), false);
				activeTitle.put(event.getWhoClicked().getUniqueId(), event.getCurrentItem().getItemMeta().getDisplayName());
				color.put(event.getWhoClicked().getUniqueId(), ChatColor.WHITE);
				bold.put(event.getWhoClicked().getUniqueId(), false);
				underline.put(event.getWhoClicked().getUniqueId(), false);
				italic.put(event.getWhoClicked().getUniqueId(), false);
				strikethrough.put(event.getWhoClicked().getUniqueId(), false);

				createTitle((Player) event.getWhoClicked());
			}
			int page = Integer.parseInt(event.getInventory().getName().split(" ")[3]);
			if(event.getRawSlot() == 48 && event.getCurrentItem() != null){
				
				showTitles((Player) event.getWhoClicked(), page-1);
			}
			if(event.getRawSlot() == 50 && event.getCurrentItem() != null){
				
				showTitles((Player) event.getWhoClicked(), page+1);
			}
		}
	}
	
	public void showTitles(Player p, int page){
		Inventory inv = Bukkit.createInventory(null, 54, "Titles - Page " + page);
		for(int i = (page - 1) * 45; i < Math.min(page * 45, playerOwned.get(p.getUniqueId()).size()); i++){
			ItemStack is = new ItemStack(Material.NAME_TAG);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(playerOwned.get(p.getUniqueId()).get(i));
			is.setItemMeta(im);
			inv.setItem(i - (page - 1) * 45, is);
		}
		
		if(page > 1){
			ItemStack arrow = new ItemStack(Material.ARROW);
			ItemMeta im = arrow.getItemMeta();
			im.setDisplayName(ChatColor.WHITE + "Prev Page");
			arrow.setItemMeta(im);
			inv.setItem(48, arrow);
		}
		
		if(playerOwned.get(p.getUniqueId()).size() > 45 * page){
			ItemStack arrow = new ItemStack(Material.ARROW);
			ItemMeta im = arrow.getItemMeta();
			im.setDisplayName(ChatColor.WHITE + "Next Page");
			arrow.setItemMeta(im);
			inv.setItem(50, arrow);
		}
		
	
		
		p.openInventory(inv);
		
	}
	
	public void loadPlayer(Player p){
		reloadConfig();
		List<String> owned = getConfig().getStringList(p.getUniqueId() + ".owned");
		String current = getConfig().getString(p.getUniqueId() + ".active");
		Integer poin = getConfig().getInt(p.getUniqueId() +".points");
		if(owned == null)
			owned = new ArrayList<String>();
		if(current == null)
			current = "//NONE//";
		playerOwned.put(p.getUniqueId(), owned);
		activeTitle.put(p.getUniqueId(), current.replace('&', '§'));
		points.put(p.getUniqueId(), poin);
		capital.put(p.getUniqueId(), false);
	}
	
	public void savePlayer(Player p){
		getConfig().set(p.getUniqueId() + ".owned", playerOwned.get(p.getUniqueId()));
		getConfig().set(p.getUniqueId()+ ".active", activeTitle.get(p.getUniqueId()).replace('§', '&'));
		getConfig().set(p.getUniqueId() +".points", points.get(p.getUniqueId()));
		saveConfig();
	}
	
	public void unloadPlayer(Player p){
		savePlayer(p);
		playerOwned.remove(p.getUniqueId());
		activeTitle.remove(p.getUniqueId());
		points.remove(p.getUniqueId());
		capital.remove(p.getUniqueId());
	}
	
	
	
}
