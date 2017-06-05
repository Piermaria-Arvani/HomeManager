package it.unitn.disi.homemanager;

/**
 * Created by piermaria on 06/05/17.
 */


public class EndPoints {
    public static final String URL_local ="http://192.168.43.69/";
    public static final String URL_heroku ="https://enigmatic-cliffs-40005.herokuapp.com/";
    public static final String URL_BASE =URL_heroku;
    public static final String URL_REGISTER_DEVICE = URL_BASE + "phpScripts/RegisterDevice.php";
    public static final String URL_REGISTER_GROUP = URL_BASE + "phpScripts/InsertGroup.php";
    public static final String URL_STORE_TOKEN = URL_BASE + "phpScripts/StoreToken.php";
    public static final String URL_INSERT_EVENT = URL_BASE + "phpScripts/InsertEvent.php";
    public static final String URL_INSERT_ITEM = URL_BASE + "phpScripts/InsertItemInShoppingList.php";
    public static final String URL_INSERT_CLEANING_ROUND = URL_BASE + "phpScripts/InsertCleaningRound.php";
    public static final String URL_INSERT_CONTACT = URL_BASE + "phpScripts/InsertContactNumber.php";
    public static final String URL_INSERT_EXPENSE = URL_BASE + "phpScripts/InsertExpense.php";
    public static final String URL_SET_CLEANING_ROUND_DONE = URL_BASE + "phpScripts/SetCleanRound.php";
    public static final String URL_JOIN_GROUP = URL_BASE + "phpScripts/JoinGroup.php";
    public static final String URL_GROUP_INFO = URL_BASE + "phpScripts/GetGroupInfo.php";
    public static final String URL_GROUP_EVENTS = URL_BASE + "phpScripts/GetGroupEvents.php";
    public static final String URL_GROUP_SHOPPINGLIST = URL_BASE + "phpScripts/GetShoppingList.php";
    public static final String URL_GROUP_CLEANINGROUND = URL_BASE + "phpScripts/GetGroupCleaningRound.php";
    public static final String URL_GROUP_CONTACTSLIST = URL_BASE + "phpScripts/GetGroupContacts.php";
    public static final String URL_GROUP_EXPENSES = URL_BASE + "phpScripts/GetGroupExpenses.php";
    public static final String URL_GROUP_DEBITS = URL_BASE + "phpScripts/GetGroupDebits.php";
    public static final String URL_GROUP_USERS = URL_BASE + "phpScripts/getGroupUsers.php";
    public static final String URL_DELETE_DEVICE = URL_BASE + "phpScripts/RemoveDevice.php";
    public static final String URL_DELETE_ITEM = URL_BASE + "phpScripts/RemoveItem.php";
    public static final String URL_UPDATE_DEBITS = URL_BASE + "phpScripts/UpdateDebits.php";
    public static final String URL_CONTROL_LOGIN = URL_BASE + "phpScripts/ControlLogin.php";
    public static final String URL_SEND_SINGLE_PUSH = URL_BASE + "phpScripts/sendSinglePush.php";
    public static final String URL_FACEBOOK_GRAPH = "https://graph.facebook.com/me?access_token=";
}

