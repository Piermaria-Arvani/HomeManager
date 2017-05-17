package it.unitn.disi.homemanager;

/**
 * Created by piermaria on 06/05/17.
 */


public class EndPoints {
    public static final String URL_BASE ="http://192.168.43.69/";
    public static final String URL_REGISTER_DEVICE = URL_BASE + "phpScripts/RegisterDevice.php";
    public static final String URL_REGISTER_GROUP = URL_BASE + "phpScripts/InsertGroup.php";
    public static final String URL_JOIN_GROUP = URL_BASE + "phpScripts/JoinGroup.php";
    public static final String URL_GROUP_INFO = URL_BASE + "phpScripts/GetGroupInfo.php";
    public static final String URL_DELETE_DEVICE = URL_BASE + "phpScripts/RemoveDevice.php";
    public static final String URL_CONTROL_LOGIN = URL_BASE + "phpScripts/ControlLogin.php";
    public static final String URL_SEND_SINGLE_PUSH = URL_BASE + "phpScripts/sendSinglePush.php";
    public static final String URL_SEND_MULTIPLE_PUSH = URL_BASE + "phpScripts/sendMultiplePush.php";
    public static final String URL_FETCH_DEVICES = URL_BASE + "192.168.1.28/phpScripts/GetRegisteredDevices.php";
    public static final String URL_FACEBOOK_GRAPH = "https://graph.facebook.com/me?access_token=";
}

