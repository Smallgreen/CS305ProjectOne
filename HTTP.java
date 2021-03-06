public class HTTP {

    private boolean isRequest;
    private int isModified;
    private String function;
    private double version;
    private String html;

    public HTTP(){}

    public HTTP(boolean isRequest,  String function, double version, String html,int isModified) {
        this.isRequest = isRequest;
        this.isModified = isModified;
        this.function = function;
        this.version = version;
        this.html = html;
    }

    public String getRequest(){
        //System.out.println("req "+html);
        return function + "@" + version + "@" + html + "@"+ isModified;

    }

    public String getResponse(){
        //System.out.println("resp "+html);
        return function + "@" + html + "@" + version + "@" + isModified;

    }
}
