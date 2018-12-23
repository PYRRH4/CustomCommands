package be.pyrrh4.customcommands;

import java.io.File;

import be.pyrrh4.pyrcore.lib.messenger.Text;

public class CCLocale {

	// ----------------------------------------------------------------------
	// Fields
	// ----------------------------------------------------------------------

	public static final File file = new File(CustomCommands.inst().getDataFolder() + "/texts.yml");

	// ----------------------------------------------------------------------
	// Messages
	// ----------------------------------------------------------------------

	public static final Text MSG_CUSTOMCOMMANDS_INVALIDARGUMENTSSIZE = new Text(
			"MSG_CUSTOMCOMMANDS_INVALIDARGUMENTSSIZE", file,
			"en_US", "&6CustomCommands >> &7Invalid text argument size : &c{error}&7.",
			"fr_FR", "&6CustomCommands >> &7Taille de l'argument texte invalide : &c{error}&7.",
			"hu_HU", "&6CustomCommands >> &7Érvénytelen szöveges argumentumméret : &c{error}&7.",
			"pl_PL", "&6CustomCommands >> &7Niepoprawna wielkość argumentu : &c{error}&7."
			);

	public static final Text MSG_CUSTOMCOMMANDS_UNKNOWNCOMMAND = new Text(
			"MSG_CUSTOMCOMMANDS_UNKNOWNCOMMAND", file,
			"en_US", "&6CustomCommands >> &7An error occured while processing your arguments. Maybe you're misusing the command. Closest command :",
			"fr_FR", "&6CustomCommands >> &7Une erreur est apparue lors de l'analyse de vos arguments. Peut-être utilisez vous mal la commande. Commande la plus proche :",
			"hu_HU", "&6CustomCommands >> &7Hiba történt az érvek feldolgozása során. Talán rosszul használja a parancsot. Legközelebbi parancs :",
			"pl_PL", "&6CustomCommands >> &7Wystąpił błąd podczas przetwarzania argumentów. Być może jest to niewłaściwa komenda. Najbardziej podobna komenda :"
			);

	public static final Text MSG_CUSTOMCOMMANDS_CLOSESTCOMMAND = new Text(
			"MSG_CUSTOMCOMMANDS_CLOSESTCOMMAND", file,
			"en_US", "&c{command}"
			);

	public static final Text MSG_CUSTOMCOMMANDS_UNAUTHORIZEDWORLD = new Text(
			"MSG_CUSTOMCOMMANDS_UNAUTHORIZEDWORLD", file,
			"en_US", "&6CustomCommands >> &7This command isn't enabled in this world.",
			"fr_FR", "&6CustomCommands >> &7Cette commande n'est pas activée dans ce monde.",
			"hu_HU", "&6CustomCommands >> &7Ez a parancs nem engedélyezett ebben a világban."
			);

	public static final Text MSG_CUSTOMCOMMANDS_ITEMSAVE = new Text(
			"MSG_CUSTOMCOMMANDS_ITEMSAVE", file,
			"en_US", "&6CustomCommands >> &7Item was saved with name &a{name}&7.",
			"fr_FR", "&6CustomCommands >> &7L'item a été sauvegardé sous le nom &a{name}&7.",
			"hu_HU", "&6CustomCommands >> &7Ez az elem mentett névvel &a{name}&7."
			);

	public static final Text MSG_CUSTOMCOMMANDS_LOCATIONSAVE = new Text(
			"MSG_CUSTOMCOMMANDS_LOCATIONSAVE", file,
			"en_US", "&6CustomCommands >> &7Location was saved with name &a{name}&7.",
			"fr_FR", "&6CustomCommands >> &7L'endroit a été sauvegardé sous le nom &a{name}&7.",
			"hu_HU", "&6CustomCommands >> &7Ez a hely neve mentett el &a{name}&7."
			);

}
