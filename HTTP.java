public class HTTP {

    private boolean isRequest;
    private boolean isModified;
    private String function;
    private double version;
    private String html;

    public HTTP(boolean isRequest,  String function, double version, String html,boolean isModified) {
        this.isRequest = isRequest;
        this.isModified = isModified;
        this.function = function;
        this.version = version;
        this.html = html;
    }

    public String getRequest(){
        return function + " " +  html + " " + version + " "+ isModified;
    }

    public String getResponse(){
        return function + " " + html + " " + version + " " + isModified;
    }
}
