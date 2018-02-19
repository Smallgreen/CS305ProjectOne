public class HTTP {

    private boolean isRequest;
    private String function;
    private double version;
    private String html;

    public HTTP(boolean request, String function, double version, String html) {
        isRequest = request;
        this.function = function;
        this.version = version;
        this.html = html;
    }

    public String getRequest(){
        return function + " " +  html + " " + version;
    }

    public String getResponse(){
        return null;
    }
}
